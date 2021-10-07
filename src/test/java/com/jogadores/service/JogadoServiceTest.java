package com.jogadores.service;

import com.jogadores.exceptions.BusinessException;
import com.jogadores.model.Jogador;
import com.jogadores.repository.JogadorRepository;
import com.jogadores.service.impl.JogadorServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class JogadoServiceTest {

    JogadorService service;

    @MockBean
    JogadorRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new JogadorServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um jogador")
    public void salvarJogadorTest(){

        //cenário
        Jogador jogador = criarJogadorValido();
        Jogador jogadorSalvoRepository = Jogador.builder()
                .id(1L)
                .nome("Felipe")
                .email("felipe@email.com")
                .telefone("(11) 99999-9999")
                .codinome("Hulk")
                .grupo("Vingadores")
                .build();

        Mockito.when(repository.existsByCodinome(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(jogador)).thenReturn(jogadorSalvoRepository);

        //execução
        Jogador jogadorSalvo = service.save(jogador);

        //verificação
        assertThat(jogadorSalvo.getId()).isNotNull();
        assertThat(jogadorSalvo.getNome()).isEqualTo("Felipe");
        assertThat(jogadorSalvo.getEmail()).isEqualTo("felipe@email.com");
        assertThat(jogadorSalvo.getTelefone()).isEqualTo("(11) 99999-9999");
        assertThat(jogadorSalvo.getCodinome()).isEqualTo("Hulk");
        assertThat(jogadorSalvo.getGrupo()).isEqualTo("Vingadores");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar salvar um Jogador com codinome duplicado")
    public void naoDeveSalvarJogadorComCodinomeCriado(){

        //cenario
        Jogador jogador = criarJogadorValido();
        Mockito.when(repository.existsByCodinome(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(jogador));

        //verificacoes
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Codinome já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(jogador);

    }

    private Jogador criarJogadorValido() {
        return Jogador.builder()
                .nome("Felipe")
                .email("felipe@email.com")
                .telefone("(11) 99999-9999")
                .codinome("Hulk")
                .grupo("Vingadores")
                .build();
    }
}
