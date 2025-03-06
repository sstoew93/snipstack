package org.example.final_project.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.AddComment;
import org.example.final_project.web.dto.AddTopic;
import org.example.final_project.web.dto.EditCommentByAdmin;
import org.example.final_project.web.dto.PostsList;
import org.example.final_project.web.dto.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public ForumController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getForumPage() {

        List<PostsList> allPosts = postService.findAllPosts()
                .stream()
                .map(DtoMapper::mapToPostDTO)
                .collect(Collectors.toList());

        ModelAndView mav = new ModelAndView("forum");

        mav.addObject("posts", allPosts);

        return mav;
    }

    @GetMapping("/add-topic")
    public ModelAndView showAddTopicForm() {

        ModelAndView mav = new ModelAndView("new-topic");
        mav.addObject("addTopic", new AddTopic());
        return mav;
    }

    @PostMapping("/add-topic")
    public ModelAndView addTopic(@Valid @ModelAttribute("addTopic") AddTopic addTopic, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authentication) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("new-topic");
            mav.addObject("addTopic", addTopic);
            return mav;
        }

        UUID userId = authentication.getId();

        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        User loggedUser = userService.findById(userId);

        Post post = postService.addPost(addTopic, loggedUser);

        ModelAndView mav = new ModelAndView();

        mav.addObject("post", post);

        return new ModelAndView("redirect:/forum/topic/" + post.getId());
    }

    @GetMapping("/topic/{id}")
    public ModelAndView getTopicPage(@PathVariable UUID id,  @AuthenticationPrincipal AuthenticationDetails authentication) {
        Post post = postService.findById(id);

        if (authentication == null) {
            ModelAndView mav = new ModelAndView("topic");
            mav.addObject("post", post);
            return mav;
        }

        UUID userId = authentication.getId();
        User loggedUser = userService.findById(userId);

        ModelAndView mav = new ModelAndView("topic");
        mav.addObject("addComment", new AddComment());
        mav.addObject("editComment", new EditCommentByAdmin());
        mav.addObject("post", post);
        mav.addObject("user", loggedUser);

        return mav;
    }

}
