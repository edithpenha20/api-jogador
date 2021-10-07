package com.jogadores.controller;

import com.jogadores.controller.dto.JogadorDTO;
import com.jogadores.controller.exception.ApiErrors;
import com.jogadores.exceptions.BusinessException;
import com.jogadores.model.Jogador;
import com.jogadores.service.JogadorService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
