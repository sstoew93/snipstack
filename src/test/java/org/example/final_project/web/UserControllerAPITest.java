package org.example.final_project.web;

import jakarta.servlet.http.HttpSession;
import org.example.final_project.rating.model.Rating;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.EditUserProfile;
import org.example.final_project.web.dto.UserChangePassword;
import org.example.final_project.web.dto.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerAPITest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private DtoMapper dtoMapper;

    @Autowired
    private MockMvc mockMvc;

    private AuthenticationDetails principal;
    private User user;

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
    void getRequestToProfile_ShouldReturnMyProfileView() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/user/profile")
                .with(user(principal))
                .with(csrf());

        when(userService.findById(principal.getId())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("editProfile"))
                .andExpect(model().attributeExists("changePassword"))
                .andExpect(view().name("my-profile"));

        verify(userService, times(1)).findById(principal.getId());
    }

    @Test
    void putRequestToChangePassword_ShouldChangeUserPassword() throws Exception {
        UserChangePassword input = UserChangePassword.builder()
                .newPassword("newPassword")
                .oldPassword("oldPassword")
                .confirmPassword("oldPassword")
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/user/profile/edit-password")
                .formField("newPassword", "newPassword")
                .formField("oldPassword", "oldPassword")
                .formField("confirmPassword", "oldPassword")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).changePassword(principal.getId(), input);

    }

    @Test
    void putRequestToChangePasswordWithNotValidData_ShouldNotChangeUserPassword() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/user/profile/edit-password")
                .formField("newPassword", "new")
                .formField("oldPassword", "olw")
                .formField("confirmPassword", "olw")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));

        verify(userService, never()).changePassword(eq(principal.getId()), any(UserChangePassword.class));

    }

    @Test
    void getRequestToUserProfile_ShouldReturnUserProfileView() throws Exception {
        UUID userId = UUID.randomUUID();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/user/" + userId)
                .with(user(principal))
                .with(csrf());

        User mockUser = User.builder()
                .id(userId)
                .isActive(true)
                .username("sstoew")
                .password("123456")
                .role(Role.USER)
                .email("sstoew@snipstack.com")
                .avatar("avatar")
                .userPosts(new ArrayList<>())
                .comments(new ArrayList<>())
                .userRating(new ArrayList<>())
                .build();

        when(userService.findById(userId)).thenReturn(mockUser);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getRequestToUserProfileDoesntExist_ShouldThrow() throws Exception {
        UUID userId = UUID.randomUUID();
        principal.setRole(Role.ADMIN);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/user/" + userId)
                .with(user(principal))
                .with(csrf());

        when(userService.findById(userId)).thenReturn(null);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());

    }

    @Test
    void putRequestToUserProfile_ShouldChangeUserData() throws Exception {
        UUID id = UUID.randomUUID();
        EditUserProfile data = EditUserProfile.builder()
                .id(id)
                .firstName("firstName")
                .lastName("lastName")
                .avatar("avatar")
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/user/profile/edit")
                .formField("id", id.toString())
                .formField("firstName", "firstName")
                .formField("lastName", "lastName")
                .formField("avatar", "avatar")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).updateProfile(eq(id), eq(data));

    }

}
