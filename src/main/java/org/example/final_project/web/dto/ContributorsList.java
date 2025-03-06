package org.example.final_project.web.dto;

import lombok.Builder;
import lombok.Data;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.post.model.Post;
import org.example.final_project.rating.model.Rating;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Builder
@Data
public class ContributorsList {
    private UUID id;

    private String username;

    private String avatar;

    private List<Post> userPosts;

    private List<Comment> userComments;

    private List<Rating> userRating;

    public String getAverageRating() {
        double average = userRating.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.00);

        return String.format(Locale.US, "%.2f", average);
    }

}
