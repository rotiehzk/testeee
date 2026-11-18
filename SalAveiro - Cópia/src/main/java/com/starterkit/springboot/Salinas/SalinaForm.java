package com.starterkit.springboot.Utilizadores;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class UtilizadorForm {
    @NotBlank(message = "Indica o email.")
    @Email(message = "Indica um email válido.")
    private String email;

    @NotNull(message = "Seleciona o trabalhador.")
    @Positive
    private Long trabalhadorId;

    @NotNull(message = "Seleciona o perfil.")
    @Positive
    private Long perfilId;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.strip(); }

    public Long getTrabalhadorId() { return trabalhadorId; }
    public void setTrabalhadorId(Long trabalhadorId) { this.trabalhadorId = trabalhadorId; }

    public Long getPerfilId() { return perfilId; }
    public void setPerfilId(Long perfilId) { this.perfilId = perfilId; }
}
