package org.example.final_project.notification.service;

import org.example.final_project.notification.model.Notification;
import org.example.final_project.notification.repository.NotificationRepository;
import org.example.final_project.post.model.Post;
import org.example.final_project.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(User recipient, String message, Post post) {

        if (post.getId() == null) {
            throw new IllegalArgumentException("Post must have a valid topic_id");
        }

        Notification notification = Notification.builder()
                .user(recipient)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .post(post)
                .build();

        notificationRepository.save(notification);
    }

    public Notification findById(UUID id) {
        return this.notificationRepository.findById(id).orElseThrow();
    }

    public void save(Notification notification) {
        notification.setRead(true);
        this.notificationRepository.save(notification);
    }

}