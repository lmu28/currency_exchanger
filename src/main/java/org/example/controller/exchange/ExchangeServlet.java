package org.example.controller.exchange;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.HttpStatus;
import org.example.controller.dto.ExchangeDTO;
import org.example.controller.dto.ResponseDTO;
import org.example.dao.ExchangeRateRepoJDBC;
import org.example.service.DBService;
import org.example.service.ExchangeRatesService;
import org.example.utils.ResponseHandler;

import java.io.IOException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {


    private final ExchangeRatesService ratesService = new ExchangeRatesService(new ExchangeRateRepoJDBC(DBService.getConnection()));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountAsString = req.getParameter("amount");

        String content = "";
        try {
            double amount = Double.parseDouble(amountAsString);
            ResponseDTO<ExchangeDTO> re = ratesService.exchange(from,to,amount);
            content = ResponseHandler.handle(resp,re);
        }catch (RuntimeException e){
            String message = "The required form field is missing";
            content = ResponseHandler.handle(resp, HttpStatus.BAD_REQUEST, message);
        }


        resp.getWriter().print(content);
    }
}
