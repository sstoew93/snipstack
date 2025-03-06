package org.example.final_project.web;

import jakarta.transaction.Transactional;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.comment.service.CommentService;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.rating.service.RatingService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


@Controller
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final PostService postService;

    @Autowired
    public RatingController(RatingService ratingService, UserService userService, CommentService commentService, NotificationService notificationService, PostService postService) {
        this.ratingService = ratingService;
        this.userService = userService;
        this.commentService = commentService;
        this.notificationService = notificationService;
        this.postService = postService;
    }

    @PostMapping
    @Transactional
    public String submitRating(@AuthenticationPrincipal AuthenticationDetails authentication, @RequestParam("value") int value, @RequestParam("postId") UUID postId, @RequestParam("userId") UUID userId, @RequestParam("commentId") UUID commentId) {
        User ratedUser = userService.findById(userId);
        Comment comment = commentService.findById(commentId);
        Post post = postService.findById(postId);

        if (ratingService.hasUserRatedComment(ratedUser, comment)) {
            return "redirect:/forum/topic/" + postId;
        }

        String message = authentication.getUsername() + " rated you comment with " + value + " stars!";

        this.notificationService.sendNotification(comment.getAuthorComment(), message, post);
        this.ratingService.saveRating(ratedUser, comment, value);

        return "redirect:/forum/topic/" + postId;
    }

}