package com.starterkit.springboot.Utilizadores;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UtilizadorService {
    private static final String CONSULTA = """
        SELECT u.id_utilizador, u.email, u.trabalhador_id, u.perfil_id,
               COALESCE(t.nome, u.email) AS nome, p.nome AS perfil, u.ativo = 1 AS ativo
        FROM Utilizador u
        LEFT JOIN Trabalhadores t ON t.id_trabalhador = u.trabalhador_id
        LEFT JOIN Perfil p ON p.id_perfil = u.perfil_id
        """;

    private final JdbcTemplate jdbc;

    public UtilizadorService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> pesquisar(String pesquisa) {
        String termo = pesquisa.strip();
        if (termo.isEmpty()) {
            return jdbc.queryForList(CONSULTA + "ORDER BY nome, u.id_utilizador");
        }
        return jdbc.queryForList(CONSULTA + """
            WHERE LOCATE(LOWER(?), LOWER(u.email)) > 0
               OR LOCATE(LOWER(?), LOWER(t.nome)) > 0
               OR LOCATE(LOWER(?), LOWER(p.nome)) > 0
            ORDER BY nome, u.id_utilizador
            """, termo, termo, termo);
    }

    public Map<String, Object> consultar(long id) {
        return jdbc.queryForList(CONSULTA + "WHERE u.id_utilizador = ?", id).stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilizador inexistente"));
    }

    public List<Map<String, Object>> trabalhadores() {
        return jdbc.queryForList("SELECT id_trabalhador AS id, nome FROM Trabalhadores ORDER BY nome");
    }

    public List<Map<String, Object>> perfis() {
        return jdbc.queryForList("SELECT id_perfil AS id, nome FROM Perfil ORDER BY nome");
    }

    public void criar(UtilizadorForm form) {
        jdbc.update("INSERT INTO Utilizador (email, trabalhador_id, perfil_id) VALUES (?, ?, ?)",
                form.getEmail(), form.getTrabalhadorId(), form.getPerfilId());
    }

    public void editar(long id, UtilizadorForm form) {
        if (jdbc.update("UPDATE Utilizador SET email = ?, trabalhador_id = ?, perfil_id = ? WHERE id_utilizador = ?",
                form.getEmail(), form.getTrabalhadorId(), form.getPerfilId(), id) == 0) {
            consultar(id);
        }
    }

    public void alterarEstado(long id, boolean ativo) {
        if (jdbc.update("UPDATE Utilizador SET ativo = ? WHERE id_utilizador = ?", ativo, id) == 0) {
            consultar(id);
        }
    }
}
