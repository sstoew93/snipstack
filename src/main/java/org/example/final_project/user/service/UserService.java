package org.example.final_project.user.service;

import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.final_project.exception.DomainException;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.user.repository.UserRepository;
import org.example.final_project.web.dto.EditUserProfile;
import org.example.final_project.web.dto.RegisterUser;
import org.example.final_project.web.dto.UserChangePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Builder
@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterUser registerUser) {
        Optional<User> byUsername = userRepository.findByUsername(registerUser.getUsername());
        Optional<User> byEmail = userRepository.findByEmail(registerUser.getEmail());

        if (byUsername.isPresent()) {
            throw new DomainException("Username already in use!");
        }

        if (byEmail.isPresent()) {
            throw new DomainException("Email already in use!");
        }

        User user = createUser(registerUser);

        userRepository.save(user);

        log.info("Successfully created user %s".formatted(user.getUsername()));

    }

    private User createUser(RegisterUser registerUser) {
        return User.builder()
                .username(registerUser.getUsername())
                .password(passwordEncoder.encode(registerUser.getPassword()))
                .email(registerUser.getEmail())
                .isActive(true)
                .role(Role.USER)
                .createdOn(LocalDateTime.now())
                .avatar("/images/default-avatar.png")
                .build();
    }

    public void updateUserRole(String username, String role) {
        User user = findByUsername(username);

        if (user != null) {
            user.setRole(Role.valueOf(role));
            userRepository.save(user);
            log.info("Successfully updated role for user %s".formatted(user.getUsername()));
        }

    }

    public void banUser(String username) {
        Optional<User> byUsername = this.userRepository.findByUsername(username);

        if (byUsername.isPresent()) {
            User user = byUsername.get();
            user.setIsActive(false);
            user.setRole(Role.USER);
            userRepository.save(user);
            log.info("Successfully banned user %s".formatted(user.getUsername()));
        }
    }

    public void unbanUser(String username) {
        Optional<User> byUsername = this.userRepository.findByUsername(username);

        if (byUsername.isPresent()) {
            User user = byUsername.get();
            user.setIsActive(true);
            userRepository.save(user);
            log.info("Successfully unbanned user %s".formatted(user.getUsername()));
        }

    }

    public User findById(UUID uuid) {

        Optional<User> byId = userRepository.findById(uuid);

        return byId.orElseThrow(() -> new DomainException("User not found!"));
    }

    public int getAll() {
        return (int) userRepository.count();
    }


    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public User findByUsername(String username) {
        Optional<User> user = this.userRepository.findByUsername(username);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new DomainException("User not found!");
        }

    }

    public List<User> findBannedUsers() {
        return this.userRepository.findAllByIsActive(Boolean.FALSE);
    }

    public void updateProfile(UUID userId, EditUserProfile editUserProfile) {
        User user = findById(userId);

        user.setFirstName(editUserProfile.getFirstName());
        user.setLastName(editUserProfile.getLastName());
        user.setAvatar(editUserProfile.getAvatar());

        this.userRepository.save(user);
    }

    public void changePassword(UUID id, UserChangePassword changePassword) {
        User user = findById(id);

        if (!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new DomainException("Old password incorrect!");
        }

        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new DomainException("Passwords do not match!");
        }

        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        int notificationsUnread = user.getNotifications().stream()
                .filter(notification -> !notification.isRead())
                .toList()
                .size();

        return new AuthenticationDetails(user.getId(), user.getUsername(), user.getPassword(), user.getAvatar(), user.getIsActive(), user.getRole(), notificationsUnread);
    }

    public List<User> getUsersRegisteredInLast24Hours() {
        LocalDateTime lastDayUsers = LocalDateTime.now().minusDays(1);
        return this.userRepository.findAllByCreatedOnAfter(lastDayUsers);
    }
}
