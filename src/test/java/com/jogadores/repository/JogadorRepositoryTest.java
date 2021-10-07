package com.jogadores.repository;

import com.jogadores.model.Jogador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest ///cria uma instância do BD em memória e ao final dos testes apaga tudo
public class JogadorRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    JogadorRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um jogador na base de dados com codinome informado")
    public void retornarVerdadeiroQuandoCodinomeExistir(){

        //cenario
        String codinome = "Hulk";
        Jogador jogador = criarNovoJogador();
        entityManager.persist(jogador);

        //execucao
        boolean existe = repository.existsByCodinome(codinome);

        //verificacao
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um jogador na base de dados com codinome informado")
    public void retornarFalsoQuandoCodinomeExistir(){

        //cenario
        String codinome = "Hulk";

        //execucao
        boolean existe = repository.existsByCodinome(codinome);

        //verificacao
        assertThat(existe).isFalse();
    }

    private Jogador criarNovoJogador() {
        return Jogador.builder()
                .nome("Felipe")
                .email("felipe@email.com")
                .telefone("(11) 99999-9999")
                .codinome("Hulk")
                .grupo("Vingadores")
                .build();
    }
}
