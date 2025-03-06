package org.example.final_project.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUser {

    @NotNull
    @Size(min = 6, max = 15, message = "Username must be 6-10 characters long!")
    private String username;

    @NotNull
    @Size(min = 6, max = 15, message = "Password must be 6-10 characters long!")
    private String password;

    @NotNull
    @Email(message = "Enter valid email address!")
    private String email;

    @NotNull
    private boolean terms;

    public boolean isTermsAccepted() {
        return terms;
    }

    public void setTermsAccepted(boolean terms) {
        this.terms = terms;
    }

}
