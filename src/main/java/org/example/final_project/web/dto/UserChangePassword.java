package org.example.final_project.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserChangePassword {

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long!")
    private String oldPassword;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long!")
    private String newPassword;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long!")
    private String confirmPassword;
}
