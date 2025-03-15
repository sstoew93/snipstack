package org.example.final_project.rating.service;

import lombok.extern.slf4j.Slf4j;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.rating.model.Rating;
import org.example.final_project.rating.repository.RatingRepository;
import org.example.final_project.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public void saveRating(User ratedUser, Comment comment, int value) {
        Rating rating = new Rating();
        rating.setUser(ratedUser);
        rating.setComment(comment);
        rating.setRating(value);
        ratingRepository.save(rating);

        log.info("Rating [%d] added for user: %s".formatted(value, rating.getUser().getUsername()));
    }

    public boolean hasUserRatedComment(User user, Comment comment) {
        return ratingRepository.findByUserAndComment(user, comment).isPresent();
    }



}
