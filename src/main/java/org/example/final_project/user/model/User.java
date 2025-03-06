package org.example.final_project.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.notification.model.Notification;
import org.example.final_project.post.model.Post;
import org.example.final_project.rating.model.Rating;

import java.text.DecimalFormat;
import java.util.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "authorPost")
    private List<Post> userPosts = new ArrayList<>();

    @OneToMany(mappedBy = "authorComment")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rating> userRating = new ArrayList<>();

    public String getAverageRating() {
        double average = userRating.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.00);

        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(average);
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @OrderBy("createdAt DESC")
    private List<Notification> notifications = new ArrayList<>();
}
