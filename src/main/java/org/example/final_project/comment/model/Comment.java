package org.example.final_project.comment.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.final_project.post.model.Post;
import org.example.final_project.rating.model.Rating;
import org.example.final_project.report.model.Report;
import org.example.final_project.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "blockCode",columnDefinition = "LONGTEXT")
    private String blockCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User authorComment;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

}
