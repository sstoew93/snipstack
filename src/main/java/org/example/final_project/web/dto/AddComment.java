package org.example.final_project.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddComment {

    private String blockCode;

    @Size(min = 10, message = "Reply must be at least 10 characters long!")
    @NotBlank(message = "Reply must be at least 10 characters long!")
    private String content;

}
