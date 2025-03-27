package org.example.final_project;

import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.scheduler.Scheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerUTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private Scheduler scheduler;

    @Test
    void testScheduler_ShouldDeletePostsThatIsOlderThanOneWeekIfPostsExists() {

        Post post = Post.builder()
                .postedOn(LocalDateTime.now().minusWeeks(2))
                .build();
        Post post2 = Post.builder()
                .postedOn(LocalDateTime.now().minusWeeks(3))
                .build();

        when(postService.findUnansweredPosts(any(LocalDateTime.class)))
                .thenReturn(List.of(post, post2));

        scheduler.deleteUnansweredPosts();

        verify(postService, times(1)).findUnansweredPosts(any(LocalDateTime.class));
        verify(postService, times(1)).delete(post);
        verify(postService, times(1)).delete(post2);
    }

    @Test
    void testScheduler_ShouldNotTryDeleteIfPostsNotExist() {
        when(postService.findUnansweredPosts(any(LocalDateTime.class)))
                .thenReturn(List.of());

        scheduler.deleteUnansweredPosts();

        verify(postService, times(1)).findUnansweredPosts(any(LocalDateTime.class));
        verify(postService, never()).delete(any(Post.class));
    }
}
