package org.example.final_project.user.service;

import org.example.final_project.web.dto.RegisterUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserInit implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public UserInit(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void run(String... args) {

        if (userService.getAll() == 0) {
            RegisterUser registerUser = RegisterUser.builder()
                    .username("administrator")
                    .password("123456")
                    .email("admin@snipstack.com")
                    .build();

            userService.register(registerUser);
        }


    }
}
