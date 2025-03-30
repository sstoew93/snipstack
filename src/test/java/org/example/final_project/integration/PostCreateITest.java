package org.example.final_project.integration;

import jakarta.transaction.Transactional;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.repository.PostRepository;
import org.example.final_project.post.service.PostService;
import org.example.final_project.user.model.User;
import org.example.final_project.user.repository.UserRepository;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.AddTopic;
import org.example.final_project.web.dto.RegisterUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PostCreateITest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Transactional
    @Test
    void addPost_ShouldAddPost() {
        RegisterUser registerUser= RegisterUser.builder()
                .username("sstoew")
                .password("123456")
                .email("sstoew@snipstack.com")
                .terms(true)
                .build();

        userService.register(registerUser);

        AddTopic addTopic = AddTopic.builder()
                .title("test title")
                .content("test post content.")
                .codeBlock("test codeBlock")
                .build();

        User user = userService.findByUsername("sstoew");

        postService.addPost(addTopic, user);

        assertEquals(1, postRepository.findAll().size());

        Optional<Post> optionalPost = postRepository.findAll().stream().findFirst();
        assertTrue(optionalPost.isPresent());

        Post savedPost = optionalPost.get();
        assertEquals(user.getUsername(), savedPost.getAuthorPost().getUsername());

    }
}
