package org.example.final_project.web;

import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final PostService postService;

    @Autowired
    public SearchController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ModelAndView searchPosts(@RequestParam("query") String query) {
        ModelAndView modelAndView = new ModelAndView("search");

        List<Post> searchResults = postService.searchByTitle(query);
        modelAndView.addObject("searchResults", searchResults);
        modelAndView.addObject("query", query);

        return modelAndView;
    }

}
