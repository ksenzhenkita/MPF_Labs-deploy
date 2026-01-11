package sumdu.edu.ua.web.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.dto.RegisterUserDto;
import sumdu.edu.ua.core.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegisterUserDto());
        return "sign-up";
    }

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("user") @Valid RegisterUserDto dto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }

        try {
            userService.register(dto);
            return "sign-up-success";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "sign-up";
        }
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token, Model model) {
        boolean ok = userService.verifyAccount(token);
        if (ok) {
            return "confirm-success";
        } else {
            return "confirm-error";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "sign-in";
    }
}
