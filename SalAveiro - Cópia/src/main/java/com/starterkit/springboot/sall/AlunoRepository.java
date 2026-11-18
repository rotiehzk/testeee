package com.starterkit.springboot.sall;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByCodigoUnico(String codigoUnico);
}