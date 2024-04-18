package org.example.service;

import org.example.controller.HttpStatus;
import org.example.service.dto.ResponseEntity;
import org.example.dao.CurrencyRepository;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    public static final String SERVER_ERROR_MESSAGE = "База данных недоступна";
    public static final String OK_MESSAGE = "Успех";
    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository cr) {
       this.repository = cr;
    }


    
    public ResponseEntity<List<Currency>> findAll() {
        try {
            List<Currency> currencies = repository.findAll();
            return ResponseEntity.<List<Currency>>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(currencies).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.<List<Currency>>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }


    }

    public ResponseEntity save(Currency el) {
        try {
            repository.save(el);
            return ResponseEntity.builder().code(HttpStatus.CREATED).message(OK_MESSAGE).build();
        }
        catch (DataIntegrityException e){
            System.out.println(e.getMessage());
            String message = "Валюта с таким кодом уже существует";
            return ResponseEntity.builder().code(HttpStatus.CONFLICT).message(message).build();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }


    public ResponseEntity<Currency> findByCode(String code) {
        try {
            Currency currency =  repository.findByCode(code);
            if (currency == null){
                String message = "Валюта не найдена";
                return ResponseEntity.<Currency>builder().code(HttpStatus.NOT_FOUND).message(message).build();
            }
            return ResponseEntity.<Currency>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(currency).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.<Currency>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }
}

