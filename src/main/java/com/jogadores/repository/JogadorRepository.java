package com.jogadores.repository;

import com.jogadores.model.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    boolean existsByCodinome(String codinome);
}
