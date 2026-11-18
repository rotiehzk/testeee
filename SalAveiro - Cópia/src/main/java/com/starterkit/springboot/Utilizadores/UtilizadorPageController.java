package com.starterkit.springboot.Utilizadores;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/utilizadores")
public class UtilizadorPageController {
    private final UtilizadorService service;

    public UtilizadorPageController(UtilizadorService service) {
        this.service = service;
    }

    @GetMapping
    public String lista(@RequestParam(name = "q", defaultValue = "") String pesquisa, Model model) {
        model.addAttribute("pesquisa", pesquisa);
        model.addAttribute("utilizadores", service.pesquisar(pesquisa));
        return "utilizadores";
    }

    @GetMapping("/{id}")
    public String consultar(@PathVariable("id") long id, Model model) {
        model.addAttribute("utilizador", service.consultar(id));
        return "utilizadores";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new UtilizadorForm());
        return formulario(model, null);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") long id, Model model) {
        Map<String, Object> utilizador = service.consultar(id);
        UtilizadorForm form = new UtilizadorForm();
        form.setEmail((String) utilizador.get("email"));
        Number trabalhador = (Number) utilizador.get("trabalhador_id");
        Number perfil = (Number) utilizador.get("perfil_id");
        form.setTrabalhadorId(trabalhador == null ? null : trabalhador.longValue());
        form.setPerfilId(perfil == null ? null : perfil.longValue());
        model.addAttribute("form", form);
        return formulario(model, id);
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") UtilizadorForm form, BindingResult erros, Model model) {
        if (!erros.hasErrors()) {
            try {
                service.criar(form);
                return "redirect:/utilizadores";
            } catch (DataIntegrityViolationException e) {
                erros.reject("utilizador.invalido", "Não foi possível criar o utilizador. Verifica o email e as associações.");
            }
        }
        return formulario(model, null);
    }

    @PostMapping("/{id}/editar")
    public String guardar(@PathVariable("id") long id, @Valid @ModelAttribute("form") UtilizadorForm form,
            BindingResult erros, Model model) {
        if (!erros.hasErrors()) {
            try {
                service.editar(id, form);
                return "redirect:/utilizadores/" + id;
            } catch (DataIntegrityViolationException e) {
                erros.reject("utilizador.invalido", "Não foi possível guardar o utilizador. Verifica o email e as associações.");
            }
        }
        return formulario(model, id);
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable("id") long id) {
        service.alterarEstado(id, false);
        return "redirect:/utilizadores/" + id;
    }

    @PostMapping("/{id}/reativar")
    public String reativar(@PathVariable("id") long id) {
        service.alterarEstado(id, true);
        return "redirect:/utilizadores/" + id;
    }

    private String formulario(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("trabalhadores", service.trabalhadores());
        model.addAttribute("perfis", service.perfis());
        return "utilizadores";
    }
}
