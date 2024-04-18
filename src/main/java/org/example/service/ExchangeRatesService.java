package org.example.service;

import org.example.controller.HttpStatus;
import org.example.model.Currency;
import org.example.service.dto.ExchangeDTO;
import org.example.service.dto.ResponseEntity;
import org.example.dao.ExchangerRateRepository;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRatesService {
    public static final String SERVER_ERROR_MESSAGE = "База данных недоступна";
    public static final String OK_MESSAGE = "Успех";
    private final ExchangerRateRepository repository;

    public ExchangeRatesService(ExchangerRateRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<List<ExchangeRate>> findAll() {
        try {
            List<ExchangeRate> exchangeRates = repository.findAll();
            return ResponseEntity.<List<ExchangeRate>>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(exchangeRates).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.<List<ExchangeRate>>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }


    }

    public ResponseEntity<ExchangeRate> findByCodes(String baseCode, String targetCode) {
        try {
            ExchangeRate currency =  repository.findByCodes(baseCode,targetCode);
            if (currency == null){
                String message = "Обменный курс для пары не найден";
                return ResponseEntity.<ExchangeRate>builder().code(HttpStatus.NOT_FOUND).message(message).build();
            }
            return ResponseEntity.<ExchangeRate>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(currency).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.<ExchangeRate>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }
    
    public ResponseEntity save(ExchangeRate el) {
        try {
            repository.save(el);
            return ResponseEntity.builder().code(HttpStatus.CREATED).message(OK_MESSAGE).build();
        }
        catch (DataIntegrityException e){
            System.out.println(e.getMessage());
            String message = "Валютная пара с таким кодом уже существует";
            return ResponseEntity.builder().code(HttpStatus.CONFLICT).message(message).build();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }


    public ResponseEntity update(ExchangeRate el) {
        try {
            int result = repository.update(el);
            if (result > 0){
                return ResponseEntity.builder().code(HttpStatus.OK).message(OK_MESSAGE).build();
            }else {
                String message = "Валютная пара отсутствует в базе данных";
                return ResponseEntity.<ExchangeRate>builder().code(HttpStatus.NOT_FOUND).message(message).build();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }

    public ResponseEntity<ExchangeDTO> exchange(String baseCode, String targetCode, double amount){

        ResponseEntity<ExchangeRate> exchangeRateEntity = findByCodes(baseCode,targetCode);

        // TODO: maybe handle high-level cases like it's in findByCodes
        HttpStatus excCode = exchangeRateEntity.getCode();
        String message = exchangeRateEntity.getMessage();
        if (excCode.equals(HttpStatus.SERVER_ERROR) ||
        excCode.equals(HttpStatus.NOT_FOUND)){

            return ResponseEntity.<ExchangeDTO>builder()
                    .code(excCode)
                    .message(message)
                    .body(null)
                    .build();
        }
        ExchangeRate exchangeRate = exchangeRateEntity.getBody();


        Currency base = exchangeRate.getBaseCurrency();
        Currency target = exchangeRate.getTargetCurrency();
        double rate = exchangeRate.getRate();
        double convertedAmount = rate * amount;
        return ResponseEntity.<ExchangeDTO>builder()
                .code(excCode)
                .message(message)
                .body( new ExchangeDTO(base,target,rate,amount,convertedAmount))
                .build();
    }


}

