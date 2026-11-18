package com.starterkit.springboot.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class LoginController {

    @Value("${security.api-key.admin:}")
    private String adminKey;

    @Value("${security.admin.username:admin}")
    private String adminUsername;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("titulo", "Login");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletRequest request,
                        Model model) {
        

        if (adminUsername.equals(username) && !adminKey.isBlank() && adminKey.equals(password)) {
            request.changeSessionId();
            session.setAttribute("authenticated", true);
            session.setAttribute("username", username);
            session.setAttribute("role", "ADMIN");
            return "redirect:/dashboard";
        }
        
        model.addAttribute("error", "Credenciais inválidas");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
