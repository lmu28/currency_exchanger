package org.example.controller.exchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.HttpStatus;
import org.example.controller.dto.ResponseDTO;
import org.example.dao.ExchangeRateRepoJDBC;
import org.example.model.ExchangeRate;
import org.example.service.DBService;
import org.example.service.ExchangeRatesService;
import org.example.utils.ResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRatesService ratesService = new ExchangeRatesService(new ExchangeRateRepoJDBC(DBService.getConnection()));

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String content = "";
        List<String> parsedPathInfo;
        try {
            parsedPathInfo = parsePathInfo(pathInfo);
            String code1 = parsedPathInfo.get(0).toUpperCase();
            String code2 = parsedPathInfo.get(1).toUpperCase();
            ResponseDTO<ExchangeRate> re = ratesService.findByCodes(code1, code2);
            content = ResponseHandler.handle(resp, re);
        } catch (RuntimeException e) {
            String message = "Currency code is missing from the address";
            content = ResponseHandler.handle(resp, HttpStatus.BAD_REQUEST, message);
        }
        resp.getWriter().print(content);
    }


    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double rate;

        String content = "";
        try {
            String rateAsString  = req.getReader().readLine();
            rateAsString = rateAsString.replace("rate=","" );
            rate = Double.parseDouble(rateAsString);
        }catch (RuntimeException e){
            String message = "The required form field is missing";
            content = ResponseHandler.handle(resp, HttpStatus.BAD_REQUEST, message);
            resp.getWriter().print(content);
            return;
        }

        String pathInfo = req.getPathInfo();
        List<String> parsedPathInfo;
        try {
            parsedPathInfo = parsePathInfo(pathInfo);
            String code1 = parsedPathInfo.get(0);
            String code2 = parsedPathInfo.get(1);
            ExchangeRate exchangeRate = ratesService.findByCodes(code1,code2).getBody();

            if (exchangeRate == null){
                String message = "The currency pair is missing from the database";
                content = ResponseHandler.handle(resp, HttpStatus.NOT_FOUND, message);
            }else {
                exchangeRate.setRate(rate);
                ResponseDTO re = ratesService.update(exchangeRate);
                content = ResponseHandler.handle(resp, re);
            }
        } catch (RuntimeException e) {
            String message = "Currency code is missing from the address";
            content = ResponseHandler.handle(resp, HttpStatus.BAD_REQUEST, message);
        }
        resp.getWriter().print(content);

    }


    private List<String> parsePathInfo(String path) {
        if (path == null) {
            return new ArrayList<>();
        } else {
            String currencies = path.split("/")[1];
            if (currencies.length() != 6) throw  new RuntimeException();
            String curr1 = currencies.substring(0, 3);
            String curr2 = currencies.substring(3, 6);

            return List.of(curr1, curr2);
        }
    }
}
