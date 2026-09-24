package hu.retronet.mc.common;

import hu.retronet.mc.common.entity.AuthSession;
import hu.retronet.mc.common.entity.model.SessionStatus;
import hu.retronet.mc.common.repository.AuthSessionRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

public class BaseAuthServerController {

    protected final AuthSessionRepository authSessionRepository;

    protected final TransactionTemplate transactionTemplate;

    public BaseAuthServerController(AuthSessionRepository authSessionRepository, TransactionTemplate transactionTemplate) {
        this.authSessionRepository = authSessionRepository;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * @param session Current session
     * @return True if the session is valid. When is invalid, it updates the record status to indicate it and then returns false.
     */
    protected boolean isValidSession(AuthSession session) {
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            transactionTemplate.executeWithoutResult(status -> authSessionRepository.updateStatus(session.getAccessToken(), session.getClientToken(), SessionStatus.INVALID));
            return false;
        }
        return true;
    }

}