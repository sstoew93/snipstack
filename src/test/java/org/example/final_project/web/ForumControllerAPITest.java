package org.example.final_project.web;

import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.AddTopic;
import org.example.final_project.web.dto.PostsList;
import org.example.final_project.web.dto.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ForumController.class)
public class ForumControllerAPITest {

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private Post post;
    private User user;
    private AuthenticationDetails principal;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("sstoew");

        post = new Post();
        post.setId(UUID.randomUUID());
        post.setTitle("Test valid name Post");
        post.setContent("Test valid content Post");
        post.setBlockCode("Test valid blockcode Post");
        post.setAuthorPost(user);
        post.setUpdatedOn(LocalDateTime.now());

        principal = AuthenticationDetails.builder()
                .id(UUID.randomUUID())
                .username("sstoew")
                .password("sstoew")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void getRequestToForum_ShouldReturnForumView() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/forum");

        PostsList postsList = DtoMapper.mapToPostDTO(post);
        when(postService.findAllPosts()).thenReturn(List.of(post));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("forum"))
                .andExpect(model().attributeExists("posts"));

        verify(postService, times(1)).findAllPosts();
    }

    @Test
    void getRequestToAddPost_ShouldReturnForumAddTopicView() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/forum/add-topic")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("new-topic"))
                .andExpect(model().attributeExists("addTopic"));
    }

    @Test
    void postRequestToAddPostWithInvalidData_ShouldNotAddTopic() throws Exception {
        AddTopic invalidAddTopic = AddTopic.builder()
                .title("invalid")
                .content("invalid")
                .codeBlock("invalid")
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/forum/add-topic")
                .with(user(principal))
                .with(csrf())
                .param("title", invalidAddTopic.getTitle())
                .param("content", invalidAddTopic.getContent())
                .param("codeBlock", invalidAddTopic.getCodeBlock());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("new-topic"))
                .andExpect(model().attributeExists("addTopic"));
    }

    @Test
    void postRequestToAddPostWitValidData_ShouldAddTopic() throws Exception {
        AddTopic validAddTopic = AddTopic.builder()
                .title("valid title of post")
                .codeBlock("valid code block of post")
                .content("valid content block of post")
                .build();

        Post mockPost = new Post();
        mockPost.setId(UUID.randomUUID());
        mockPost.setTitle(validAddTopic.getTitle());
        mockPost.setContent(validAddTopic.getContent());
        mockPost.setBlockCode(validAddTopic.getCodeBlock());
        mockPost.setAuthorPost(user);
        mockPost.setUpdatedOn(LocalDateTime.now());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/forum/add-topic")
                .with(user(principal))
                .with(csrf())
                .param("title", validAddTopic.getTitle())
                .param("content", validAddTopic.getContent())
                .param("codeBlock", validAddTopic.getCodeBlock());

        when(userService.findById(principal.getId())).thenReturn(user);

        when(postService.addPost(any(AddTopic.class), eq(user))).thenReturn(mockPost);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forum/topic/" + mockPost.getId()));

        verify(postService, times(1)).addPost(any(AddTopic.class), eq(user));
    }

    @Test
    void getRequestToTopicId_ShouldReturnTopicView() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/forum/topic/{id}", post.getId())
                .with(user(principal))
                .with(csrf());

        when(postService.findById(post.getId())).thenReturn(post);
        when(userService.findById(principal.getId())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("topic"))
                .andExpect(model().attributeExists("addComment"))
                .andExpect(model().attributeExists("editComment"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("user"));
    }


}
