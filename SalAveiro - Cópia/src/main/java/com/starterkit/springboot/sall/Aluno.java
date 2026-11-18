package com.starterkit.springboot.sall;

import java.sql.Date;
import java.time.LocalDateTime;
import javax.persistence.*;

/*

curl.exe -X POST http://localhost:8080/api/alunos -H "Content-Type: application/json" -H "Authorization: Bearer fe844afd1f50484dbe7c3830e7d53320" -d "{\"nome\":\"João Silva\",\"email\":\"joao@email.com\",\"telefone\":\"11999999999\",\"dataPagamento\":\"2026-06-01\"}"

*/


@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(length = 20)
    private String telefone;

    private Date dataPagamento;

    private Date dataVencimento;

    @Column(length = 100)
    private String plano;


    private Boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 100)
    private String codigoUnico;

    @Column(length = 255)
    private String imagemPath;


    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
    }


    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
        if (dataPagamento != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(dataPagamento);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 30);
            this.dataVencimento = new Date(cal.getTimeInMillis());
        }
    }

    public Date getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(Date dataVencimento) { this.dataVencimento = dataVencimento; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }


    public Boolean getActivo() { 
        if (activo != null && activo && dataVencimento != null) {
            java.util.Date hoje = new java.util.Date();
            if (hoje.after(dataVencimento)) {
                return false;
            }
        }
        return activo; 
    }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }

}