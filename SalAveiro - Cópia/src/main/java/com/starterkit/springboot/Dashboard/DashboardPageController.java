package com.starterkit.springboot.Dashboard;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller 
@RequestMapping("/dashboard")
public class DashboardPageController {

    private final DashboardService dashboardService;

    public DashboardPageController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("titulo", "Dashboard");
        carregarResumo(model, session);
        return "dashboard";
    }

    private void carregarResumo(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Map<String, Object> utilizador = dashboardService.utilizadorPorEmail(username).orElse(Map.of());
        Object perfil = utilizador.get("perfil");
        model.addAttribute("nomeUtilizador", utilizador.getOrDefault("nome", username));
        model.addAttribute("perfilUtilizador", perfil != null ? perfil : session.getAttribute("role"));
        model.addAttribute("resumo", dashboardService.resumo());
        model.addAttribute("ultimasColheitas", dashboardService.ultimasColheitas());
    }
    
    
    
}
