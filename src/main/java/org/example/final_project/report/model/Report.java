package org.example.final_project.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reported;

    @ManyToOne
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    private LocalDateTime reportTime;

    private boolean resolved;
}
