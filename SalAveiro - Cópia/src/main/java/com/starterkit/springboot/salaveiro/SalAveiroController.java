package com.starterkit.springboot.salaveiro;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/salaveiro")
public class SalAveiroController {

    private static final Map<String, Table> TABLES = Map.ofEntries(
            Map.entry("salinas", new Table("Salinas", "id_salina",
                    "nome, localizacao, capacidade_total")),
            Map.entry("tanques", new Table("Tanques", "id_tanque",
                    "area_util, capacidade_maxima_producao, salina_id, estado_funcionamento")),
            Map.entry("funcoes", new Table("Funcoes", "id_funcao",
                    "nome")),
            Map.entry("trabalhadores", new Table("Trabalhadores", "id_trabalhador",
                    "nome, contacto, email, funcao_id, data_admissao, estado_emprego")),
            Map.entry("perfis", new Table("Perfil", "id_perfil",
                    "nome")),
            Map.entry("utilizadores", new Table("Utilizador", "id_utilizador",
                    "trabalhador_id, perfil_id, email")),
            Map.entry("colheitas", new Table("Colheitas", "id_colheita",
                    "data_colheita, quantidade_produzida, tanque_id, trabalhadorAtividadeProduzida_id, utilizadorRegistroOp_id, observacoes")),
            Map.entry("categorias", new Table("Categorias", "id_categoria",
                    "nome, descricao, designacao")),
            Map.entry("produtos", new Table("Produtos", "id_produto",
                    "categoria_id, nome, peso_unitario, preco_venda, stock_disponivel, estado_produto")),
            Map.entry("clientes", new Table("Clientes", "id_cliente",
                    "nome, contacto, email, morada, estado_cliente, Numero_Identificacao_Fiscal")),
            Map.entry("encomendas", new Table("Encomendas", "id_encomenda",
                    "cliente_id, utilizador_id, data_encomenda, estado_encomenda, observacoes, total")),
            Map.entry("itens-encomenda", new Table("ItensEncomenda", "id_item_encomenda",
                    "encomenda_id, produto_id, quantidade, preco_unitario"))
    );

    private final JdbcTemplate jdbc;

    public SalAveiroController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/{recurso}")
    public List<Map<String, Object>> listar(@PathVariable("recurso") String recurso,
            @RequestParam(name = "limite", defaultValue = "50") int limite,
            @RequestParam(name = "offset", defaultValue = "0") long offset) {
        if (limite < 1 || limite > 100 || offset < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limite ou offset inválido");
        }
        Table table = findTable(recurso);
        return jdbc.queryForList(table.select() + " ORDER BY " + table.id() + " LIMIT ? OFFSET ?",
                limite, offset);
    }

    @GetMapping("/{recurso}/{id}")
    public Map<String, Object> consultar(@PathVariable("recurso") String recurso, @PathVariable("id") int id) {
        Table table = findTable(recurso);
        return jdbc.queryForList(table.select() + " WHERE " + table.id() + " = ?", id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registo inexistente"));
    }

    private Table findTable(String resource) {
        Table table = TABLES.get(resource);
        if (table == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso inexistente");
        }
        return table;
    }

    private record Table(String name, String id, String columns) {
        String select() {
            return "SELECT " + id + ", " + columns + " FROM " + name;
        }
    }
}
