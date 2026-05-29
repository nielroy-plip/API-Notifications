package com.example.notificacoesapi.exception;

public record FieldValidationError(
    String field,
    String message,
    Object rejectedValue
) {
}
