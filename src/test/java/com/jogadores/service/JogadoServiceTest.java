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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

        when(repository.existsByCodinome(Mockito.anyString())).thenReturn(false);
        when(repository.save(jogador)).thenReturn(jogadorSalvoRepository);

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
        when(repository.existsByCodinome(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(jogador));

        //verificacoes
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Codinome já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(jogador);

    }

    @Test
    @DisplayName("Deve obter um jogador por ID")
    public void obterJogadorPorIdTest() {
        Long id = 1L;
        Jogador jogador = criarJogadorValido();
        jogador.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(jogador));

        //execucao
        Optional<Jogador> encontrarJogador = service.getById(id);

        //verificacoes
        assertThat(encontrarJogador.isPresent()).isTrue();
        assertThat(encontrarJogador.get().getId()).isEqualTo(id);
        assertThat(encontrarJogador.get().getNome()).isEqualTo(jogador.getNome());
        assertThat(encontrarJogador.get().getEmail()).isEqualTo(jogador.getEmail());
        assertThat(encontrarJogador.get().getTelefone()).isEqualTo(jogador.getTelefone());
        assertThat(encontrarJogador.get().getCodinome()).isEqualTo(jogador.getCodinome());
        assertThat(encontrarJogador.get().getGrupo()).isEqualTo(jogador.getGrupo());
    }

    @Test
    @DisplayName("Deve retornar vazio quando um jogador por ID não existir")
    public void obterJogadorPorIdNaoEncontradoTest() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Jogador> encontrarJogador = service.getById(id);

        //verificacoes
        assertThat(encontrarJogador.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um jogador.")
    public void deleteJogadorTest(){
        Jogador jogador = Jogador.builder().id(1L).build();

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.deleteJogador(jogador) );

        //verificacoes
        Mockito.verify(repository, Mockito.times(1)).delete(jogador);
    }

    @Test
    @DisplayName("Deve apresentar erro ao tentar deletar um jogador inexistente.")
    public void deleteJogadorInvalidoTest(){
        Jogador jogador = new Jogador();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteJogador(jogador));

        //verificacoes
        Mockito.verify(repository, Mockito.never()).delete(jogador);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void atualizarJogadorTest(){
        //cenário
        long id = 1l;

        //livro a atualizar
        Jogador atualizandoJogador = Jogador.builder().id(id).build();

        //simulacao
        Jogador jogadorAtualizado = criarJogadorValido();
        jogadorAtualizado.setId(id);
        when(repository.save(atualizandoJogador)).thenReturn(jogadorAtualizado);

        //exeucao
        Jogador jogador = service.updateJogador(atualizandoJogador);

        //verificacoes
        assertThat(jogador.getId()).isEqualTo(jogadorAtualizado.getId());
        assertThat(jogador.getNome()).isEqualTo(jogadorAtualizado.getNome());
        assertThat(jogador.getEmail()).isEqualTo(jogadorAtualizado.getEmail());
        assertThat(jogador.getTelefone()).isEqualTo(jogadorAtualizado.getTelefone());
        assertThat(jogador.getCodinome()).isEqualTo(jogadorAtualizado.getCodinome());
        assertThat(jogador.getGrupo()).isEqualTo(jogadorAtualizado.getGrupo());

    }

    @Test
    @DisplayName("Deve apresentar erro ao tentar atualizar um jogador inexistente.")
    public void atualizarJogadorInvalidoTest(){
        Jogador jogador = new Jogador();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateJogador(jogador));

        Mockito.verify( repository, Mockito.never() ).save(jogador);
    }

    @Test
    @DisplayName("Deve filtrar jogadores pelas propriedades")
    public void findJogadorTest(){
        //cenario
        Jogador jogador = criarJogadorValido();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Jogador> lista = Arrays.asList(jogador);
        Page<Jogador> page = new PageImpl<Jogador>(lista, pageRequest, 1);
        when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Jogador> result = service.findJogador(jogador, pageRequest);


        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
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
