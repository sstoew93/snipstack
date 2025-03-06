package org.example.final_project.web;

import jakarta.servlet.http.HttpSession;
import org.example.final_project.exception.DomainException;
import org.example.final_project.notification.model.Notification;
import org.example.final_project.notification.service.NotificationService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.EditUserProfile;
import org.example.final_project.web.dto.UserChangePassword;
import org.example.final_project.web.dto.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public UserController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/profile")
    public ModelAndView getMyProfile(@AuthenticationPrincipal AuthenticationDetails authentication) {
        ModelAndView modelAndView = new ModelAndView("my-profile");

        UUID userID = authentication.getId();

        if (userID == null) {
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        User loggedUser = userService.findById(userID);
        modelAndView.addObject("user", DtoMapper.mapToUserProfileInfo(loggedUser));
        modelAndView.addObject("editProfile", DtoMapper.UserProfileEdit(loggedUser));
        modelAndView.addObject("changePassword", DtoMapper.changePassword(loggedUser));

        return modelAndView;
    }

    @PutMapping("/profile/edit-password")
    public ModelAndView editPassword(UserChangePassword changePassword,@AuthenticationPrincipal AuthenticationDetails authentication, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("my-profile");
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/user/profile");

        UUID userID = authentication.getId();

        userService.changePassword(userID, changePassword);

        return modelAndView;
    }

    @PutMapping("/profile/edit")
    public ModelAndView updateMyProfile(EditUserProfile editUserProfile,@AuthenticationPrincipal AuthenticationDetails authentication) {
        ModelAndView modelAndView = new ModelAndView("redirect:/user/profile");

        UUID id = editUserProfile.getId();
        userService.updateProfile(id, editUserProfile);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getUserProfile(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authentication) {
        ModelAndView modelAndView = new ModelAndView("user-profile");

        User user = userService.findById(id);

        if (user == null) {
            return new ModelAndView("redirect:/error");
        }

        modelAndView.addObject("user", DtoMapper.mapToUserProfileInfo(user));

        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }

//    @GetMapping("/notifications")
//    public ModelAndView getUserNotifications(@AuthenticationPrincipal AuthenticationDetails authentication) {
//        ModelAndView modelAndView = new ModelAndView("notifications");
//
//        User user = userService.findById(authentication.getId());
//
//        List<Notification> unreadNotificationsByUser = notificationService.findUnreadNotificationsByUser(user);
//
//        modelAndView.addObject("unreadNotifications", unreadNotificationsByUser);
//        modelAndView.addObject("user", user);
//
//        return modelAndView;
//    }
//
//    @GetMapping("/notifications/read/{id}")
//    public String markNotificationAsRead(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authentication) {
//        Notification notification = notificationService.findById(id);
//
//        this.notificationService.save(notification);
//
//        return "redirect:/forum/topic/" + notification.getPost().getId();
//    }






}
