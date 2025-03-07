package org.example.final_project.web;

import org.example.final_project.notification.model.Notification;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }


    @GetMapping("/notifications")
    public ModelAndView getUserNotifications(@AuthenticationPrincipal AuthenticationDetails authentication) {
        ModelAndView modelAndView = new ModelAndView("notifications");

        User user = userService.findById(authentication.getId());

        List<Notification> unreadNotificationsByUser = user.getNotifications().stream()
                .filter(notification -> !notification.isRead())
                .toList();

        modelAndView.addObject("unreadNotifications", unreadNotificationsByUser);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/notifications/read/{id}")
    public String markNotificationAsRead(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authentication) {
        Notification notification = notificationService.findById(id);
        UUID postId = notification.getPostId();

        this.notificationService.save(notification);

        return "redirect:/forum/topic/" + postId;
    }
}
