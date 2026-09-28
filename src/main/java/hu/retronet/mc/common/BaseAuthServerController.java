package hu.retronet.mc.common;

import hu.retronet.mc.common.entity.GameSession;
import hu.retronet.mc.common.entity.model.SessionStatus;
import hu.retronet.mc.common.repository.GameSessionRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

public class BaseAuthServerController {

    protected final GameSessionRepository gameSessionRepository;

    protected final TransactionTemplate transactionTemplate;

    public BaseAuthServerController(GameSessionRepository GameSessionRepository, TransactionTemplate transactionTemplate) {
        this.gameSessionRepository = GameSessionRepository;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * @param session Current session
     * @return True if the session is valid. When is invalid, it updates the record status to indicate it and then returns false.
     */
    protected boolean isValidSession(GameSession session) {
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            transactionTemplate.executeWithoutResult(status -> gameSessionRepository.updateStatus(session.getAccessToken(), session.getClientToken(), SessionStatus.INVALID));
            return false;
        }
        return true;
    }

}