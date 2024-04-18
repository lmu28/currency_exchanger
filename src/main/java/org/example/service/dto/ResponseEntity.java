package org.example.service.dto;

import org.example.controller.HttpStatus;

public class ResponseEntity<T> {

    private HttpStatus code;
    private String message;

    private T body;

    public ResponseEntity(HttpStatus code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public ResponseEntity() {
    }

    public ResponseEntity(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }



    public static class Builder<T>{
        ResponseEntity<T> re = new ResponseEntity<>();

        public Builder<T> body(T body){
            re.setBody(body);
            return this;
        }

        public Builder<T> message(String message){
            re.setMessage(message);
            return this;
        }
        public Builder<T> code(HttpStatus code){
            re.setCode(code);
            return this;
        }

        public ResponseEntity<T> build(){
            return re;
        }

    }
}
