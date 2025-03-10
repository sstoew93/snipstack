package org.example.final_project.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BanNotification {

    private UUID userId;

    private String username;

    private String email;

}
