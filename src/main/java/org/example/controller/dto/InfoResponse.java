package org.example.controller.dto;

public class InfoResponse {

    private  String message;

    public InfoResponse() {
    }

    public InfoResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
