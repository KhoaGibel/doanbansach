package com.example.doanbansach.Controller;

import com.example.doanbansach.dto.RegistrationDTO;
import com.example.doanbansach.service.PasswordMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.doanbansach.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") RegistrationDTO registrationDto, Model model) {
        try {
            userService.registerNewUser(registrationDto);

        } catch (PasswordMismatchException e) {
            model.addAttribute("passwordError", e.getMessage());
            model.addAttribute("user", registrationDto);
            return "register";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", registrationDto);
            return "register";
        }

        System.out.println("Đăng ký thành công cho người dùng: " + registrationDto.getUsername());
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }
}