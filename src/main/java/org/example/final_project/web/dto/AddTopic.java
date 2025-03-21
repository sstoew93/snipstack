package org.example.final_project.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTopic {

    @NotNull(message = "Title should be between 10 and 40 characters long!")
    @Size(min = 10, max = 40, message = "Title should be between 10 and 40 characters long!")
    private String title;

    @NotBlank(message = "Code snippet cannot be empty!")
    @NotNull(message = "Code snippet cannot be empty!")
    private String codeBlock;

    @NotNull(message = "Your message cannot be empty or less than 10 characters!")
    @Size(min = 10, message = "Your message cannot be empty or less than 10 characters!")
    private String content;
}