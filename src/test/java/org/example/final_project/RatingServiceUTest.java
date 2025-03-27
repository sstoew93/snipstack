package org.example.final_project;

import org.example.final_project.comment.model.Comment;
import org.example.final_project.rating.model.Rating;
import org.example.final_project.rating.repository.RatingRepository;
import org.example.final_project.rating.service.RatingService;
import org.example.final_project.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceUTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private User user;
    private Comment comment;
    private Rating rating;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("sstoew")
                .build();

        comment = Comment.builder()
                .id(UUID.randomUUID())
                .authorComment(new User())
                .content("content")
                .blockCode("blockCode")
                .build();

        rating = Rating.builder()
                .id(UUID.randomUUID())
                .comment(comment)
                .user(user)
                .rating(5)
                .build();
    }

    @Test
    void saveRating_ShouldSaveRating() {

        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        ratingService.saveRating(user, comment, 5);

        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void hasUserRated_ShouldReturnTrue() {
        when(ratingRepository.findByUserAndComment(user, comment)).thenReturn(Optional.of(rating));

        assertTrue(ratingService.hasUserRatedComment(user, comment));
        verify(ratingRepository, times(1)).findByUserAndComment(any(User.class), any(Comment.class));
    }
}
