package org.example.service;


import org.example.controller.HttpStatus;
import org.example.dao.ExchangerRateRepository;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.example.controller.dto.ExchangeDTO;
import org.example.controller.dto.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ExchangeRatesServiceTest {


    private ExchangeRatesService exchangeRatesService;

    @Mock
    private ExchangerRateRepository repo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        exchangeRatesService = spy(new ExchangeRatesService(repo));
    }


    @Test
    void findAll_RepoReturnsList_ReturnsResponseEntity() throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        doReturn(exchangeRates).when(repo).findAll();
        ResponseDTO<List<ExchangeRate>> resp = exchangeRatesService.findAll();
        assertThat(resp).matches(r -> r.getBody().equals(exchangeRates)).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);
        verify(repo, times(1)).findAll();
        verifyNoMoreInteractions(repo);


    }

    @Test
    void findAll_RepoThrowsException_ReturnsResponseEntity() throws SQLException {
        doThrow(SQLException.class).when(repo).findAll();
        ResponseDTO<List<ExchangeRate>> resp = exchangeRatesService.findAll();
        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).findAll();
        verifyNoMoreInteractions(repo);
    }


    @Test
    void findByCodes_RepoReturnsValidElement_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        String code1 = "RUB";
        String code2 = "USD";
        doReturn(exchangeRate).when(repo).findByCodes(code1, code2);
        ResponseDTO<ExchangeRate> resp = exchangeRatesService.findByCodes(code1, code2);
        assertThat(resp).matches(r -> r.getBody().equals(exchangeRate)).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).findByCodes(code1, code2);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void findByCodes_RepoReturnsNull_ReturnsResponseEntity() throws SQLException {
        String code1 = "RUB";
        String code2 = "USD";
        doReturn(null).when(repo).findByCodes(code1, code2);
        ResponseDTO<ExchangeRate> resp = exchangeRatesService.findByCodes(code1, code2);
        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.NOT_FOUND)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).findByCodes(code1, code2);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void findByCodes_RepoThrowsException_ReturnsResponseEntity() throws SQLException {
        String code1 = "RUB";
        String code2 = "USD";
        doThrow(SQLException.class).when(repo).findByCodes(code1, code2);
        ResponseDTO resp = exchangeRatesService.findByCodes(code1, code2);
        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).findByCodes(code1, code2);
        verifyNoMoreInteractions(repo);

    }

    @Test
    void save_RepoSaves_ReturnsResponseEntity() throws SQLException {
        int rowsUpdated = 1;
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doReturn(rowsUpdated).when(repo).save(exchangeRate);
        ResponseDTO resp = exchangeRatesService.save(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.CREATED)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).save(exchangeRate);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void save_RepoThrowsDataIntegrityException_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doThrow(DataIntegrityException.class).when(repo).save(exchangeRate);
        ResponseDTO resp = exchangeRatesService.save(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.CONFLICT)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).save(exchangeRate);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void save_RepoThrowsSQLException_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doThrow(SQLException.class).when(repo).save(exchangeRate);
        ResponseDTO resp = exchangeRatesService.save(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).save(exchangeRate);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_RepoReturnValueMoreThanZero_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        int rowsUpdated = 1;
        doReturn(rowsUpdated).when(repo).update(exchangeRate);

        ResponseDTO resp = exchangeRatesService.update(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).update(exchangeRate);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_RepoReturnZero_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        int rowsUpdated = 0;
        doReturn(rowsUpdated).when(repo).update(exchangeRate);

        ResponseDTO resp = exchangeRatesService.update(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.NOT_FOUND)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).update(exchangeRate);
        verifyNoMoreInteractions(repo);
    }





    @Test
    void exchange_ValidRateWithOKStatus_ReturnsExchangeDTO() {
        String baseCode = "RUB";
        String targetCode = "EUR";
        double amount = 10;
        ResponseDTO resFindByCodes = mock(ResponseDTO.class);
        doReturn(resFindByCodes).when(exchangeRatesService).findByCodes(baseCode, targetCode);
        String message = "message";
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doReturn(HttpStatus.OK).when(resFindByCodes).getCode();
        doReturn(message).when(resFindByCodes).getMessage();
        doReturn(exchangeRate).when(resFindByCodes).getBody();

        double rate = 1.5;
        Currency currency1 = mock(Currency.class);
        Currency currency2 = mock(Currency.class);
        doReturn(rate).when(exchangeRate).getRate();
        doReturn(currency1).when(exchangeRate).getBaseCurrency();
        doReturn(currency2).when(exchangeRate).getTargetCurrency();
        double expectedConvertedCurrency = rate * amount;


        ResponseDTO<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);

        assertThat(resp.getBody()).matches(b -> b.getAmount() == amount).matches(b -> b.getRate() == rate).matches(b -> b.getBaseCurrency().equals(currency1)).matches(b -> b.getTargetCurrency().equals(currency2)).matches(b -> b.getConvertedAmount() == expectedConvertedCurrency);


        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(resFindByCodes, times(1)).getCode();
        verify(resFindByCodes, times(1)).getMessage();
        verify(resFindByCodes, times(1)).getBody();

        verify(exchangeRate, times(1)).getBaseCurrency();
        verify(exchangeRate, times(1)).getTargetCurrency();
        verify(exchangeRate, times(1)).getRate();
        verifyNoMoreInteractions(exchangeRatesService, resFindByCodes, exchangeRate);

    }





    @Test
    void exchange_ValidRateWithServerErrorStatus_ReturnsResponseEntity() {
        String baseCode = "USD";
        String targetCode = "USD";
        double amount = 10;
        ResponseDTO resFindByCodes = mock(ResponseDTO.class);
        doReturn(HttpStatus.SERVER_ERROR).when(resFindByCodes).getCode();
        String message = "message";
        doReturn(message).when(resFindByCodes).getMessage();
        doReturn(resFindByCodes).when(exchangeRatesService).findByCodes(baseCode, targetCode);


        ResponseDTO<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(resFindByCodes, times(1)).getCode();
        verify(resFindByCodes, times(1)).getMessage();
        verify(resFindByCodes, times(1)).getBody();
        verifyNoMoreInteractions(exchangeRatesService, resFindByCodes);


    }



    @Test
    void exchange_ValidReversedRateWithOKStatus_ReturnsResponseEntity() {
        String baseCode = "RUB";
        String targetCode = "EUR";
        double amount = 10;

        ResponseDTO rate = mock(ResponseDTO.class);
        doReturn(HttpStatus.NOT_FOUND).when(rate).getCode();
        doReturn(rate).when(exchangeRatesService).findByCodes(baseCode, targetCode);

        ResponseDTO reversedRate = mock(ResponseDTO.class);
        doReturn(reversedRate).when(exchangeRatesService).findByCodes(targetCode, baseCode);
        String message = "message";
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        double rateV = 1.5;
        Currency currency1 = mock(Currency.class);
        Currency currency2 = mock(Currency.class);
        doReturn(rateV).when(exchangeRate).getRate();
        doReturn(currency1).when(exchangeRate).getBaseCurrency();
        doReturn(currency2).when(exchangeRate).getTargetCurrency();

        doReturn(HttpStatus.OK).when(reversedRate).getCode();
        doReturn(message).when(reversedRate).getMessage();
        doReturn(exchangeRate).when(reversedRate).getBody();
        doReturn(message).when(reversedRate).getMessage();
        doReturn(exchangeRate).when(reversedRate).getBody();


        double expectedConvertedCurrency = 1/rateV * amount;
        ResponseDTO<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);

        assertThat(resp.getBody())
                .matches(b -> b.getAmount() == amount)
                .matches(b -> b.getRate() == 1/rateV)
                .matches(b -> b.getBaseCurrency().equals(currency2))
                .matches(b -> b.getTargetCurrency().equals(currency1))
                .matches(b -> b.getConvertedAmount() == expectedConvertedCurrency);


        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(exchangeRatesService, times(1)).findByCodes(targetCode, baseCode);

        verify(rate, times(1)).getCode();

        verify(reversedRate, times(1)).getCode();
        verify(reversedRate, times(1)).getMessage();
        verify(reversedRate, times(1)).getBody();

        verify(exchangeRate, times(1)).getBaseCurrency();
        verify(exchangeRate, times(1)).getTargetCurrency();
        verify(exchangeRate, times(1)).getRate();

        verifyNoMoreInteractions(exchangeRatesService,rate, reversedRate, exchangeRate);

    }








    @Test
    void exchange_ValidReversedRateWithServerErrorStatus_ReturnsResponseEntity() {
        String baseCode = "RUB";
        String targetCode = "EUR";
        double amount = 10;
        ResponseDTO rate = mock(ResponseDTO.class);
        doReturn(rate).when(exchangeRatesService).findByCodes(baseCode, targetCode);
        doReturn(HttpStatus.NOT_FOUND).when(rate).getCode();


        ResponseDTO reversedRate = mock(ResponseDTO.class);
        String message = "message";
        doReturn(message).when(reversedRate).getMessage();
        doReturn(HttpStatus.SERVER_ERROR).when(reversedRate).getCode();
        doReturn(reversedRate).when(exchangeRatesService).findByCodes(targetCode, baseCode);


        ResponseDTO<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(exchangeRatesService, times(1)).findByCodes(targetCode, baseCode);

        verify(rate, times(1)).getCode();

        verify(reversedRate, times(1)).getCode();
        verify(reversedRate, times(1)).getMessage();
        verify(reversedRate, times(1)).getBody();

        verifyNoMoreInteractions(exchangeRatesService, rate,reversedRate);

    }








    @Test
    void exchange_ValidRateBasedUSDWithOKStatus_ReturnsResponseEntity() throws SQLException {
        String baseCode = "RUB";
        String targetCode = "EUR";
        String usd = "USD";
        double amount = 10;
        ResponseDTO rate = mock(ResponseDTO.class);
        doReturn(HttpStatus.NOT_FOUND).when(rate).getCode();
        doReturn(rate).when(exchangeRatesService).findByCodes(baseCode, targetCode);

        ResponseDTO reversedRate = mock(ResponseDTO.class);
        doReturn(HttpStatus.NOT_FOUND).when(reversedRate).getCode();
        doReturn(reversedRate).when(exchangeRatesService).findByCodes(targetCode, baseCode);


        Currency usdCurrency = mock(Currency.class);
        Currency currency1 = mock(Currency.class);
        Currency currency2 = mock(Currency.class);
        doReturn(targetCode).when(currency1).getCode();
        doReturn(baseCode).when(currency2).getCode();
        double rate1 = 1.5;
        double rate2 = 2.5;
        ExchangeRate exchangeRate1 = mock(ExchangeRate.class);
        ExchangeRate exchangeRate2 = mock(ExchangeRate.class);
        doReturn(usdCurrency).when(exchangeRate1).getBaseCurrency();
        doReturn(currency1).when(exchangeRate1).getTargetCurrency();
        doReturn(usdCurrency).when(exchangeRate2).getBaseCurrency();
        doReturn(currency2).when(exchangeRate2).getTargetCurrency();
        doReturn(rate1).when(exchangeRate1).getRate();
        doReturn(rate2).when(exchangeRate2).getRate();

        double resRate = rate2/rate1;
        ExchangeRate resultingExchangeRate = mock(ExchangeRate.class);
        doReturn(currency1).when(resultingExchangeRate).getBaseCurrency();
        doReturn(currency2).when(resultingExchangeRate).getTargetCurrency();
        doReturn(resRate).when(resultingExchangeRate).getRate();

        int listSize = 2;
        List exchangeRates = mock(List.class);
        doReturn(listSize).when(exchangeRates).size();
        doReturn(exchangeRate1).when(exchangeRates).get(0);
        doReturn(exchangeRate2).when(exchangeRates).get(1);
        doReturn(exchangeRates).when(repo).findByTargetCodesBasedUSD(baseCode,targetCode);

        ResponseDTO<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp)
                .matches(r -> r.getBody() != null)
                .matches(r -> r.getCode().equals(HttpStatus.OK))
                .matches(r -> r.getMessage() != null);

        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(exchangeRatesService, times(1)).findByCodes(targetCode, baseCode);
        verify(repo, times(1)).findByTargetCodesBasedUSD(baseCode,targetCode);

        verifyNoMoreInteractions(exchangeRatesService,repo);

    }


    @Test
    void exchange_ValidRateBasedUSDWithNotFoundStatus_ReturnsResponseEntity() throws SQLException {
        String baseCode = "RUB";
        String targetCode = "EUR";
        String usd = "USD";
        double amount = 10;
        ResponseDTO rate = mock(ResponseDTO.class);
        doReturn(HttpStatus.NOT_FOUND).when(rate).getCode();
        doReturn(rate).when(exchangeRatesService).findByCodes(baseCode, targetCode);

        ResponseDTO reversedRate = mock(ResponseDTO.class);
        doReturn(HttpStatus.NOT_FOUND).when(reversedRate).getCode();
        doReturn(reversedRate).when(exchangeRatesService).findByCodes(targetCode, baseCode);

        int listSize = 1;
        List exchangeRates = mock(List.class);
        doReturn(listSize).when(exchangeRates).size();
        doReturn(exchangeRates).when(repo).findByTargetCodesBasedUSD(baseCode,targetCode);


        ResponseDTO<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp)
                .matches(r -> r.getBody() == null)
                .matches(r -> r.getCode().equals(HttpStatus.NOT_FOUND))
                .matches(r -> r.getMessage() != null);

        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(exchangeRatesService, times(1)).findByCodes(targetCode, baseCode);
        verify(repo, times(1)).findByTargetCodesBasedUSD(baseCode,targetCode);

        verifyNoMoreInteractions(exchangeRatesService,repo);

    }


    @Test
    void exchange_SQLExceptionThrownDuringRateBasedUSDQuery_ReturnsResponseEntity() throws SQLException {
        String baseCode = "RUB";
        String targetCode = "EUR";
        String usd = "USD";
        double amount = 10;
        ResponseDTO rate = mock(ResponseDTO.class);
        doReturn(HttpStatus.NOT_FOUND).when(rate).getCode();
        doReturn(rate).when(exchangeRatesService).findByCodes(baseCode, targetCode);

        ResponseDTO reversedRate = mock(ResponseDTO.class);
        doReturn(HttpStatus.NOT_FOUND).when(reversedRate).getCode();
        doReturn(reversedRate).when(exchangeRatesService).findByCodes(targetCode, baseCode);

        doThrow(SQLException.class).when(repo).findByTargetCodesBasedUSD(baseCode,targetCode);

        ResponseDTO<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp)
                .matches(r -> r.getBody() == null)
                .matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR))
                .matches(r -> r.getMessage() != null);

    }


}