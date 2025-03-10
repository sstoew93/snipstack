package org.example.final_project.user.service;

import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserInit implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInit(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {

        if (userService.getAll() == 0) {

            User admin = User.builder()
                    .username("administrator")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin@snipstack.com")
                    .isActive(true)
                    .role(Role.ADMIN)
                    .avatar("/images/default-avatar.png")
                    .createdOn(LocalDateTime.now())
                    .build();

            userService.initializeAdmin(admin);
        }


    }
}
