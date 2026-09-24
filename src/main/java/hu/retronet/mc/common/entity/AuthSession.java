package hu.retronet.mc.common.entity;

import hu.retronet.mc.common.entity.model.SessionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "${AUTH_SERVER_TABLE_AUTH_SESSIONS}")
public class AuthSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private SessionStatus status = SessionStatus.VALID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id", // This matches the Liquibase column in textures
            referencedColumnName = "id", // This matches the primary key in profiles
            foreignKey = @ForeignKey(name = "fk_auth_sessions_user")
    )
    private User user;

    private String accessToken;

    private String clientToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
//    @PreUpdate
    public void updateExpiresAt() {
        this.expiresAt = LocalDateTime.now().plusDays(1);
    }
}
