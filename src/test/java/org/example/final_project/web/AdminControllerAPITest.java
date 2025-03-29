package org.example.final_project.web;

import org.example.final_project.client.EmailServiceClient;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.report.model.Report;
import org.example.final_project.report.service.ReportService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.UpdateRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AdminController.class})
public class AdminControllerAPITest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private EmailServiceClient emailServiceClient;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private AuthenticationDetails principal;

    @BeforeEach
    public void setup() {
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
                .role(Role.USER)
                .isActive(true)
                .password("123456")
                .username("sstoew")
                .userPosts(new ArrayList<>())
                .comments(new ArrayList<>())
                .userRating(new ArrayList<>())
                .build();
    }

    @Test
    void putRequestToUpdateRole_ShouldChangeRoleOFUser() throws Exception {
        UpdateRole updateRole = UpdateRole.builder()
                .role(Role.ADMIN)
                .username("sstoew2")
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/updateRole")
                .formField("role", updateRole.getRole().toString())
                .formField("username", updateRole.getUsername())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(userService, times(1)).updateUserRole(updateRole.getUsername(), String.valueOf(updateRole.getRole()));
    }

    @Test
    void putRequestToUpdateRoleFromUnauthorizedUser_ShouldThrow() throws Exception {
        UpdateRole updateRole = UpdateRole.builder()
                .username("sstoew2")
                .role(Role.USER)
                .build();

        principal.setRole(Role.USER);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/updateRole")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(view().name("not-found"));

        verify(userService, never()).updateUserRole(updateRole.getUsername(), String.valueOf(updateRole.getRole()));
    }

    @Test
    void putRequestToBanUser_ShouldBanUserIfExists() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/ban-user")
                .formField("username", user.getUsername())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        verify(userService, times(1)).banUser(anyString());
    }

    @Test
    void putRequestToBanUserWithNoAdminRole_ShouldNotBanUserAndThrow() throws Exception {
        principal.setRole(Role.USER);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/ban-user")
                .formField("username", user.getUsername())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isForbidden())
                        .andExpect(view().name("not-found"));
        verify(userService, never()).banUser(anyString());
    }

    @Test
    void putRequestToUnBanUserWithNoAdminRole_ShouldNotUnBanUserAndThrow() throws Exception {
        principal.setRole(Role.USER);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/unban-user")
                .formField("username", user.getUsername())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(view().name("not-found"));
        verify(userService, never()).unbanUser(anyString());
    }

    @Test
    void putRequestToUnBanUser_ShouldUnBanUserIfExists() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/unban-user")
                .formField("username", user.getUsername())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        verify(userService, times(1)).unbanUser(anyString());
    }

    @Test
    void putRequestToResolveReportWithAdminRole_ShouldRedirectToTopic() throws Exception {
        UUID postId = UUID.randomUUID();
        Post post = Post.builder()
                .id(postId)
                .build();

        Comment comment = Comment.builder()
                .post(post)
                .build();

        Report report = Report.builder()
                .id(UUID.randomUUID())
                .comment(comment)
                .build();

        when(reportService.findById(report.getId())).thenReturn(Optional.of(report));
        when(postService.findById(post.getId())).thenReturn(post);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/reports/resolve")
                .param("reportId", report.getId().toString())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forum/topic/" + post.getId()));
        verify(reportService, times(1)).findById(report.getId());
        verify(postService, times(1)).findById(post.getId());
        verify(reportService,times(1)).resolve(report);
    }

    @Test
    void getRequestToAdminEndpoint_ShouldReturnAdminPanelView() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/admin")
                .with(user(principal))
                .with(csrf());

        when(emailServiceClient.getBannedUsers()).thenReturn(new ArrayList<>());
        when(reportService.findAll()).thenReturn(new ArrayList<>());
        when(userService.findById(any(UUID.class))).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("bannedUsers"))
                .andExpect(model().attributeExists("reports"))
                .andExpect(model().attributeExists("updateRole"))
                .andExpect(model().attributeExists("unbanUser"))
                .andExpect(model().attributeExists("banUser"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("title"));

        verify(emailServiceClient, times(1)).getBannedUsers();
        verify(reportService, times(1)).findAll();
    }
}
