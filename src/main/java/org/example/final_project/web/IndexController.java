package org.example.final_project.web;

import jakarta.validation.Valid;
import org.example.final_project.user.service.UserService;
import org.example.final_project.web.dto.ContributorsList;
import org.example.final_project.web.dto.LoginUser;
import org.example.final_project.web.dto.RegisterUser;
import org.example.final_project.web.dto.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView registerPage() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("registerUser", new RegisterUser());

        return mav;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid RegisterUser registerUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerUser);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginUser", new LoginUser());

        if (error != null) {
            modelAndView.addObject("error", "Invalid username or password!");
        }

        return modelAndView;
    }

    @GetMapping("/contributors")
    public ModelAndView contributors() {
        ModelAndView modelAndView = new ModelAndView("contributors");

        List<ContributorsList> contributorsLists = this.userService.findAll()
                .stream()
                .map(DtoMapper::mapToContributorsList)
                .sorted((a, b) -> Double.compare(
                        Double.parseDouble(b.getAverageRating()),
                        Double.parseDouble(a.getAverageRating())
                )).toList();

        modelAndView.addObject("contributors", contributorsLists);
        return modelAndView;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/messages")
    public String getMessages() {
        return "messages";
    }

}
