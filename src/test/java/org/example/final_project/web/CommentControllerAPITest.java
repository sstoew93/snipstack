package org.example.final_project.web;

import org.example.final_project.comment.model.Comment;
import org.example.final_project.comment.service.CommentService;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.report.service.ReportService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
public class CommentControllerAPITest {

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    NotificationService notificationService;

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private MockMvc mockMvc;

    private Post post;
    private Comment comment;
    private User user;
    private AuthenticationDetails principal;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("sstoew")
                .build();

        post = Post.builder()
                .id(UUID.randomUUID())
                .title("title of post")
                .content("content of post")
                .blockCode("block code of post")
                .comments(new ArrayList<>())
                .authorPost(user)
                .build();

        comment = Comment.builder()
                .id(UUID.randomUUID())
                .post(post)
                .authorComment(user)
                .build();

        principal = AuthenticationDetails.builder()
                .id(UUID.randomUUID())
                .role(Role.USER)
                .build();
    }

    @Test
    void getEditCommentEndpointWithAdminUser_ShouldReturnEditCommentView() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/topic/{postId}/edit-comment/{commentId}", post.getId(), comment.getId())
                .with(user(principal))
                .with(csrf());

        principal.setRole(Role.ADMIN);

        when(commentService.findById(comment.getId())).thenReturn(comment);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-comment"))
                .andExpect(model().attributeExists("editComment"));

        verify(commentService, times(1)).findById(comment.getId());
    }

    @Test
    void getEditCommentEndpointWithoutUser_ShouldRedirectToLogin() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/topic/{postId}/edit-comment/{commentId}", post.getId(), comment.getId());

        when(commentService.findById(comment.getId())).thenReturn(comment);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(commentService, never()).findById(comment.getId());
    }

    @Test
    void postRequestToReportEndpoint_ShouldReportComment() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/topic/{postId}/report-comment/{commentId}", post.getId(), comment.getId())
                .with(user(principal))
                .with(csrf());

        when(userService.findById(principal.getId())).thenReturn(user);
        when(commentService.findById(comment.getId())).thenReturn(comment);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forum/topic/" + post.getId()));

        verify(userService, times(1)).findById(principal.getId());
        verify(commentService, times(2)).findById(comment.getId());
        verify(reportService, times(1)).reportComment(user, user, comment);

    }

    


}
