package dev.csmacf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/students";
    }
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "logout"; 
    }
}
