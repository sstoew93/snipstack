package org.example.final_project.web;

import org.example.final_project.client.EmailServiceClient;
import org.example.final_project.post.model.Post;
import org.example.final_project.post.service.PostService;
import org.example.final_project.report.model.Report;
import org.example.final_project.report.service.ReportService;
import org.example.final_project.security.AuthenticationDetails;
import org.example.final_project.user.model.User;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.BanUser;
import org.example.final_project.web.dto.UnbanUser;
import org.example.final_project.web.dto.UpdateRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;
    private final PostService postService;
    private final EmailServiceClient emailServiceClient;

    @Autowired
    public AdminController(UserService userService, ReportService reportService, PostService postService, EmailServiceClient emailServiceClient) {
        this.userService = userService;
        this.reportService = reportService;
        this.postService = postService;
        this.emailServiceClient = emailServiceClient;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView index(@AuthenticationPrincipal AuthenticationDetails authentication) {
        ModelAndView mav = new ModelAndView();

        UUID userId = authentication.getId();
        List<Report> reports = this.reportService.findAll();

        if (userId == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        User user = userService.findById(userId);
        mav.addObject("user", user);
        mav.addObject("updateRole", UpdateRole.builder().build());
        mav.addObject("banUser", BanUser.builder().build());
        mav.addObject("unbanUser", UnbanUser.builder().build());
        mav.addObject("reports", reports);

        mav.addObject("title", "Admin Page");
        mav.setViewName("admin");

        return mav;
    }

    @GetMapping("/banned-users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView bannedUsers(@AuthenticationPrincipal AuthenticationDetails authentication) {
        ModelAndView mav = new ModelAndView();

        UUID userId = authentication.getId();

        if (userId == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        User user = userService.findById(userId);
        List<User> bannedUsers = emailServiceClient.getBannedUsers();
        List<Report> reports = this.reportService.findAll();

        mav.addObject("user", user);
        mav.addObject("bannedUsers", bannedUsers);
        mav.addObject("title", "Banned Users");

        mav.addObject("updateRole", UpdateRole.builder().build());
        mav.addObject("banUser", BanUser.builder().build());
        mav.addObject("unbanUser", UnbanUser.builder().build());
        mav.addObject("reports", reports);
        mav.setViewName("banned-users");

        return mav;
    }


    @PutMapping("/updateRole")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateUserRole(UpdateRole updateRole) {
        userService.updateUserRole(updateRole.getUsername(), String.valueOf(updateRole.getRole()));
        return "redirect:/admin";
    }

    @PutMapping("/ban-user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String banUser(BanUser banUser) {
        userService.banUser(banUser.getUsername());
        return "redirect:/admin";
    }

    @PutMapping("/unban-user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String unbanUser(UnbanUser unbanUser) {
        userService.unbanUser(unbanUser.getUsername());

        return "redirect:/admin";
    }

    @PutMapping("/reports/resolve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String resolveReport(@RequestParam UUID reportId) {
        Report report = this.reportService.findById(reportId).get();
        Post post = this.postService.findById(report.getComment().getPost().getId());

        this.reportService.resolve(report);

        return "redirect:/forum/topic/" + post.getId();
    }

}
