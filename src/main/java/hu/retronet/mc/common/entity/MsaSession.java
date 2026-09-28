package hu.retronet.mc.common.entity;

import hu.retronet.mc.common.entity.model.SessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "msa_sessions")
public class MsaSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.VALID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id", // This matches the Liquibase column in textures
            referencedColumnName = "id", // This matches the primary key in profiles
            foreignKey = @ForeignKey(name = "fk_msa_sessions_user")
    )
    private User user;

    @Column(name = "xbox_user_id", unique = true, nullable = false)
    private String xboxUserId;

    @Column(name = "refresh_token", unique = true, nullable = false)
    private String refreshToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

}
