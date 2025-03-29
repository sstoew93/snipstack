package org.example.final_project.web;

import org.example.final_project.post.service.PostService;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.RegisterUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IndexController.class)
public class IndexControllerAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PostService postService;

    @Test
    void getRequestToRegisterEndpoint_ShouldReturnRegisterView() throws Exception {
        MockHttpServletRequestBuilder request = get("/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerUser"));

    }

    @Test
    void postRequestToRegister_ShouldRegisterUser() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .param("username", "sstoew")
                .param("password", "123456")
                .param("email", "sstoew@snipstack.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService, times(1)).register(any(RegisterUser.class));

    }

    @Test
    void postRequestToRegister_ShouldNotRegisterUserWhenInputIsInvalid() throws Exception {
        MockHttpServletRequestBuilder request = post("/register")
                .param("username", "ss")
                .param("password", "1256")
                .param("email", "sstoew@snipstack.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any(RegisterUser.class));
    }

    @Test
    void getRequestToIndexEndpoint_ShouldReturnIndexView() throws Exception {
        MockHttpServletRequestBuilder request = get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("lastPosts"));
    }

    @Test
    void getRequestToContributorsEndpoint_ShouldReturnContributorsView() throws Exception {
        MockHttpServletRequestBuilder request = get("/contributors");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("contributors"))
                .andExpect(model().attributeExists("contributors"));
    }

    @Test
    void getRequestToLoginEndpoint_ShouldReturnLoginView() throws Exception {
        MockHttpServletRequestBuilder request = get("/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginUser"));
    }

    @Test
    void postRequestToLoginEndpointWithErrorData_ShouldReturnLoginViewWithError() throws Exception {
        MockHttpServletRequestBuilder request = get("/login")
                .param("error", "wrong data input");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginUser"))
                .andExpect(model().attributeExists("error"));
    }

}
