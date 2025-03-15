package org.example.final_project.post.service;

import lombok.extern.slf4j.Slf4j;
import org.example.final_project.exception.DomainException;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.repository.PostRepository;
import org.example.final_project.user.model.User;
import org.example.final_project.web.dto.AddTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAllPosts() {

        return postRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Post::getUpdatedOn).reversed())
                .toList();
    }

    public Post findById(UUID id) {
        return postRepository.findById(id).orElseThrow(() -> new DomainException("Post not found!"));
    }

    public Post addPost(AddTopic addTopic, User user) throws DomainException {
        Post post = new Post();

        if (addTopic.getCodeBlock() != null && !addTopic.getCodeBlock().isEmpty()) {
            post.setBlockCode(addTopic.getCodeBlock().trim());
        } else {
            post.setBlockCode(null);
        }

        post.setTitle(addTopic.getTitle());
        post.setContent(addTopic.getContent());
        post.setAuthorPost(user);
        post.setPostedOn(LocalDateTime.now());
        post.setUpdatedOn(LocalDateTime.now());

        log.info("Added new post [%s] from [%s] at %s".formatted(post.getTitle(), post.getAuthorPost().getUsername(), post.getPostedOn()));

        return postRepository.save(post);
    }

    public List<Post> findUnansweredPosts(LocalDateTime oneWeekAgo) {
        return this.postRepository.findUnansweredPosts(oneWeekAgo);
    }

    public void delete(Post unansweredPost) {
        this.postRepository.delete(unansweredPost);
        log.info("Scheduler: Deleted post with title [%s]".formatted(unansweredPost.getTitle()));
    }

    public List<Post> findLastTenPosts() {
        return this.postRepository.findLastTenPosts();
    }

    public List<Post> searchByTitle(String query) {

        if (query == null || query.isBlank()) {
            throw new DomainException("Please enter a valid text to search!");
        }

        return postRepository.findByTitleContainingIgnoreCase(query);
    }
}
