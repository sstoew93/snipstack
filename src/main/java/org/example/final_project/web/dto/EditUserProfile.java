package org.example.final_project.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EditUserProfile {

    private UUID id;

    private String firstName;

    private String lastName;

    private String avatar;

}
