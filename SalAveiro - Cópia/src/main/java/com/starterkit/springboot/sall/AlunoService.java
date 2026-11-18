package com.starterkit.springboot.sall;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AlunoService {

    private final AlunoRepository repo;
    private final Path alunosUploadDir;

    public AlunoService(AlunoRepository repo, @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.repo = repo;
        this.alunosUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("alunos");
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(alunosUploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel criar a pasta de uploads", ex);
        }
    }

    public Aluno create(AlunoForm form) {
        Aluno aluno = new Aluno();
        aluno.setCodigoUnico(UUID.randomUUID().toString());
        applyForm(aluno, form);
        if (form.getImagemPath() != null && !form.getImagemPath().isEmpty()) {
            aluno.setImagemPath(form.getImagemPath());
        }
        return repo.save(aluno);
    }

    public Aluno update(Long id, AlunoForm form) {
        Aluno aluno = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno nao encontrado"));
        applyForm(aluno, form);
        if (form.getImagemPath() != null && !form.getImagemPath().isEmpty()) {
            aluno.setImagemPath(form.getImagemPath());
        }
        return repo.save(aluno);
    }

    private void applyForm(Aluno aluno, AlunoForm form) {
        aluno.setNome(form.getNome());
        aluno.setEmail(form.getEmail());
        aluno.setTelefone(form.getTelefone());
        if (form.getDataPagamento() != null && !form.getDataPagamento().trim().isEmpty()) {
            try {
                aluno.setDataPagamento(java.sql.Date.valueOf(form.getDataPagamento()));
            } catch (IllegalArgumentException ex) {
                // ignora data inválida
            }
        }
        if (form.getDataVencimento() != null && !form.getDataVencimento().trim().isEmpty()) {
            try {
                aluno.setDataVencimento(java.sql.Date.valueOf(form.getDataVencimento()));
            } catch (IllegalArgumentException ex) {
                // ignora data inválida
            }
        }
        if (form.getPlano() != null && !form.getPlano().trim().isEmpty()) {
            aluno.setPlano(form.getPlano());
        }
        if (form.getActivo() != null) {
            aluno.setActivo(form.getActivo());
        }
    }

    private String storeImage(MultipartFile imagem, String currentImagePath) {
        if (imagem == null || imagem.isEmpty() || imagem.getOriginalFilename() == null || imagem.getOriginalFilename().isEmpty()) {
            return currentImagePath;
        }

        String originalName = StringUtils.cleanPath(imagem.getOriginalFilename());
        String extension = getExtension(originalName);
        if (!isAllowedImageExtension(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de imagem nao suportado");
        }

        String generatedName = UUID.randomUUID().toString() + extension;
        Path destination = alunosUploadDir.resolve(generatedName).normalize();
        if (!destination.startsWith(alunosUploadDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome de ficheiro invalido");
        }

        try (InputStream inputStream = imagem.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao guardar a imagem");
        }

        deleteStoredImage(currentImagePath);
        return "alunos/" + generatedName;
    }

    private void deleteStoredImage(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return;
        }

        String relativePath = imagePath.replace('/', java.io.File.separatorChar);
        Path filePath = alunosUploadDir.getParent().resolve(relativePath).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // ignora erro ao eliminar
        }
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return fileName.substring(index).toLowerCase(Locale.ROOT);
    }

    private boolean isAllowedImageExtension(String extension) {
        return ".png".equals(extension)
                || ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".webp".equals(extension)
                || ".gif".equals(extension);
    }
}