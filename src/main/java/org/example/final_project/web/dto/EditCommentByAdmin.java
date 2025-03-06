package org.example.final_project.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditCommentByAdmin {

    private String codeBlock;

    @Size(min = 10, message = "Reply must be at least 10 characters long!")
    @NotBlank(message = "Reply cannot be empty!")
    private String content;

}
