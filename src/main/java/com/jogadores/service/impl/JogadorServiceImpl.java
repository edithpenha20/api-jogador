package com.jogadores.service.impl;

import com.jogadores.exceptions.BusinessException;
import com.jogadores.model.Jogador;
import com.jogadores.repository.JogadorRepository;
import com.jogadores.service.JogadorService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional<Jogador> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void deleteJogador(Jogador jogador) {
        if (jogador == null || jogador.getId() == null){
            throw new IllegalArgumentException("O ID do jogador não pode ser nulo.");
        }

        this.repository.delete(jogador);
    }

    @Override
    public Jogador updateJogador(Jogador jogador) {
        if (jogador == null || jogador.getId() == null){
            throw new IllegalArgumentException("O ID do jogador não pode ser nulo.");
        }

        return this.repository.save(jogador);
    }

    @Override
    public Page<Jogador> findJogador(Jogador filter, Pageable pageRequest) {
        Example<Jogador> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example, pageRequest);
    }
}
