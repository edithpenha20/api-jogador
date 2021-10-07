package com.jogadores.service.impl;

import com.jogadores.exceptions.BusinessException;
import com.jogadores.model.Jogador;
import com.jogadores.repository.JogadorRepository;
import com.jogadores.service.JogadorService;
import org.springframework.stereotype.Service;

@Service
public class JogadorServiceImpl implements JogadorService {

    private JogadorRepository repository;

    public JogadorServiceImpl(JogadorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Jogador save(Jogador jogador) {
        if (repository.existsByCodinome(jogador.getCodinome())){
            throw new BusinessException("Codinome já cadastrado");
        }
        return repository.save(jogador);
    }
}
