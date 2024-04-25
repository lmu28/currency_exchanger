package org.example.controller.dto;

import org.example.controller.HttpStatus;

public class ResponseDTO<T> {

    private HttpStatus code;
    private String message;

    private T body;

    public ResponseDTO(HttpStatus code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public ResponseDTO() {
    }

    public ResponseDTO(HttpStatus code, String message) {
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
        ResponseDTO<T> re = new ResponseDTO<>();

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

        public ResponseDTO<T> build(){
            return re;
        }

    }
}
