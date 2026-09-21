package hu.retronet.mc.portal.controller;

import hu.retronet.mc.common.entity.User;
import hu.retronet.mc.common.model.ErrorResponse;
import hu.retronet.mc.common.repository.UserRepository;
import hu.retronet.mc.portal.exceptions.SessionStateException;
import hu.retronet.mc.portal.model.MinecraftAccount;
import hu.retronet.mc.portal.model.MsaTokenResponse;
import hu.retronet.mc.portal.utils.UserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.Objects;

import static hu.retronet.mc.portal.utils.MSAConstants.CLIENT_ID;
import static hu.retronet.mc.portal.utils.MSAConstants.REDIRECT_URI;

@Slf4j
@Controller
@RequestMapping(path = "/auth")
public class AuthenticationController {

    private final UserRepository userRepository;

    public AuthenticationController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        

        String authUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize?" +
                "client_id=" + CLIENT_ID +
                "&prompt=select_account" +
                "&response_type=code" +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_mode=query" +
                "&code_challenge=" + codeChallenge.getCodeChallenge() +
                "&code_challenge_method=S256" +
                "&scope=XboxLive.signin%20offline_access" +
                "&state=" + state;

        RedirectView redirectView = new RedirectView();
        redirectView.setServletContext(request.getServletContext());
        redirectView.setUrl(authUrl);
        return redirectView;
    }

    @GetMapping(path = "/callback")
    public String callback(@RequestParam("code") String code, @RequestParam("state") String returnedState,
                           HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        try {
            String state = getSessionAttribute(session, "oauth_state", "Missing session state token");
            if (!Objects.equals(state, returnedState)) {
                throw new SessionStateException("Session state mismatch! Try logging in later");
            }
            String codeVerifier = getSessionAttribute(session, "code_verifier", "Invalid session state");
            MsaTokenResponse msaTokenResponse = UserAuthentication.obtainMsaToken(code, codeVerifier);
            return verify(request, session, redirectAttributes, model, msaTokenResponse);
        } catch (Exception e) {
            log.error("Error occurred during callback processing", e);
            return invalidateSessionWithError(session, redirectAttributes, redirectAttributes, e);
        }
    }

    private static String getSessionAttribute(HttpSession session, String attributeName, String errorMessage) throws SessionStateException {
        String value = (String) session.getAttribute(attributeName);
        if (value == null) {
            throw new SessionStateException(errorMessage);
        }
        return value;
    }

    @GetMapping(path = "/refresh")
    public String refresh(HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        try {
            getSessionAttribute(session, "access_token", "Missing access token");
            String refreshToken = getSessionAttribute(session, "refresh_token", "Missing refresh token");
            getSessionAttribute(session, "profile", "Missing profile");

            MsaTokenResponse msaTokenResponse = UserAuthentication.refreshMsaToken(refreshToken);

            return verify(request, session, redirectAttributes, model, msaTokenResponse);
        } catch (Exception e) {
            return invalidateSessionWithError(session, redirectAttributes, redirectAttributes, e);
        }
    }

    private String verify(HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes, Model model, MsaTokenResponse msaTokenResponse) {
        try {
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("access_token", msaTokenResponse.getAccessToken());
            session.setAttribute("refresh_token", msaTokenResponse.getRefreshToken());
            MinecraftAccount profile = UserAuthentication.obtainGameProfile(msaTokenResponse);
            session.setAttribute("profile", profile);
            if(userRepository.findByUuid(profile.getId()).isEmpty()) {
                User user = new User();
                user.setUuid(profile.getId());
                user.setUsername(profile.getName());
                String emailDomain = System.getenv("PROFILE_DUMMY_EMAIL_DOMAIN");
                if (emailDomain == null || emailDomain.isEmpty()) {
                    emailDomain = "dummy.example.com"; // Default domain if not set
                }
                var email = profile.getName() + "@" + emailDomain;
                user.setEmail(email);
                userRepository.save(user);
            }
            return "redirect:/";
        } catch (Exception e) {
            return invalidateSessionWithError(session, model, redirectAttributes, e);
        }
    }

    private String invalidateSessionWithError(HttpSession session, Model model, RedirectAttributes redirectAttributes, Exception e) {
        ErrorResponse error = new ErrorResponse(e.getClass().getSimpleName(),
                e.getCause() == null ? null : e.getCause().getMessage(), e.getMessage(), LocalDateTime.now());
        session.invalidate();
        redirectAttributes.addFlashAttribute("error", error);
        redirectAttributes.addFlashAttribute("error_origin", this.getClass());
        return "redirect:/error";
    }

}
