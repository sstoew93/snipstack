package org.example.final_project.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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

import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ModelAndView getMyProfile(@AuthenticationPrincipal AuthenticationDetails authentication) {
        ModelAndView modelAndView = new ModelAndView("my-profile");

        UUID userID = authentication.getId();
        User loggedUser = userService.findById(userID);

        modelAndView.addObject("user", DtoMapper.mapToUserProfileInfo(loggedUser));
        modelAndView.addObject("editProfile", DtoMapper.UserProfileEdit(loggedUser));
        modelAndView.addObject("changePassword", DtoMapper.changePassword(loggedUser));

        return modelAndView;
    }

    @PutMapping("/profile/edit-password")
    public ModelAndView editPassword(@Valid UserChangePassword changePassword, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authentication) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("redirect:/user/profile");
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

}
