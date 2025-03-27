package org.example.final_project;

import org.example.final_project.exception.DomainException;
import org.example.final_project.notification.model.Notification;
import org.example.final_project.notification.repository.NotificationRepository;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceUTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private User recipient;
    private Post post;

    @BeforeEach
    void setUp() {
        recipient = User.builder()
                .id(UUID.randomUUID())
                .username("sstoew")
                .isActive(true)
                .role(Role.ADMIN)
                .build();

        notification = Notification.builder()
                .user(recipient)
                .message("message content")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .postId(UUID.randomUUID())
                .build();

        post = Post.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    void sendNotification_ShouldSendNotificationToRecipientAndSaveToRepository() {

        notificationService.sendNotification(recipient, "message content", post);

        verify(notificationRepository, times(1)).save(
                argThat(notification ->
                        notification.getUser().equals(recipient) &&
                                notification.getMessage().equals("message content") &&
                                !notification.isRead() &&
                                notification.getPostId().equals(post.getId())
                )
        );

    }

    @Test
    void findById_ShouldReturnNotification() {
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));

        Notification optional = notificationService.findById(notification.getId());

        verify(notificationRepository, times(1)).findById(notification.getId());
        assertEquals(notification, optional);
    }

    @Test
    void findById_ShouldThrowExceptionWhenNotificationNotFound() {
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> notificationService.findById(notification.getId()));

        verify(notificationRepository, times(1)).findById(notification.getId());

    }

    @Test
    void setReadNotification_ShouldSaveNotificationToRepositoryWithFlagTrue() {

        notification.setRead(true);
        notificationService.save(notification);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

}
