package org.example.final_project.web.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsList {

    private UUID id;

    private String title;

    private String authorUsername;

    private UUID authorId;

    private int commentsCount;

    private String lastUpdated;

    private UUID lastCommentAuthorId;

    private String lastCommentAuthorUsername;
}

