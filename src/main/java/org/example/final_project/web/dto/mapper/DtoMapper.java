package org.example.final_project.web.dto.mapper;

import lombok.Builder;
import lombok.experimental.UtilityClass;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.post.model.Post;
import org.example.final_project.user.model.User;
import org.example.final_project.web.dto.*;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
@Builder
public class DtoMapper {

    public static UserInfo mapToUserProfileInfo(User user) {
        return UserInfo.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .userPosts(user.getUserPosts())
                .userComments(user.getComments())
                .userRating(user.getUserRating())
                .build();
    }

    public static EditUserProfile UserProfileEdit(User user) {
        return EditUserProfile.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .build();
    }

    public static EditCommentByAdmin mapToEditCommentByAdmin(Comment comment) {
        return EditCommentByAdmin.builder()
                .codeBlock(comment.getBlockCode())
                .content(comment.getContent())
                .build();
    }

    public static PostsList mapToPostDTO(Post post) {
        return PostsList.builder()
                .id(post.getId())
                .title(post.getTitle())
                .authorUsername(post.getAuthorPost().getUsername())
                .authorId(post.getAuthorPost().getId())
                .commentsCount(post.getComments().size())
                .lastUpdated(post.getUpdatedOn().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH)))
                .lastCommentAuthorId(
                        post.getComments().isEmpty() ? null : post.getComments().get(0).getAuthorComment().getId()
                )
                .lastCommentAuthorUsername(
                        post.getComments().isEmpty() ? "No comments" : post.getComments().get(post.getComments().size() - 1).getAuthorComment().getUsername()
                )
                .build();
    }

    public static ContributorsList mapToContributorsList(User user) {
        return ContributorsList.builder()
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .userPosts(user.getUserPosts())
                .userComments(user.getComments())
                .userRating(user.getUserRating())
                .build();
    }

    public static UserChangePassword changePassword(User loggedUser) {
        return UserChangePassword.builder()
                .oldPassword(loggedUser.getPassword())
                .newPassword(loggedUser.getPassword())
                .confirmPassword(loggedUser.getPassword())
                .build();
    }
}
