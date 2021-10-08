package com.jogadores.service;

import com.jogadores.model.Jogador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface JogadorService {
    Jogador save(Jogador any);

    Optional<Jogador> getById(Long id);

    void deleteJogador(Jogador jogador);

    Jogador updateJogador(Jogador jogador);

    Page<Jogador> findJogador(Jogador filter, Pageable pageRequest);
}
