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

import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um jogador quando buscar por ID")
    public void encontrarJogadorPorId(){
        //cenario
        Jogador jogador = criarNovoJogador();
        entityManager.persist(jogador);

        //execucao
        Optional<Jogador> encontrarJogador = repository.findById(jogador.getId());

        //verificacoes
        assertThat(encontrarJogador.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um jogador")
    public void salvarJogadorTest(){
        Jogador jogador = criarNovoJogador();

        Jogador jogadorSalvo = repository.save(jogador);

        assertThat(jogadorSalvo.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um jogador.")
    public void deletarJogadorTest(){
        Jogador jogador = criarNovoJogador();
        entityManager.persist(jogador);
        Jogador encontrarJogador = entityManager.find(Jogador.class, jogador.getId());

        repository.delete((encontrarJogador));

        Jogador jogadorDeletado = entityManager.find(Jogador.class, jogador.getId());
        assertThat(jogadorDeletado).isNull();
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
