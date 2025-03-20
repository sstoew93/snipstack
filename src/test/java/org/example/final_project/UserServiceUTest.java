package org.example.final_project;

import org.example.final_project.user.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.final_project.client.EmailServiceClient;
import org.example.final_project.exception.*;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.user.repository.UserRepository;
import org.example.final_project.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailServiceClient emailServiceClient;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .username("sstoew")
                .password("123123")
                .email("email@email.email")
                .isActive(true)
                .role(Role.USER)
                .createdOn(LocalDateTime.now())
                .avatar("httP://avatar.com")
                .build();
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        RegisterUser registerUser = RegisterUser.builder()
                .username("sstoew")
                .build();

        when(userRepository.findByUsername("sstoew")).thenReturn(Optional.of(testUser));

        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerUser));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldSaveUser_WhenValid() {
        RegisterUser registerUser = RegisterUser.builder()
                .username("sstoew")
                .email("email@email.email")
                .password("password")
                .build();

        when(userRepository.findByUsername("sstoew")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("email@email.email")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.register(registerUser);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User user = userService.findById(userId);

        assertNotNull(user);
        assertEquals("sstoew", user.getUsername());
    }

    @Test
    void findById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void updateUserRole_ShouldUpdateRole_WhenUserExists() {
        when(userRepository.findByUsername("sstoew")).thenReturn(Optional.of(testUser));

        userService.updateUserRole("sstoew", "ADMIN");

        assertEquals(Role.ADMIN, testUser.getRole());
        verify(userRepository).save(testUser);
    }

    @Test
    void banUser_ShouldSetUserInactiveAndSendNotification() {
        when(userRepository.findByUsername("sstoew")).thenReturn(Optional.of(testUser));

        userService.banUser("sstoew");

        assertFalse(testUser.getIsActive());
        assertEquals(Role.USER, testUser.getRole());
        verify(emailServiceClient).sendBanNotification(any());
        verify(userRepository).save(testUser);
    }

    @Test
    void unbanUser_ShouldSetUserActiveAndSendNotification() {
        testUser.setIsActive(false);
        when(userRepository.findByUsername("sstoew")).thenReturn(Optional.of(testUser));

        userService.unbanUser("sstoew");

        assertTrue(testUser.getIsActive());
        verify(emailServiceClient).sendUnbanNotification(any());
        verify(userRepository).save(testUser);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("sstoew")).thenReturn(Optional.of(testUser));

        User user = userService.findByUsername("sstoew");

        assertNotNull(user);
        assertEquals("sstoew", user.getUsername());
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("noUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("noUser"));
    }

    @Test
    void updateProfile_ShouldUpdateProfile_WhenUserExists() {
        EditUserProfile editUserProfile = EditUserProfile.builder()
                .firstName("Stanimir")
                .lastName("Stoew")
                .avatar("https://avatar.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        userService.updateProfile(userId, editUserProfile);

        assertEquals("Stanimir", testUser.getFirstName());
        assertEquals("Stoew", testUser.getLastName());
        assertEquals("https://avatar.com", testUser.getAvatar());
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIncorrect() {
        UserChangePassword changePassword = UserChangePassword.builder()
                .newPassword("newpass")
                .oldPassword("dadadadada")
                .confirmPassword("newpass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThrows(UserPasswordNotMatch.class, () -> userService.changePassword(userId, changePassword));
    }

    @Test
    void changePassword_ShouldThrowException_WhenPasswordsDoNotMatch() {
        UserChangePassword changePassword = UserChangePassword.builder()
                .newPassword("dadadada")
                .oldPassword("123123")
                .confirmPassword("dadadada2")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThrows(UserPasswordNotMatch.class, () -> userService.changePassword(userId, changePassword));
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenValid() {
        UserChangePassword changePassword = UserChangePassword.builder()
                .newPassword("123456")
                .oldPassword("123123")
                .confirmPassword("123456")
                .build();

        testUser.setPassword(passwordEncoder.encode("123123"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("123123", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("123456")).thenReturn("123456");

        userService.changePassword(userId, changePassword);

        assertEquals("123456", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }


    @Test
    void loadUserByUsername_ShouldReturnAuthenticationDetails() {
        when(userRepository.findByUsername("sstoew")).thenReturn(Optional.of(testUser));

        AuthenticationDetails details = (AuthenticationDetails) userService.loadUserByUsername("sstoew");

        assertNotNull(details);
        assertEquals("sstoew", details.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("noUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("noUser"));
    }

    @Test
    void findAll_ShouldReturnListOfUsers() {
        List<User> users = List.of(
                User.builder().id(UUID.randomUUID()).username(testUser.getUsername()).email("email@email.email").build(),
                User.builder().id(UUID.randomUUID()).username("user2").email("email2@email.email").build()
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("sstoew", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void register_ShouldThrowEmailAlreadyExistException_WhenEmailExists() {

        RegisterUser registerUser = RegisterUser.builder()
                .email("email@email.email")
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(EmailAlreadyExistException.class, () -> userService.register(registerUser));
    }

    @Test
    void changePassword_ShouldThrow_WhenPasswordsDoNotMatch() {
        UserChangePassword userChangePassword= UserChangePassword.builder()
                .oldPassword("123123")
                .confirmPassword("123456")
                .newPassword("1234567")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("123123", testUser.getPassword())).thenReturn(true);

        assertThrows(UserPasswordNotMatch.class, () -> userService.changePassword(userId, userChangePassword));
    }

    @Test
    void getAll_ShouldReturnCountOfUsers() {
        long users = 3;

        when(userRepository.count()).thenReturn(users);

        long result = userService.getAll();

        assertEquals(users, result);
    }
}
