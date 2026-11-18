package com.starterkit.springboot.Dashboard;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service 

public class DashboardService {
    private final JdbcTemplate jdbcTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> resumo() {
        return jdbcTemplate.queryForMap("""
            SELECT (SELECT COUNT(*) FROM Salinas) AS salinas,
                   (SELECT COUNT(*) FROM Trabalhadores) AS trabalhadores,
                   (SELECT COUNT(*) FROM Colheitas) AS colheitas,
                   (SELECT COALESCE(SUM(quantidade_produzida), 0) FROM Colheitas) AS totalProduzido,
                   (SELECT COUNT(*) FROM Produtos) AS produtos,
                   (SELECT COUNT(*) FROM Clientes) AS clientes,
                   (SELECT COUNT(*) FROM Encomendas) AS encomendas
            """);
    }

    public Optional<Map<String, Object>> utilizadorPorEmail(String email) {
        return jdbcTemplate.queryForList("""
            SELECT COALESCE(t.nome, u.email) AS nome, p.nome AS perfil
            FROM Utilizador u
            LEFT JOIN Trabalhadores t ON t.id_trabalhador = u.trabalhador_id
            LEFT JOIN Perfil p ON p.id_perfil = u.perfil_id
            WHERE u.email = ?
            ORDER BY u.id_utilizador
            LIMIT 1
            """, email).stream().findFirst();
    }

    public List<Map<String, Object>> ultimasColheitas() {
        return jdbcTemplate.queryForList("""
            SELECT c.data_colheita, s.nome AS salina, c.tanque_id,
                   c.quantidade_produzida, t.nome AS trabalhador
            FROM Colheitas c
            LEFT JOIN Tanques ta ON ta.id_tanque = c.tanque_id
            LEFT JOIN Salinas s ON s.id_salina = ta.salina_id
            LEFT JOIN Trabalhadores t ON t.id_trabalhador = c.trabalhadorAtividadeProduzida_id
            ORDER BY c.data_colheita DESC, c.id_colheita DESC
            LIMIT 5
            """);
    }

   public List<Map<String, Object>> listarUtilizadores() {
    return jdbcTemplate.queryForList("""
        SELECT u.id_utilizador, u.email, COALESCE(t.nome, u.email) AS nome, p.nome AS perfil
        FROM Utilizador u
        LEFT JOIN Trabalhadores t ON t.id_trabalhador = u.trabalhador_id
        LEFT JOIN Perfil p ON p.id_perfil = u.perfil_id
        ORDER BY u.id_utilizador
        """);
}
    public long contarId(String Salinas) {
        return jdbcTemplate.queryForObject(
        "SELECT COUNT(id_salina) FROM Salinas",
        long.class);
    }


        
    }
