package org.example.final_project.notification.repository;

import jakarta.transaction.Transactional;
import org.example.final_project.notification.model.Notification;
import org.example.final_project.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false")
    List<Notification> findUnreadNotificationsByUser(@Param("user") User user);

    @Query("SELECT COUNT(*) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    int countUnreadNotificationsByUser(@Param("userId") UUID userId);

}
