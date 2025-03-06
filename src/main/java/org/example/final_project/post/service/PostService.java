package org.example.final_project.post.service;

import org.example.final_project.exception.DomainException;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.repository.PostRepository;
import org.example.final_project.user.model.User;
import org.example.final_project.web.dto.AddTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

        return postRepository.save(post);
    }

    public List<Post> getPostsInLast24Hours() {
        LocalDateTime lastDayPosts = LocalDateTime.now().minusDays(1);
        return this.postRepository.findAllByPostedOnAfter(lastDayPosts);
    }
}
