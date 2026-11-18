package com.starterkit.springboot.Dashboard;


public class DashboardForm {
    private String nome;
    private String perfil;
    private String ultimo_acesso;
    private String salinas_totais;
    private String trabalhadores_totais;
    private String colheitas_totais;
    private String quantidade_produzidas;
    private String produtos;
    private String clientes;
    private String encomendas;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getUltimo_acesso() { return ultimo_acesso; }
    public void setUltimo_acesso(String ultimo_acesso) { this.ultimo_acesso = ultimo_acesso; }

    public String getSalinas_totais() { return salinas_totais; }
    public void setSalinas_totais(String salinas_totais) { this.salinas_totais = salinas_totais; }

    public String getTrabalhadores_totais() { return trabalhadores_totais; }
    public void setTrabalhadores_totais(String trabalhadores_totais) { this.trabalhadores_totais = trabalhadores_totais; }

    public String getColheitas_totais() { return colheitas_totais; }
    public void setColheitas_totais(String colheitas_totais) { this.colheitas_totais = colheitas_totais; }

    public String getQuantidade_produzidas() { return quantidade_produzidas; }
    public void setQuantidade_produzidas(String quantidade_produzidas) { this.quantidade_produzidas = quantidade_produzidas; }

    public String getProdutos() { return produtos; }
    public void setProdutos(String produtos) { this.produtos = produtos; }

    public String getClientes() { return clientes; }
    public void setClientes(String clientes) { this.clientes = clientes; }

    public String getEncomendas() { return encomendas; }
    public void setEncomendas(String encomendas) { this.encomendas = encomendas; }
}
