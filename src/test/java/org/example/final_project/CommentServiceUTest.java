package org.example.final_project;

import org.example.final_project.comment.model.Comment;
import org.example.final_project.comment.repository.CommentRepository;
import org.example.final_project.comment.service.CommentService;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.repository.PostRepository;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.web.dto.AddComment;
import org.example.final_project.web.dto.EditCommentByAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private UUID userId;
    private UUID postId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        postId = UUID.randomUUID();

        this.user = User.builder()
                .id(userId)
                .username("sstoew")
                .password("123123")
                .email("email@email.email")
                .isActive(true)
                .role(Role.USER)
                .createdOn(LocalDateTime.now())
                .avatar("http://avatar.com")
                .build();
    }

    @Test
    void addCommentToPost_ShouldAddComment_WhenValidInput() {
        AddComment addComment = AddComment.builder()
                .content("comment content")
                .build();

        Post post = Post.builder()
                .id(postId)
                .authorPost(user)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        commentService.addCommentToPost(addComment, postId, user);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentToPost_ShouldSetBlockCode_WhenBlockCodeIsProvided () {
        AddComment addComment = AddComment.builder()
                .content("comment content")
                .blockCode("blockCode")
                .build();

        assertEquals("blockCode", addComment.getBlockCode());
    }

    @Test
    void addCommentToPost_ShouldSendNotification_WhenCommenterIsNotPostAuthor() {
        User postAuthor = User.builder()
                .username("author")
                .build();

        AddComment addComment = AddComment.builder()
                .content("comment content")
                .build();

        Post post = Post.builder()
                .id(postId)
                .authorPost(postAuthor)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        commentService.addCommentToPost(addComment, postId, user);

        verify(notificationService, never()).sendNotification(postAuthor, "test", post);

    }

    @Test
    void findById_ShouldReturnComment_WhenCommentExists() {
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .build();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        Comment foundComment = commentService.findById(comment.getId());

        assertNotNull(foundComment);
    }

    @Test
    void editCommentByAdmin_ShouldUpdateCommentContentAndCodeBlock() {
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .content("comment content")
                .blockCode("blockCode")
                .build();

        EditCommentByAdmin editCommentByAdmin = EditCommentByAdmin.builder()
                .codeBlock("editedBlockCode")
                .content("editedContent")
                .build();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.editCommentByAdmin(comment.getId(), editCommentByAdmin);

        assertEquals("editedContent", comment.getContent());
        assertEquals("editedBlockCode", comment.getBlockCode());
        verify(commentRepository, times(1)).save(any(Comment.class));

    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenCommentExists() {
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .content("comment content")
                .blockCode("blockCode")
                .build();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.deleteComment(comment.getId());

        verify(commentRepository, times(1)).delete(comment);
    }

}


