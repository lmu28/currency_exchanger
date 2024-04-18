package org.example.service;

import org.example.controller.HttpStatus;
import org.example.controller.dto.ResponseEntity;
import org.example.dao.CRUDRepository;
import org.example.dao.ExchangeRateRepoJDBC;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRatesService {
    public static final String SERVER_ERROR = "База данных недоступна";
    public static final String OK_MESSAGE = "Оk";
    private final CRUDRepository<ExchangeRate> repository;

    public ExchangeRatesService() {
       this.repository = new ExchangeRateRepoJDBC(DBService.getConnection());
    }


    
    public ResponseEntity<List<ExchangeRate>> findAll() {
        try {
            List<ExchangeRate> exchangeRates = repository.findAll();
            return ResponseEntity.<List<ExchangeRate>>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(exchangeRates).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.<List<ExchangeRate>>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR).build();
        }


    }
    
    public ResponseEntity save(ExchangeRate el) {
        try {
            repository.save(el);
            return ResponseEntity.builder().code(HttpStatus.OK).message(OK_MESSAGE).build();
        }
        catch (DataIntegrityException e){
            System.out.println(e.getMessage());
            String message = "Валютная пара с таким кодом уже существует";
            return ResponseEntity.builder().code(HttpStatus.CONFLICT).message(message).build();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR).build();
        }
    }
//
//
//    public ResponseEntity<Currency> findByCode(String code) {
//        try {
//            Currency currency =  repository.findByCode(code);
//            if (currency == null){
//                String message = "Валюта не найдена";
//                return ResponseEntity.<Currency>builder().code(404).message(message).build();
//            }
//            return ResponseEntity.<Currency>builder().code(200).message(OK_MESSAGE).body(currency).build();
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//            return ResponseEntity.<Currency>builder().code(500).message(SERVER_ERROR).build();
//        }
//    }
}

