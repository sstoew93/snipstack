package org.example.final_project.post.repository;

import org.example.final_project.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> findById(UUID postId);

    List<Post> findAllByPostedOnAfter(LocalDateTime postedOn);
}
