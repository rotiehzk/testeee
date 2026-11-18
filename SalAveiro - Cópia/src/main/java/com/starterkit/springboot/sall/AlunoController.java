package com.starterkit.springboot.sall;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/alunos")
class AlunoController {

    private final AlunoRepository repo;
    private final java.nio.file.Path uploadDir;

    public AlunoController(AlunoRepository repo, @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.repo = repo;
        this.uploadDir = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize().resolve("alunos");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aluno create(@RequestBody AlunoRequest req) {
        validateRequired(req);
        Aluno a = new Aluno();
        applyRequest(a, req);
        return repo.save(a);
    }

    @GetMapping
    public List<Aluno> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Aluno get(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }

    @PutMapping("/{id}")
    public Aluno update(@PathVariable Long id, @RequestBody AlunoRequest req) {
        Aluno a = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        applyRequest(a, req);
        return repo.save(a);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado");
        }
        repo.deleteById(id);
    }

    @PostMapping("/{id}/imagem")
    public Aluno uploadImagem(@PathVariable Long id, @RequestParam("imagem") MultipartFile imagem) {
        Aluno aluno = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        if (imagem != null && !imagem.isEmpty()) {
            try {
                java.nio.file.Files.createDirectories(uploadDir);
                String ext = getExtension(imagem.getOriginalFilename());
                String filename = UUID.randomUUID().toString() + ext;
                java.nio.file.Path dest = uploadDir.resolve(filename).normalize();
                try (InputStream is = imagem.getInputStream()) {
                    java.nio.file.Files.copy(is, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                aluno.setImagemPath("alunos/" + filename);
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao guardar imagem");
            }
        }

        return repo.save(aluno);
    }

    @PostMapping("/upload-temp")
    public java.util.Map<String, String> uploadTemp(@RequestParam("imagem") MultipartFile imagem) {
        String path = null;
        if (imagem != null && !imagem.isEmpty()) {
            try {
                java.nio.file.Files.createDirectories(uploadDir);
                String ext = getExtension(imagem.getOriginalFilename());
                String filename = UUID.randomUUID().toString() + ext;
                java.nio.file.Path dest = uploadDir.resolve(filename).normalize();
                try (InputStream is = imagem.getInputStream()) {
                    java.nio.file.Files.copy(is, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                path = "alunos/" + filename;
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao guardar imagem");
            }
        }
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("path", path);
        return result;
    }

    private void validateRequired(AlunoRequest req) {
        if (isBlank(req.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nome é obrigatório");
        }
        if (isBlank(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email é obrigatório");
        }
    }

    private void applyRequest(Aluno a, AlunoRequest req) {
        if (req.getNome() != null) a.setNome(req.getNome());
        if (req.getEmail() != null) a.setEmail(req.getEmail());
        if (req.getTelefone() != null) a.setTelefone(req.getTelefone());
        if (req.getDataPagamento() != null && !req.getDataPagamento().trim().isEmpty()) {
            try {
                a.setDataPagamento(Date.valueOf(req.getDataPagamento()));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "dataPagamento inválida (formato esperado: yyyy-MM-dd)"
                );
            }
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(i).toLowerCase() : "";
    }

    }