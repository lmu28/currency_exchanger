package org.example.controller.exchange;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.HttpStatus;
import org.example.controller.dto.ResponseDTO;
import org.example.dao.CurrencyRepoJDBC;
import org.example.dao.ExchangeRateRepoJDBC;
import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.example.service.CurrencyService;
import org.example.service.DBService;
import org.example.service.ExchangeRatesService;
import org.example.utils.ResponseHandler;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {


    private final ExchangeRatesService ratesService = new ExchangeRatesService(new ExchangeRateRepoJDBC(DBService.getConnection()));
    private final CurrencyService currencyService = new CurrencyService(new CurrencyRepoJDBC(DBService.getConnection()));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO<List<ExchangeRate>> re = ratesService.findAll();
        String content = ResponseHandler.handle(resp, re);
        resp.getWriter().print(content);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        String content = "";
        try {
            Currency currencyBase = currencyService.findByCode(baseCurrencyCode.toUpperCase()).getBody();
            Currency currencyTarget = currencyService.findByCode(targetCurrencyCode.toUpperCase()).getBody();

            if (currencyBase == null || currencyTarget == null) {
                String message = "One (or both) currencies from a currency pair do not exist";
                content = ResponseHandler.handle(resp, HttpStatus.NOT_FOUND, message);
            } else {
                ResponseDTO re = ratesService.save(new ExchangeRate(0,currencyBase,currencyTarget,Double.parseDouble(rate)));
                content = ResponseHandler.handle(resp,re);

            }

        }catch (RuntimeException e){
            String message = "The required form field is missing";
            content = ResponseHandler.handle(resp, HttpStatus.BAD_REQUEST, message);
        }



        resp.getWriter().print(content);
    }
}
