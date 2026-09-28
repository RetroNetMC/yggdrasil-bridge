package hu.retronet.mc.common.service;

import hu.retronet.mc.common.entity.User;
import hu.retronet.mc.common.exceptions.InvalidCredentialsException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User save(User person);

    Optional<User> findByUuid(UUID uuid);

    Optional<User> findByEmail(String email);

    User login(String email, String password) throws InvalidCredentialsException;

    boolean logout(String email, String password);

//    void delete(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Session s " +
            "SET s.user.password = :new_password " +
            "WHERE s.user.uuid = :uuid")
    boolean updatePasswordByUuid(UUID uuid, String newPassword);
}
