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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
