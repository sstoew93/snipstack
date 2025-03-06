package org.example.final_project.web.dto;

import lombok.*;
import org.example.final_project.user.model.Role;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRole {

    private String username;

    private Role role;

}
