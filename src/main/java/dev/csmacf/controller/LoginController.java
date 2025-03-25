package dev.csmacf.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/logout")

    public String logoutget(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "redirect:/login?logout";
    }
    @PostMapping("/logout")
public String logoutpost(HttpServletRequest request) {
    SecurityContextHolder.clearContext();
    request.getSession().invalidate();
    return "redirect:/login?logout";
}
}
