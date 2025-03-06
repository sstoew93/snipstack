package org.example.final_project.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    @NotNull(message = "Username cannot be blank!")
    @Size(min = 6, max = 15, message = "Username must be 6-10 characters long!")
    private String username;

    @NotNull(message = "Username cannot be blank!")
    @Size(min = 6, max = 15, message = "Password must be 6-10 characters long!")
    private String password;

}
