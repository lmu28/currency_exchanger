package org.example.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.HttpStatus;
import org.example.controller.dto.InfoResponse;
import org.example.controller.dto.ResponseDTO;

public class ResponseHandler {


    public static String handle(HttpServletResponse resp, ResponseDTO<?> responseDTO){
        HttpStatus status = responseDTO.getCode();
        resp.setStatus(status.getValue());
        String content = "";
        if (status.equals(HttpStatus.OK) || status.equals(HttpStatus.CREATED)){
            if (responseDTO.getBody() != null){
                content = JsonConverter.convertObjectToJson(responseDTO.getBody());
            }else{
                content = JsonConverter.convertObjectToJson(new InfoResponse(responseDTO.getMessage()));
            }
        } else {
            content = JsonConverter.convertObjectToJson(new InfoResponse(responseDTO.getMessage()));
        }
        return content;
    }



    public static String handle(HttpServletResponse resp, HttpStatus status, String message){
        resp.setStatus(status.getValue());
        String  content = JsonConverter.convertObjectToJson(new InfoResponse(message));
        return content;
    }
}
