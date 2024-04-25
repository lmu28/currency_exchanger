package org.example.controller.currency;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.HttpStatus;
import org.example.controller.dto.InfoResponse;
import org.example.dao.CurrencyRepoJDBC;
import org.example.model.Currency;
import org.example.service.CurrencyService;
import org.example.service.DBService;
import org.example.controller.dto.ResponseDTO;
import org.example.utils.JsonConverter;
import org.example.utils.RequestHandler;
import org.example.utils.ResponseHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService(new CurrencyRepoJDBC(DBService.getConnection()));


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ResponseDTO<List<Currency>> re = currencyService.findAll();

        String content = ResponseHandler.handle(resp,re);
//        resp.setStatus(re.getCode().getValue());
//        if (re.getCode().equals(HttpStatus.SERVER_ERROR)){
//            String content = JsonConverter.convertObjectToJson(new InfoResponse(re.getMessage()));
//            resp.getWriter().print(content);
//            return;
//        }
//        String content = JsonConverter.convertObjectToJson(re.getBody());
        resp.getWriter().print(content);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        String content = "";
        if (name == null || code == null || sign == null){
            String message = "The required form field is missing";
            content = ResponseHandler.handle(resp,HttpStatus.BAD_REQUEST, message);

        }else{
            ResponseDTO re = currencyService.save(new Currency(0,code,name,sign));
            content = ResponseHandler.handle(resp,re);
//            resp.setStatus(re.getCode().getValue());
//            content = JsonConverter.convertObjectToJson(new InfoResponse(re.getMessage()));

        }
        resp.getWriter().print(content);

    }
}
