package org.example.final_project;

import org.example.final_project.exception.DomainException;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.repository.PostRepository;
import org.example.final_project.post.service.PostService;
import org.example.final_project.user.model.Role;
import org.example.final_project.user.model.User;
import org.example.final_project.web.dto.AddTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceUTest {

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostService postService;

    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .updatedOn(LocalDateTime.now())
                .id(UUID.randomUUID())
                .postedOn(LocalDateTime.now())
                .content("content")
                .blockCode("blockCode")
                .title("title")
                .build();
    }

    @Test
    void findAll_ShouldReturnAllPosts() {

        Post post2 = Post.builder()
                .updatedOn(LocalDateTime.now())
                .build();

        when(postRepository.findAll()).thenReturn(List.of(post, post2));

        List<Post> allPosts = postService.findAllPosts();
        assertEquals(2, allPosts.size());
    }

    @Test
    void findById_ShouldReturnPost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        Post foundPost = postService.findById(post.getId());

        assertNotNull(foundPost);
        assertEquals("title", post.getTitle());
    }

    @Test
    void findById_ShouldThrowExceptionWhenPostNotFound() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> postService.findById(post.getId()));
    }

    @Test
    void addPost_ShouldSavePostWhenThereIsAuthor() {
        User user = User.builder()
                .username("sstoew")
                .role(Role.ADMIN)
                .isActive(true)
                .build();

        AddTopic addTopic = AddTopic.builder()
                .title("title")
                .content("content")
                .codeBlock("blockCode")
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post newPost = postService.addPost(addTopic, user);
        post.setAuthorPost(user);

        assertEquals("title", newPost.getTitle());
        assertEquals("content", newPost.getContent());
        assertEquals("blockCode", newPost.getBlockCode());
        assertEquals("sstoew", newPost.getAuthorPost().getUsername());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void searchByTitle_ShouldReturnPosts_WhenQueryIsValid() {
        Post post2 = Post.builder()
                .title("title2")
                .build();

        List<Post> result  = List.of(post, post2);

        when(postRepository.findByTitleContainingIgnoreCase("title")).thenReturn(result);

        List<Post> posts = postService.searchByTitle("title");

        assertEquals(2, posts.size());
        assertEquals("title", posts.get(0).getTitle());
        verify(postRepository, times(1)).findByTitleContainingIgnoreCase("title");
    }

    @Test
    void findUnansweredPosts_ShouldReturnUnansweredPosts() {
        Post post = Post.builder()
                .updatedOn(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
        List<Post> posts = List.of(post);
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        when(postRepository.findUnansweredPosts(oneWeekAgo)).thenReturn(posts);

        List<Post> unansweredPosts = postService.findUnansweredPosts(oneWeekAgo);

        assertEquals(1, unansweredPosts.size());
        verify(postRepository, times(1)).findUnansweredPosts(oneWeekAgo);
    }

    @Test
    void deletePost_ShouldDeletePostWhenThereIsPost() {
        postService.delete(post);

        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void findLastTenPosts_ShouldReturnAllPostsButNoMoreTenPosts() {
        List<Post> postsList = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder().title("post " + i).build())
                .toList();

        when(postRepository.findLastTenPosts()).thenReturn(postsList);

        List<Post> posts = postService.findLastTenPosts();

        assertEquals(10, posts.size());

        verify(postRepository, times(1)).findLastTenPosts();

    }

    @Test
    void searchByTitle_ShouldThrowException_WhenQueryIsNull() {
        assertThrows(DomainException.class, () -> postService.searchByTitle(null));
        assertThrows(DomainException.class, () -> postService.searchByTitle("   "));
    }


}
