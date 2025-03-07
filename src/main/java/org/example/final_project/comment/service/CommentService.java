package org.example.final_project.comment.service;

import lombok.extern.slf4j.Slf4j;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.comment.repository.CommentRepository;
import org.example.final_project.exception.DomainException;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.repository.PostRepository;
import org.example.final_project.user.model.User;
import org.example.final_project.web.dto.AddComment;
import org.example.final_project.web.dto.EditCommentByAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    @Autowired
    public CommentService(PostRepository postRepository, CommentRepository commentRepository, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    public void addCommentToPost(AddComment addComment, UUID postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        Comment comment = new Comment();

        if (addComment.getBlockCode() != null && !addComment.getBlockCode().isEmpty()) {
            comment.setBlockCode(addComment.getBlockCode());
        } else {
            comment.setBlockCode(null);
        }

        comment.setContent(addComment.getContent());
        comment.setPost(post);
        comment.setAuthorComment(user);
        comment.setCreatedAt(LocalDateTime.now());
        post.setUpdatedOn(LocalDateTime.now());

        commentRepository.save(comment);

        if (!post.getAuthorPost().equals(user)) {
            String message = user.getUsername() + " commented on your topic.";
            notificationService.sendNotification(post.getAuthorPost(), message, post);
        }
    }


    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        log.info("Comment deleted: " + comment.getId());

        commentRepository.delete(comment);
    }

    public void editCommentByAdmin(UUID commentId, EditCommentByAdmin editComment) {
        Comment comment = commentRepository.findById(commentId).get();

        if (editComment.getCodeBlock() != null && !editComment.getCodeBlock().trim().isEmpty()) {
            comment.setBlockCode(editComment.getCodeBlock());
        }

        comment.setContent(editComment.getContent());

        commentRepository.save(comment);
    }

    public Comment findById(UUID commentId) {

        Optional<Comment> byId = this.commentRepository.findById(commentId);

        return byId.orElseThrow(() -> new DomainException("Comment not found!"));
    }

    public void deleteById(UUID id) {
        this.commentRepository.deleteById(id);
    }
}
