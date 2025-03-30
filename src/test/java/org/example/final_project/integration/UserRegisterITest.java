package org.example.final_project.integration;

import io.micrometer.core.instrument.config.validate.ValidationException;
import jakarta.validation.ConstraintViolationException;
import org.example.final_project.user.model.User;
import org.example.final_project.user.repository.UserRepository;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.RegisterUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class UserRegisterITest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUser_success() {
        RegisterUser registerUser = RegisterUser.builder()
                .username("sstoew93")
                .email("sstoew93@snipstack.com")
                .password("123456")
                .terms(true)
                .build();

        userService.register(registerUser);

        Optional<User> savedUser = userRepository.findByUsername("sstoew93");
        assertTrue(savedUser.isPresent());
        assertEquals("sstoew93@snipstack.com", savedUser.get().getEmail());
        assertEquals("sstoew93", savedUser.get().getUsername());
        assertEquals(true, savedUser.get().getIsActive());
        assertTrue(passwordEncoder.matches("123456", savedUser.get().getPassword()));
        assertEquals(userRepository.findByUsername("sstoew93").get().getUsername(), savedUser.get().getUsername());
        assertEquals(userRepository.findByEmail("sstoew93@snipstack.com").get().getEmail(), savedUser.get().getEmail());
        assertFalse(userRepository.findAll().isEmpty());

    }

}
