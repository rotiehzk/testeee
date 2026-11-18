package com.starterkit.springboot.sall;

public class AlunoRequest {
    private String nome;
    private String email;
    private String telefone;
    private String dataPagamento; // formato yyyy-MM-dd
    private String dataVencimento; // formato yyyy-MM-dd
    private String plano;
    private Boolean activo;


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

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}   