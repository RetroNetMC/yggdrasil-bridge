package hu.retronet.mc.common.repository;

import hu.retronet.mc.common.entity.AuthSession;
import hu.retronet.mc.common.entity.model.SessionStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByClientToken(String clientToken);

    @Query("SELECT s " +
                "FROM AuthSession s " +
            "WHERE s.user.email = :email " +
            "  AND s.clientToken = :client_token")
    Optional<AuthSession> findByEmailAndClientToken(@Param("email") String email, @Param("client_token") String clientToken);

    @Query("SELECT s " +
            "FROM AuthSession s " +
            "WHERE s.accessToken = :access_token " +
            "  AND s.clientToken = :client_token")
    Optional<AuthSession> findByAccessTokenAndClientToken(@Param("access_token") String accessToken, @Param("client_token") String clientToken);

    @Modifying
    @Transactional
    @Query("UPDATE AuthSession s " +
            "SET s.accessToken = :access_token " +
            "WHERE s.user.uuid = :user_id" +
            "  AND s.clientToken = :client_token")
    int updateAccessToken(@Param("user_id") UUID userId, @Param("client_token") String clientToken, @Param("access_token")  String newAccessToken);

    @Modifying
    @Transactional
    @Query("UPDATE AuthSession s " +
            "SET s.status = :session_status " +
            "WHERE s.accessToken = :access_token" +
            "  AND s.clientToken = :client_token")
    int updateStatus(@Param("access_token") String accessToken, @Param("client_token") String clientToken, @Param("session_status") SessionStatus newStatus);


    @Query("SELECT s " +
            "FROM AuthSession s " +
            "WHERE s.user.id = :user_id")
    List<AuthSession> findByUserId(@Param("user_id") Long userId);

    @Query("SELECT s " +
            "FROM AuthSession s " +
            "WHERE s.user.id = :user_id")
    Stream<AuthSession> streamAllByUserId(@Param("user_id") Long userId);

    @Query("SELECT s " +
            "FROM AuthSession s " +
            "WHERE s.user.email = :email")
    Optional<AuthSession> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE AuthSession s " +
            "SET s.status = SessionStatus.INVALID " +
            "WHERE s = :session")
    int invalidate(AuthSession session);

}
