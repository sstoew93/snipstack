//package org.example.final_project.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.example.final_project.user.model.Role;
//import org.example.final_project.user.model.User;
//import org.example.final_project.user.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.util.Set;
//import java.util.UUID;
//
//@Component
//public class SessionCheckInterceptor implements HandlerInterceptor {
//
//    private final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/", "/messages", "/login", "/register", "/forum", "/contributors", "/error");
//    private final Set<String> ADMIN_ENDPOINTS = Set.of("/admin");
//
//    private final UserService userService;
//
//    @Autowired
//    public SessionCheckInterceptor(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        String requestURI = request.getServletPath();
//
//        if(UNAUTHENTICATED_ENDPOINTS.contains(requestURI) || requestURI.startsWith("/forum/topic")) {
//            return true;
//        }
//
//        HttpSession currentUserSession = request.getSession(false);
//        if(currentUserSession == null) {
//            response.sendRedirect("/login");
//
//            return false;
//        }
//
//        UUID id = (UUID) request.getSession().getAttribute("user_id");
//        User user = userService.findById(id);
//
//        if (ADMIN_ENDPOINTS.contains(requestURI) && user.getRole() != Role.ADMIN) {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.sendRedirect("/error");
//            return false;
//        }
//
//        return true;
//    }
//
//
//}
