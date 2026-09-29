package hu.retronet.mc.portal.controller;

import com.czompi.mcservicessdk.UserAuthentication;
import com.czompi.mcservicessdk.model.MinecraftAccount;
import com.czompi.mcservicessdk.model.MsaTokenResponse;
import hu.retronet.mc.common.entity.MsaSession;
import hu.retronet.mc.common.entity.User;
import hu.retronet.mc.common.model.ErrorResponse;
import hu.retronet.mc.common.repository.GameSessionRepository;
import hu.retronet.mc.common.repository.MsaSessionRepository;
import hu.retronet.mc.common.repository.UserRepository;
import hu.retronet.mc.portal.exceptions.SessionStateException;
import hu.retronet.mc.portal.utils.PKCEChallenge;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

import static com.czompi.mcservicessdk.utils.MSAConstants.CLIENT_ID;
import static com.czompi.mcservicessdk.utils.MSAConstants.REDIRECT_URI;
import static java.text.MessageFormat.format;

@Slf4j
@Controller
@RequestMapping(path = "/auth")
public class MsaConnectionController {

    private final String MSA_AUTH_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize?" +
            "client_id=" + CLIENT_ID +
            "&prompt=select_account" +
            "&response_type=code" +
            "&redirect_uri=" + REDIRECT_URI +
            "&response_mode=query" +
            "&code_challenge={0}" +
            "&code_challenge_method=S256" +
            "&scope=XboxLive.signin%20offline_access" +
            "&state={1}";
    private final UserRepository userRepository;
    private final GameSessionRepository gameSessionRepository;
    private final MsaSessionRepository msaSessionRepository;
    private final TransactionTemplate transactionTemplate;

    public MsaConnectionController(UserRepository userRepository, GameSessionRepository gameSessionRepository, MsaSessionRepository msaSessionRepository, TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.msaSessionRepository = msaSessionRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @GetMapping(path = "/login")
    public RedirectView login(HttpServletRequest request, HttpSession session) {
        if (session.getAttribute("access_token") != null) {
            log.debug("User already logged in, redirecting to home page");
            RedirectView redirectView = new RedirectView();
            redirectView.setServletContext(request.getServletContext());
            redirectView.setUrl("/");
            return redirectView;
        }

        log.debug("Initiating login process...");

        PKCEChallenge codeChallenge = PKCEChallenge.generate();
        session.setAttribute("code_verifier", codeChallenge.getCodeVerifier());
        String state = PKCEChallenge.generateCodeVerifier();
        session.setAttribute("oauth_state", state);

        String authUrl = format(MSA_AUTH_URL, codeChallenge.getCodeChallenge(), state);

        RedirectView redirectView = new RedirectView();
        redirectView.setServletContext(request.getServletContext());
        redirectView.setUrl(authUrl);
        return redirectView;
    }

    @GetMapping(path = "/callback")
    public String callback(@RequestParam("code") String code, @RequestParam("state") String returnedState,
                           HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String state = getSessionAttribute(session, "oauth_state", "Missing session state token");
            if (!Objects.equals(state, returnedState)) {
                throw new SessionStateException("Session state mismatch! Try logging in later");
            }
            String codeVerifier = getSessionAttribute(session, "code_verifier", "Invalid session state");
            MsaTokenResponse msaTokenResponse = UserAuthentication.obtainMsaToken(code, codeVerifier);
            return verify(request, session, redirectAttributes, msaTokenResponse);
        } catch (Exception e) {
            log.error("Error occurred during callback processing", e);
            return invalidateSessionWithError(session, redirectAttributes, e);
        }
    }

    private static String getSessionAttribute(HttpSession session, String attributeName, String errorMessage) throws SessionStateException {
        String value = (String) session.getAttribute(attributeName);
        if (value == null) {
            log.error("Session attribute is missing: {}", attributeName);
            throw new SessionStateException(errorMessage);
        }
        return value;
    }

    @GetMapping(path = "/refresh")
    public String refresh(HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            getSessionAttribute(session, "access_token", "Missing access token");
            String refreshToken = getSessionAttribute(session, "refresh_token", "Missing refresh token");
            getSessionAttribute(session, "profile", "Missing profile");

            MsaTokenResponse msaTokenResponse = UserAuthentication.refreshMsaToken(refreshToken);

            return verify(request, session, redirectAttributes, msaTokenResponse);
        } catch (Exception e) {
            log.error("Error occurred during token refresh", e);
            return invalidateSessionWithError(session, redirectAttributes, e);
        }
    }

    private String verify(HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes, MsaTokenResponse msaTokenResponse) {
        try {
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("access_token", msaTokenResponse.getAccessToken());
            session.setAttribute("refresh_token", msaTokenResponse.getRefreshToken());
            MinecraftAccount profile = UserAuthentication.obtainGameProfile(msaTokenResponse);
            session.setAttribute("profile", profile);
            log.debug("User {} logged in successfully.", profile.getId());
            if (userRepository.findByUuid(profile.getId()).isEmpty()) {
                User user = new User();
                user.setUuid(profile.getId());
                String emailDomain = System.getenv("PROFILE_DUMMY_EMAIL_DOMAIN");
                if (emailDomain == null || emailDomain.isEmpty()) {
                    emailDomain = "dummy.example.com"; // Default domain if not set
                }
                log.debug("User {} logged in for the first time, creating new account.", profile.getId());
                String email = profile.getName() + profile.getId().toString().substring(0, 4) + "@" + emailDomain;
                email = email.toLowerCase(Locale.ROOT);
                user.setEmail(email);
                userRepository.save(user);
            }
            MsaSession msaSession = new MsaSession();
            msaSession.setUser(userRepository.findByUuid(profile.getId()).orElseThrow(() -> new RuntimeException("User not found after creation")));
            msaSession.setRefreshToken(msaTokenResponse.getRefreshToken());
            msaSession.setCreatedAt(msaTokenResponse.getGeneratedAt());
            msaSession.setExpiresAt(msaTokenResponse.getNextRefreshTokenRequestAt());
            transactionTemplate.execute(status -> msaSessionRepository.save(msaSession));
            return "redirect:/";
        } catch (Exception e) {
            log.warn("Error occurred during token verification or profile retrieval", e);
            return invalidateSessionWithError(session, redirectAttributes, e);
        }
    }

    private String invalidateSessionWithError(HttpSession session, RedirectAttributes redirectAttributes, Exception e) {
        ErrorResponse error = new ErrorResponse(e.getClass().getSimpleName(),
                e.getCause() == null ? null : e.getCause().getMessage(), e.getMessage(), LocalDateTime.now());
        session.invalidate();
        redirectAttributes.addFlashAttribute("error", error);
        redirectAttributes.addFlashAttribute("error_origin", this.getClass());
        return "redirect:/error";
    }

}
