package org.example.final_project.web;

import org.example.final_project.exception.*;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExistException(RedirectAttributes redirectAttributes, UsernameAlreadyExistException e) {

        redirectAttributes.addFlashAttribute("usernameExist", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public String handleEmailAlreadyExistException(RedirectAttributes redirectAttributes, EmailAlreadyExistException e) {

        redirectAttributes.addFlashAttribute("emailExist", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException() {

        return "redirect:/admin";
    }

    @ExceptionHandler(UserPasswordNotMatch.class)
    public String handleUserPasswordNotMatchException(RedirectAttributes redirectAttributes, UserPasswordNotMatch e) {

        if (e.getMessage().equals("Incorrect old password or password length is not between 6 and 20 characters!")) {
            redirectAttributes.addFlashAttribute("oldPasswordError", e.getMessage());
        } else if (e.getMessage().equals("Passwords do not match!")) {
            redirectAttributes.addFlashAttribute("confirmPasswordError", e.getMessage());
        }

        return "redirect:/user/profile";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoResourceFoundException.class, TypeMismatchException.class, DomainException.class})
    public ModelAndView handleNotFoundException() {

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException() {
        return new ModelAndView("not-found");
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException() {

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReportNotFoundException.class)
    public String handleReportNotFoundException() {

        return "redirect:/admin";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(TopicNotFoundException.class)
    public String handleTopicNotFoundException() {

        return "redirect:/topic/";
    }

}
