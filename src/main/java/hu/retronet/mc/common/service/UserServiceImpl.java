package hu.retronet.mc.common.service;

import hu.retronet.mc.common.entity.User;
import hu.retronet.mc.common.exceptions.InvalidCredentialsException;
import hu.retronet.mc.common.repository.GameSessionRepository;
import hu.retronet.mc.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameSessionRepository sessionRepository;

    @Override
    public User save(User person) {
        return userRepository.save(person);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User login(String email, String password) throws InvalidCredentialsException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new InvalidCredentialsException();
        }
        return user.get();
    }

    @Override
    public boolean logout(String email, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            return false;
        }

        int rowsAffected = sessionRepository.findByEmail(email).map(sessionRepository::invalidate).orElse(0);
        return true;
    }

    @Override
    public boolean updatePasswordByUuid(UUID uuid, String newPassword) {
        return userRepository.updatePasswordByUuid(uuid, newPassword);
    }

}
