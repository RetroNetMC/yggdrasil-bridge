package hu.retronet.mc.common.repository;

import hu.retronet.mc.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * @param email E-mail address of user to be found
     * @return The player credentials. When no player present with said username, it returns Optional.empty()
     */
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Find a user by their UUID.
     * @param uuid The UUID of the user to find.
     * @return The user, or Optional.empty() if no user is found.
     */
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.uuid = :uuid")
    Optional<User> findByUuid(@Param("uuid") UUID uuid);

    @Modifying
    @Transactional
    @Query("UPDATE User u " +
            "SET u.password = :new_password " +
            "WHERE u.uuid = :uuid")
    boolean updatePasswordByUuid(@Param("uuid") UUID uuid, @Param("new_password") String newPassword);

}
