package org.example.final_project.web;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.comment.service.CommentService;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.report.service.ReportService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.AddComment;
import org.example.final_project.web.dto.EditCommentByAdmin;
import org.example.final_project.web.dto.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping()
public class CommentController {


    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService, PostService postService, NotificationService notificationService, ReportService reportService) {
        this.commentService = commentService;
        this.userService = userService;
        this.postService = postService;
        this.notificationService = notificationService;
        this.reportService = reportService;
    }

    @PostMapping("/topics/{postId}/add")
    public ModelAndView submitComment(@PathVariable UUID postId, @Valid AddComment addComment, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authentication) {
        UUID userId = authentication.getId();
        User user = userService.findById(userId);

        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("topic");
            mav.addObject("post", postService.findById(postId));
            mav.addObject("addComment", addComment);
            mav.addObject("user", user);
            return mav;
        }

        if (addComment.getContent().trim().length() < 10) {
            ModelAndView mav = new ModelAndView("topic");
            mav.addObject("post", postService.findById(postId));
            mav.addObject("addComment", addComment);
            mav.addObject("user", user);
            mav.addObject("bindingResult", bindingResult);
            return mav;
        }

        commentService.addCommentToPost(addComment, postId, user);

        return new ModelAndView("redirect:/forum/topic/" + postId);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @DeleteMapping("/topic/{postId}/delete-comment/{commentId}")
    public String deleteComment(@PathVariable UUID postId, @PathVariable UUID commentId) {
        User user = this.commentService.findById(commentId).getAuthorComment();
        Post post = postService.findById(postId);
        String message = "Admin/Moderator deleted your comment for post %s!".formatted(post.getTitle());

        this.commentService.deleteComment(commentId);
        this.notificationService.sendNotification(user, message, post);

        return "redirect:/forum/topic/" + postId;
    }

    @GetMapping("/topic/{postId}/edit-comment/{commentId}")
    public ModelAndView editComment(@PathVariable UUID postId, @PathVariable UUID commentId) {
        ModelAndView mav = new ModelAndView("edit-comment");

        Comment comment = this.commentService.findById(commentId);

        mav.addObject("editComment", DtoMapper.mapToEditCommentByAdmin(comment));

        return mav;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @PutMapping("/topic/{postId}/edit-comment/{commentId}")
    public ModelAndView editCommentByAdmin(@PathVariable UUID postId, @PathVariable UUID commentId, @Valid EditCommentByAdmin editComment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("edit-comment");
            mav.addObject("editComment", editComment);
            return mav;
        }

        User user = this.commentService.findById(commentId).getAuthorComment();
        Post post = postService.findById(postId);

        String message = "Admin/Moderator edited your comment for post %s!".formatted(post.getTitle());

        this.notificationService.sendNotification(user, message, post);
        this.commentService.editCommentByAdmin(commentId,editComment);

        return new ModelAndView("redirect:/forum/topic/" + postId);
    }


    @PostMapping("/topic/{postId}/report-comment/{commentId}")
    public ModelAndView reportComment(@PathVariable UUID postId, @PathVariable UUID commentId, @AuthenticationPrincipal AuthenticationDetails authentication) {
        User reportedUser = this.commentService.findById(commentId).getAuthorComment();
        User reporter = this.userService.findById(authentication.getId());
        Comment comment = commentService.findById(commentId);

        this.reportService.reportComment(reportedUser, reporter, comment);

        return new ModelAndView("redirect:/forum/topic/" + postId);
    }

}

