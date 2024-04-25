package org.example.controller.currency;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.HttpStatus;
import org.example.controller.dto.InfoResponse;
import org.example.controller.dto.ResponseDTO;
import org.example.dao.CurrencyRepoJDBC;
import org.example.model.Currency;
import org.example.service.CurrencyService;
import org.example.service.DBService;
import org.example.utils.JsonConverter;
import org.example.utils.ResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name ="CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService(new CurrencyRepoJDBC(DBService.getConnection()));


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo  = req.getPathInfo();
        String code = parsePathInfo(pathInfo);
        String content = "";
        if(code.length() != 3){
            String message = "Currency code is missing from the address";
            content = ResponseHandler.handle(resp,HttpStatus.BAD_REQUEST,message);
        } else {
            ResponseDTO<Currency> re  =  currencyService.findByCode(code.toUpperCase());
            content = ResponseHandler.handle(resp,re);

//            resp.setStatus(re.getCode().getValue());
//            if (re.getCode().equals(HttpStatus.OK) ){
//                content = JsonConverter.convertObjectToJson(re.getBody());
//            } else {
//                content = JsonConverter.convertObjectToJson(
//                        new InfoResponse(re.getMessage())
//                );
//            }
        }


        resp.getWriter().print(content);
    }


    private String parsePathInfo(String path) {
        if (path == null){
            return  null;
        }else {
            return  path.split("/")[1];
        }
    }
}
