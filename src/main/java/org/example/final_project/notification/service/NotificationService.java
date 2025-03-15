package org.example.final_project.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.example.final_project.exception.DomainException;
import org.example.final_project.notification.model.Notification;
import org.example.final_project.notification.repository.NotificationRepository;
import org.example.final_project.post.model.Post;
import org.example.final_project.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(User recipient, String message, Post post) {

        Notification notification = Notification.builder()
                .user(recipient)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .postId(post.getId())
                .build();

        notificationRepository.save(notification);
        log.info("Notification sent to [%s]".formatted(recipient.getUsername()));
    }

    public Notification findById(UUID id) {
        return this.notificationRepository.findById(id).orElseThrow(() -> new DomainException("Notification with id [%s] not found".formatted(id)));
    }

    public void save(Notification notification) {
        notification.setRead(true);
        this.notificationRepository.save(notification);

        log.info("Notification [%s] reviewed by user [%s]".formatted(notification.getId(), notification.getUser().getUsername()));
    }

}