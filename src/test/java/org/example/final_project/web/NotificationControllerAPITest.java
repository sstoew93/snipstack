package org.example.final_project.web;

import org.example.final_project.notification.model.Notification;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
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

import java.util.*;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class)
public class NotificationControllerAPITest {

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private AuthenticationDetails principal;
    private User user;

    @BeforeEach
    void setUp() {
        principal = AuthenticationDetails.builder()
                .id(UUID.randomUUID())
                .userAvatar("avatar")
                .role(Role.ADMIN)
                .isActive(true)
                .unreadNotificationsCount(2)
                .password("123456")
                .username("sstoew")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .avatar("avatar")
                .role(Role.ADMIN)
                .isActive(true)
                .password("123456")
                .username("sstoew")
                .userPosts(new ArrayList<>())
                .comments(new ArrayList<>())
                .userRating(new ArrayList<>())
                .build();
    }

    @Test
    void getRequestToNotification_ShouldReturnNotificationsForUser() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/notifications")
                .with(user(principal))
                .with(csrf());

        user.setNotifications(new ArrayList<>());

        when(userService.findById(principal.getId())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(view().name("notifications"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("unreadNotifications"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getRequestToNotificationRead_ShouldReturnPost() throws Exception {
        Post post = new Post();
        post.setId(UUID.randomUUID());

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setPostId(post.getId());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/notifications/read/" + notification.getId())
                .with(user(principal))
                .with(csrf());

        user.setNotifications(new ArrayList<>());

        when(notificationService.findById(any(UUID.class))).thenReturn(notification);
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forum/topic/" + notification.getPostId()));

        verify(notificationService, times(1)).findById(any(UUID.class));
        verify(notificationService, times(1)).save(any(Notification.class));
    }


}
