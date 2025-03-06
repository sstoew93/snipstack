package org.example.final_project.scheduler;

import org.example.final_project.comment.service.CommentService;
import org.example.final_project.post.service.PostService;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.DailyStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatisticsScheduler {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public StatisticsScheduler(UserService userService, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public DailyStatistics getDailyStatistics() {
        Integer registrations = this.userService.getUsersRegisteredInLast24Hours().size();
        Integer posts = this.postService.getPostsInLast24Hours().size();
        Integer comments = this.commentService.getCommentsInLast24Hours().size();

        DailyStatistics dailyStatistics = new DailyStatistics();
        dailyStatistics.setDailyRegistrationCount(registrations);
        dailyStatistics.setDailyPostCount(posts);
        dailyStatistics.setDailyCommentCount(comments);

        return dailyStatistics;
    }
}
