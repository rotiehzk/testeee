package com.starterkit.springboot.sall;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/alunos")
public class AlunoPageController {

    private final AlunoRepository repo;
    private final AlunoService service;

    @Value("${security.api-key.admin:}")
    private String adminKey;

    public AlunoPageController(AlunoRepository repo, AlunoService service) {
        this.repo = repo;
        this.service = service;
    }

    @GetMapping
    public String lista(Model model, @RequestParam(required = false) String status) {
        List<Aluno> alunos = repo.findAll();
        model.addAttribute("titulo", "Gestão de Alunos");
        model.addAttribute("alunos", alunos);
        model.addAttribute("status", status);
        return "alunos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("titulo", "Novo Aluno");
        model.addAttribute("alunoForm", new AlunoForm());
        model.addAttribute("modo", "novo");
        model.addAttribute("planos", getPlanosValores());
        return "alunos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Aluno aluno = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        AlunoForm form = new AlunoForm();
        copyAlunoToForm(aluno, form);
        model.addAttribute("titulo", "Editar Aluno");
        model.addAttribute("aluno", aluno);
        model.addAttribute("alunoForm", form);
        model.addAttribute("modo", "editar");
        model.addAttribute("planos", getPlanosValores());
        return "alunos/form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Aluno aluno = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        model.addAttribute("titulo", "Dados do Aluno");
        model.addAttribute("aluno", aluno);
        model.addAttribute("pageScript", "/js/alunos.js");
        return "alunos/detalhe";
    }

    @GetMapping("/codigo/{codigo}")
    public String detalhePorCodigo(@PathVariable String codigo, Model model) {
        Aluno aluno = repo.findByCodigoUnico(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        model.addAttribute("titulo", "Dados do Aluno");
        model.addAttribute("aluno", aluno);
        model.addAttribute("pageScript", "/js/alunos.js");
        return "alunos/detalhe";
    }

    @GetMapping("/scan")
    public String scan(Model model) {
        model.addAttribute("titulo", "Ler QR Code");
        model.addAttribute("pageScript", "/js/alunos.js");
        return "alunos/scan";
    }

    @PostMapping
    public String criar(AlunoForm form, Model model) {
        try {
            if (!isValidAdminKey(form.getAdminApiKey())) {
                model.addAttribute("error", "Chave de administrador inválida");
                model.addAttribute("titulo", "Novo Aluno");
                model.addAttribute("modo", "novo");
                model.addAttribute("planos", getPlanosValores());
                return "alunos/form";
            }
            if (isBlank(form.getNome()) || isBlank(form.getEmail())) {
                model.addAttribute("error", "Nome e email são obrigatórios");
                model.addAttribute("titulo", "Novo Aluno");
                model.addAttribute("modo", "novo");
                model.addAttribute("planos", getPlanosValores());
                return "alunos/form";
            }
            service.create(form);
            return "redirect:/alunos?status=created";
        } catch (Exception ex) {
            model.addAttribute("error", "Erro ao criar aluno: " + ex.getMessage());
            model.addAttribute("titulo", "Novo Aluno");
            model.addAttribute("modo", "novo");
            model.addAttribute("planos", getPlanosValores());
            model.addAttribute("alunoForm", form);
            return "alunos/form";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, AlunoForm form, Model model) {
        Aluno aluno = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        if (!isValidAdminKey(form.getAdminApiKey())) {
            model.addAttribute("error", "Chave de administrador inválida");
            model.addAttribute("titulo", "Editar Aluno");
            model.addAttribute("modo", "editar");
            model.addAttribute("aluno", aluno);
            model.addAttribute("planos", getPlanosValores());
            return "alunos/form";
        }
        service.update(id, form);
        return "redirect:/alunos?status=updated";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, @RequestParam String adminApiKey) {
        if (!isValidAdminKey(adminApiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chave de administrador inválida");
        }
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado");
        }
        repo.deleteById(id);
        return "redirect:/alunos?status=deleted";
    }

    private boolean isValidAdminKey(String providedKey) {
        return providedKey != null && !providedKey.trim().isEmpty() && providedKey.equals(adminKey);
    }

    private Map<String, Double> getPlanosValores() {
        Map<String, Double> planos = new java.util.HashMap<>();
        planos.put("1x por semana", null);
        planos.put("2x por semana", null);
        planos.put("3x por semana", null);
        planos.put("Livre Transito", null);
        planos.put("Aula Avulsa", null);
        return planos;
    }

    private void copyAlunoToForm(Aluno aluno, AlunoForm form) {
        form.setNome(aluno.getNome());
        form.setEmail(aluno.getEmail());
        form.setTelefone(aluno.getTelefone());
        if (aluno.getDataPagamento() != null) {
            form.setDataPagamento(aluno.getDataPagamento().toString());
        }
        if (aluno.getDataVencimento() != null) {
            form.setDataVencimento(aluno.getDataVencimento().toString());
        }

        form.setPlano(aluno.getPlano());
        form.setActivo(aluno.getActivo());
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}