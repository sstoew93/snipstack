package org.example.final_project.web;

import org.example.final_project.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SearchController.class)
public class SearchControllerAPITest {

    @MockitoBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void searchPost_ShouldReturnPostsByKeyword() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/search")
                .param("query", "java");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("query"))
                .andExpect(model().attributeExists("searchResults"));

        verify(postService, times(1)).searchByTitle("java");
    }
}
