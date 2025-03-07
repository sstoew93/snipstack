package org.example.final_project.post.model;

import jakarta.persistence.*;
import lombok.*;

import org.example.final_project.comment.model.Comment;
import org.example.final_project.user.model.User;

import java.util.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "blockCode",columnDefinition = "MEDIUMTEXT")
    private String blockCode;

    @Column(name = "posted_on")
    private LocalDateTime postedOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User authorPost;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Comment> comments = new ArrayList<>();

    
}
