package com.starterkit.springboot.Utilizadores;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/salaveiro/utilizadores")
public class UtilizadorController {

    private final UtilizadorService service;

    public UtilizadorController(UtilizadorService service) {
        this.service = service;
    }

    @GetMapping("/pesquisa")
    public List<Map<String, Object>> pesquisar(
            @RequestParam(name = "q", defaultValue = "") String pesquisa) {
        return service.pesquisar(pesquisa);
    }
    

}
