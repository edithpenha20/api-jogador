package com.jogadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogadores.controller.dto.JogadorDTO;
import com.jogadores.exceptions.BusinessException;
import com.jogadores.model.Jogador;
import com.jogadores.service.JogadorService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class JogadorControllerTest {

    static String JOGADOR_API = "/api/jogadores";

    @Autowired
    MockMvc mvc;

    @MockBean
    JogadorService service;

    @Test
    @DisplayName(("Deve criar um jogador com sucesso"))
    public void criarJogadorTest() throws Exception {

        JogadorDTO dto = criarNovoJogadorDTO();
        Jogador jogadorSalvo = Jogador.builder()
                .id(1L)
                .nome("Felipe")
                .email("felipe@email.com")
                .telefone("(11) 99999-9999")
                .codinome("Hulk")
                .grupo("Vingadores")
                .build();

        BDDMockito.given(service.save(Mockito.any(Jogador.class))).willReturn(jogadorSalvo);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(JOGADOR_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value("Felipe"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("felipe@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("telefone").value("(11) 99999-9999"))
                .andExpect(MockMvcResultMatchers.jsonPath("codinome").value("Hulk"))
                .andExpect(MockMvcResultMatchers.jsonPath("grupo").value("Vingadores"));
    }

    @Test
    @DisplayName(("Deve retornar um erro quando algum dos campos não for preenchido"))
    public void criarJogadorInvalidoTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new JogadorDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(JOGADOR_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(5)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um codinome duplicado")
    public void criarJogadorComCodinomeDuplicado() throws Exception {
        JogadorDTO dto = criarNovoJogadorDTO();

        String json = new ObjectMapper().writeValueAsString(dto);
        String menssagemErro = "Codinome já cadastrado.";
        BDDMockito.given(service.save(Mockito.any(Jogador.class))).willThrow(new BusinessException(menssagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(JOGADOR_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(menssagemErro));

    }

    @Test
    @DisplayName("Deve mostrar informaçoes de um jogador")
    public void obterDetalhesJogadorTest() throws Exception {

        //cenario ou given
        Long id = 1L;

        Jogador jogador = Jogador.builder()
                        .id(id)
                        .nome(criarNovoJogadorDTO().getNome())
                        .email(criarNovoJogadorDTO().getEmail())
                        .telefone(criarNovoJogadorDTO().getTelefone())
                        .codinome(criarNovoJogadorDTO().getCodinome())
                        .grupo(criarNovoJogadorDTO().getGrupo())
                        .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(jogador));

        //execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(JOGADOR_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(criarNovoJogadorDTO().getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(criarNovoJogadorDTO().getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("telefone").value(criarNovoJogadorDTO().getTelefone()))
                .andExpect(MockMvcResultMatchers.jsonPath("codinome").value(criarNovoJogadorDTO().getCodinome()))
                .andExpect(MockMvcResultMatchers.jsonPath("grupo").value(criarNovoJogadorDTO().getGrupo()))
        ;

    }

    @Test
    @DisplayName("Deve retornar não encontrado quando o jogador não existir'")
    public void jogadorNaoEncontradoTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(JOGADOR_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve apagar um jogador")
    public void deletarJogadorTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Jogador.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(JOGADOR_API.concat("/" + 1));

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found quando quando não encontrar jogador para apagar")
    public void deletarJogadorInexistenteTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(JOGADOR_API.concat("/" + 1));

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um jogador")
    public void atualizarJogadorTest() throws Exception{
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(criarNovoJogadorDTO());

        Jogador atualizandoJogador = Jogador.builder()
                .id(1L)
                .nome("Felipe")
                .email("lipe@email.com")
                .telefone("(11) 99999-9999")
                .codinome("Hulk")
                .grupo("Vingadores")
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(atualizandoJogador));
        Jogador jogadorAtualizado = Jogador.builder()
                .id(id)
                .nome("Felipe")
                .email("felipe@email.com")
                .telefone("(11) 99999-9999")
                .codinome("Hulk")
                .grupo("Vingadores")
                .build();

        BDDMockito.given(service.updateJogador(atualizandoJogador)).willReturn(jogadorAtualizado);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(JOGADOR_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(criarNovoJogadorDTO().getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(criarNovoJogadorDTO().getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("telefone").value(criarNovoJogadorDTO().getTelefone()))
                .andExpect(MockMvcResultMatchers.jsonPath("codinome").value(criarNovoJogadorDTO().getCodinome()))
                .andExpect(MockMvcResultMatchers.jsonPath("grupo").value(criarNovoJogadorDTO().getGrupo()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um jogador inexistente")
    public void atualizarJogadorInexistenteTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(criarNovoJogadorDTO());

        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(JOGADOR_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar jogador")
    public void findJogadoresTest() throws Exception{

        Long id = 1l;

        Jogador jogador = Jogador.builder()
                .id(id)
                .nome(criarNovoJogadorDTO().getNome())
                .email(criarNovoJogadorDTO().getEmail())
                .telefone(criarNovoJogadorDTO().getTelefone())
                .codinome(criarNovoJogadorDTO().getCodinome())
                .grupo(criarNovoJogadorDTO().getGrupo())
                .build();

        BDDMockito.given( service.findJogador(Mockito.any(Jogador.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Jogador>( Arrays.asList(jogador), PageRequest.of(0,10), 1 ));

        String queryString = String.format("?nome=%s&grupo=%s&page=0&size=10",
                jogador.getNome(), jogador.getGrupo());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(JOGADOR_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform( request )
                .andExpect(MockMvcResultMatchers.status().isOk() )
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1) )
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(10) )
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0))
        ;
    }

    private JogadorDTO criarNovoJogadorDTO() {
        return JogadorDTO.builder()
                .nome("Felipe")
                .email("felipe@email.com")
                .telefone("(11) 99999-9999")
                .codinome("Hulk")
                .grupo("Vingadores")
                .build();
    }
}
