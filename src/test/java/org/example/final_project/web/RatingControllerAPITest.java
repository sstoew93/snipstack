package org.example.final_project.web;

import org.example.final_project.comment.model.Comment;
import org.example.final_project.comment.service.CommentService;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.rating.service.RatingService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RatingController.class)
public class RatingControllerAPITest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RatingService ratingService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private PostService postService;

    @Test
    void submitRating_ShouldAddRatingToUser() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        User user = randUser(userId);
        Comment comment = new Comment();
        comment.setId(commentId);

        Post post = new Post();
        post.setId(postId);

        AuthenticationDetails principal = AuthenticationDetails.builder()
                .id(userId)
                .userAvatar("avatar")
                .role(Role.USER)
                .isActive(true)
                .unreadNotificationsCount(2)
                .password("123456")
                .username("sstoew")
                .build();

        when(userService.findById(userId)).thenReturn(user);
        when(commentService.findById(commentId)).thenReturn(comment);
        when(postService.findById(postId)).thenReturn(post);
        when(ratingService.hasUserRatedComment(any(User.class), any(Comment.class))).thenReturn(false);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/ratings")
                .param("value", "5")
                .param("postId", postId.toString())
                .param("userId", userId.toString())
                .param("commentId", commentId.toString())
                .with(user(principal))
                .with(csrf());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forum/topic/" + postId));

        verify(ratingService, times(1)).saveRating(user, comment, 5);

    }

    @Test
    void submitRating_ShouldRedirectIfUserAlreadyRated() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        User user = randUser(userId);
        Comment comment = new Comment();
        comment.setId(commentId);

        Post post = new Post();
        post.setId(postId);

        AuthenticationDetails principal = AuthenticationDetails.builder()
                .id(userId)
                .userAvatar("avatar")
                .role(Role.USER)
                .isActive(true)
                .unreadNotificationsCount(2)
                .password("123456")
                .username("sstoew")
                .build();

        when(userService.findById(userId)).thenReturn(user);
        when(commentService.findById(commentId)).thenReturn(comment);
        when(postService.findById(postId)).thenReturn(post);
        when(ratingService.hasUserRatedComment(any(User.class), any(Comment.class))).thenReturn(true);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/ratings")
                .param("value", "5")
                .param("postId", postId.toString())
                .param("userId", userId.toString())
                .param("commentId", commentId.toString())
                .with(user(principal))
                .with(csrf());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forum/topic/" + postId));

        verify(ratingService, never()).saveRating(user, comment, 2);

    }

    private User randUser(UUID userId) {
        return User.builder()
                .id(userId)
                .username("sstoew")
                .password("123456")
                .avatar("avatar")
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .email("sstoew@snipstac.com")
                .build();
    }

}
