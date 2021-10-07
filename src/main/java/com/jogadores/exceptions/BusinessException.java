package com.jogadores.exceptions;

import com.jogadores.model.Jogador;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }
}
