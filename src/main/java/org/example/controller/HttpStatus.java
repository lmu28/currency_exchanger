package org.example.controller;

public enum HttpStatus {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    CONFLICT(409),
    SERVER_ERROR(500);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
