package org.example.service;

import org.example.controller.HttpStatus;
import org.example.controller.dto.ResponseDTO;
import org.example.dao.CurrencyRepository;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    public static final String SERVER_ERROR_MESSAGE = "Data base is unavailable";
    public static final String OK_MESSAGE = "Success";
    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository cr) {
       this.repository = cr;
    }


    
    public ResponseDTO<List<Currency>> findAll() {
        try {
            List<Currency> currencies = repository.findAll();
            return ResponseDTO.<List<Currency>>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(currencies).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.<List<Currency>>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }


    }

    public ResponseDTO save(Currency el) {
        try {
            repository.save(el);
            return ResponseDTO.builder().code(HttpStatus.CREATED).message(OK_MESSAGE).build();
        }
        catch (DataIntegrityException e){
            System.out.println(e.getMessage());
            String message = "Currency with such code is already exists";
            return ResponseDTO.builder().code(HttpStatus.CONFLICT).message(message).build();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }


    public ResponseDTO<Currency> findByCode(String code) {
        try {
            Currency currency =  repository.findByCode(code.toUpperCase());
            if (currency == null){
                String message = "Currency not found";
                return ResponseDTO.<Currency>builder().code(HttpStatus.NOT_FOUND).message(message).build();
            }
            return ResponseDTO.<Currency>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(currency).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.<Currency>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }
}

