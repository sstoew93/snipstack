package org.example.final_project.rating.repository;

import org.example.final_project.comment.model.Comment;
import org.example.final_project.rating.model.Rating;

import org.example.final_project.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndComment(User user, Comment comment);
}

