package org.example.final_project.post.repository;

import org.example.final_project.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> findById(UUID postId);

    @Query("SELECT p FROM Post p WHERE SIZE(p.comments) = 0 AND p.postedOn <= :oneWeekAgo")
    List<Post> findUnansweredPosts(LocalDateTime oneWeekAgo);

    @Query("select p from Post p order by p.postedOn limit 10")
    List<Post> findLastTenPosts();

    List<Post> findByTitleContainingIgnoreCase(String query);
}

