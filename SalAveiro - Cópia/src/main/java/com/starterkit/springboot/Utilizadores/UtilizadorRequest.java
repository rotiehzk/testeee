package com.starterkit.springboot.Utilizadores;

public class UtilizadorRequest {
    private String nome;
    private String email;
    private String nomeDoUser;
    private String password;
    private boolean estado;
    private String perfil;


    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomeDoUser() { return nomeDoUser; }
    public void setNomeDoUser(String nomeDoUser) { this.nomeDoUser = nomeDoUser; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean getEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    

    
}
