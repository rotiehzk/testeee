package com.starterkit.springboot.sall;

public class AlunoForm {

    private String nome;
    private String email;
    private String telefone;
    private String dataPagamento;
    private String dataVencimento;
    private Boolean activo;
    private String plano;
 
    private String imagemPath;
    private String adminApiKey;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(String dataPagamento) { this.dataPagamento = dataPagamento; }

    public String getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(String dataVencimento) { this.dataVencimento = dataVencimento; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }


    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getAdminApiKey() { return adminApiKey; }
    public void setAdminApiKey(String adminApiKey) { this.adminApiKey = adminApiKey; }
}