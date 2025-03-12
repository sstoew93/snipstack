package org.example.final_project.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;
import java.time.LocalDateTime;

@Slf4j
@Component
public class Scheduler {

    private final PostService postService;

    @Autowired
    public Scheduler(PostService postService) {
        this.postService = postService;
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
