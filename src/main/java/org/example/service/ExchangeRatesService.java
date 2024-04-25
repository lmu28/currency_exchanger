package org.example.service;

import org.example.controller.HttpStatus;
import org.example.model.Currency;
import org.example.controller.dto.ExchangeDTO;
import org.example.controller.dto.ResponseDTO;
import org.example.dao.ExchangerRateRepository;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class ExchangeRatesService {
    public static final String SERVER_ERROR_MESSAGE = "Data base is unavailable";
    public static final String OK_MESSAGE = "Success";
    private final ExchangerRateRepository repository;

    public ExchangeRatesService(ExchangerRateRepository repository) {
        this.repository = repository;
    }

    public ResponseDTO<List<ExchangeRate>> findAll() {
        try {
            List<ExchangeRate> exchangeRates = repository.findAll();
            return ResponseDTO.<List<ExchangeRate>>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(exchangeRates).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.<List<ExchangeRate>>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }


    }

    public ResponseDTO<ExchangeRate> findByCodes(String baseCode, String targetCode) {
        try {
            ExchangeRate currency = repository.findByCodes(baseCode.toUpperCase(), targetCode.toUpperCase());
            if (currency == null) {
                String message = "The exchange rate for the pair was not found";
                return ResponseDTO.<ExchangeRate>builder().code(HttpStatus.NOT_FOUND).message(message).build();
            }
            return ResponseDTO.<ExchangeRate>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(currency).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.<ExchangeRate>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }

    public ResponseDTO save(ExchangeRate el) {
        try {
            repository.save(el);
            return ResponseDTO.builder().code(HttpStatus.CREATED).message(OK_MESSAGE).build();
        } catch (DataIntegrityException e) {
            System.out.println(e.getMessage());
            String message = "Currency pair with such codes is already exists";
            return ResponseDTO.builder().code(HttpStatus.CONFLICT).message(message).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }


    public ResponseDTO update(ExchangeRate el) {
        try {
            int result = repository.update(el);
            if (result > 0) {
                return ResponseDTO.builder().code(HttpStatus.OK).message(OK_MESSAGE).build();
            } else {
                String message = "The currency pair is missing from the database";
                return ResponseDTO.<ExchangeRate>builder().code(HttpStatus.NOT_FOUND).message(message).build();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
    }

    public ResponseDTO<ExchangeDTO> exchange(String baseCode, String targetCode, double amount)  {
        ResponseDTO<ExchangeDTO> answer = null;

        ResponseDTO<ExchangeRate> rateEntity = findByCodes(baseCode, targetCode);
        answer = handleResponseDTO(Set.of(HttpStatus.SERVER_ERROR,HttpStatus.OK),rateEntity, amount);
        if (answer != null ) return answer;

        ResponseDTO<ExchangeRate> reversedRateEntity = findByCodes(targetCode, baseCode);
        answer = handleResponseDTO(Set.of(HttpStatus.SERVER_ERROR,HttpStatus.OK),reversedRateEntity, amount);
        if (answer != null ) {
            ExchangeDTO body = answer.getBody();
            if (body != null) reverseExchangeRate(body);
            return answer;
        }

        try {
            List<ExchangeRate> ratesBasedUSD = repository.findByTargetCodesBasedUSD(baseCode.toUpperCase(),targetCode.toUpperCase());
            if (ratesBasedUSD.size() != 2){
                String message = "The currency pair is missing from the database";
                return ResponseDTO.<ExchangeDTO>builder().code(HttpStatus.NOT_FOUND).message(message).build();
            } else {
                ExchangeRate exchangeRate = buildExchangeRate(ratesBasedUSD, baseCode.toUpperCase());
                answer = handleResponseDTO(
                        Set.of(HttpStatus.OK),
                        ResponseDTO.<ExchangeRate>builder().code(HttpStatus.OK).message(OK_MESSAGE).body(exchangeRate).build(),
                        amount);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return ResponseDTO.<ExchangeDTO>builder().code(HttpStatus.SERVER_ERROR).message(SERVER_ERROR_MESSAGE).build();
        }
        return answer;
        // usd rub
        // usd eur

        // 95r  -- 0.95e
        // 100r  -- 1e
        // 1000r -- 10e  (eur rub amount=10)


    }

    private ExchangeRate buildExchangeRate(List<ExchangeRate> ratesBasedUSD, String baseCode) {
        ExchangeRate based = ratesBasedUSD.get(0);
        ExchangeRate target = ratesBasedUSD.get(1);
        if (!based.getTargetCurrency().getCode().equals(baseCode)){
            ExchangeRate tmp = based;
            based = target;
            target = tmp;
        }
        double rate = based.getRate()/ target.getRate();

        return new ExchangeRate(0, based.getTargetCurrency(), target.getTargetCurrency(),rate);

    }

    private void reverseExchangeRate(ExchangeDTO body) {
        double rate = body.getRate();
        Currency base = body.getBaseCurrency();
        Currency target = body.getTargetCurrency();
        double convertedRate = 1/rate;
        body.setRate(convertedRate);
        body.setBaseCurrency(target);
        body.setTargetCurrency(base);
        body.setConvertedAmount( body.getConvertedAmount() / rate * convertedRate );
    }


    private ResponseDTO<ExchangeDTO> handleResponseDTO(Set<HttpStatus> statuses, ResponseDTO<ExchangeRate> re, double amount) {
        HttpStatus status = re.getCode();
        if (statuses.contains(status)){
            ExchangeRate exchangeRate = re.getBody();
            ExchangeDTO exchangeDTO = null;
            if (exchangeRate != null) {
                Currency base = exchangeRate.getBaseCurrency();
                Currency target = exchangeRate.getTargetCurrency();
                double rate = exchangeRate.getRate();
                double convertedAmount = rate * amount;
                exchangeDTO = new ExchangeDTO(base, target, rate, amount, convertedAmount);
            }
            return ResponseDTO.<ExchangeDTO>builder()
                    .code(status)
                    .message(re.getMessage())
                    .body(exchangeDTO)
                    .build();

        }
        return null;

    }


}

