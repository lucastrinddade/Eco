package com.ecorecicla.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata erros de validação do @Valid (ex: campo obrigatório faltando).
     * Retorna 400 Bad Request com detalhes dos campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> camposErros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            camposErros.put(campo, mensagem);
        });

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", LocalDateTime.now().toString());
        resposta.put("status", 400);
        resposta.put("erro", "Dados inválidos");
        resposta.put("campos", camposErros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    /**
     * Trata erros internos genéricos.
     * Retorna 500 Internal Server Error com mensagem amigável.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", LocalDateTime.now().toString());
        resposta.put("status", 500);
        resposta.put("erro", "Erro interno do servidor");
        resposta.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}
