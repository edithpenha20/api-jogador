package com.jogadores.controller;

import com.jogadores.controller.dto.JogadorDTO;
import com.jogadores.controller.exception.ApiErrors;
import com.jogadores.exceptions.BusinessException;
import com.jogadores.model.Jogador;
import com.jogadores.service.JogadorService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jogadores")
public class JogadorController {

    private JogadorService service;
    private ModelMapper modelMapper;

    public JogadorController(JogadorService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JogadorDTO create(@RequestBody @Valid JogadorDTO dto){
        Jogador entity = modelMapper.map(dto, Jogador.class);
        entity = service.save(entity);
        return modelMapper.map(entity, JogadorDTO.class);
    }

    @GetMapping("{id}")
    public JogadorDTO getById(@PathVariable Long id){
        return service.getById(id)
                .map(jogador -> modelMapper.map(jogador, JogadorDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJogador(@PathVariable Long id) {
        Jogador jogador = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.deleteJogador(jogador);
    }

    @PutMapping("{id}")
    public JogadorDTO updateJogador(@PathVariable Long id, @RequestBody @Valid JogadorDTO dto){
        return service.getById(id).map(jogador -> {
            jogador.setNome(dto.getNome());
            jogador.setEmail(dto.getEmail());
            jogador.setTelefone(dto.getTelefone());
            jogador.setCodinome(dto.getCodinome());
            jogador.setGrupo(dto.getGrupo());
            return modelMapper.map(jogador, JogadorDTO.class);

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<JogadorDTO> find(JogadorDTO dto, Pageable pageRequest ){
        Jogador filter = modelMapper.map(dto, Jogador.class);
        Page<Jogador> result = service.findJogador(filter, pageRequest);
        List<JogadorDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, JogadorDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<JogadorDTO>( list, pageRequest, result.getTotalElements() );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }
}
