package org.example.final_project.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;
import java.time.LocalDateTime;

@Slf4j
@Component
public class StatisticsScheduler {

    private final PostService postService;
    private final NotificationService notificationService;

    @Autowired
    public StatisticsScheduler(PostService postService, NotificationService notificationService) {
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 00 0 * * ?")
    public void deleteUnansweredPosts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<Post> unansweredPosts = this.postService.findUnansweredPosts(oneWeekAgo);

        if (!unansweredPosts.isEmpty()) {
            for (Post unansweredPost : unansweredPosts) {
                this.postService.delete(unansweredPost);
            }

        } else {
            log.info("No unanswered posts to delete.");
        }
    }
}
