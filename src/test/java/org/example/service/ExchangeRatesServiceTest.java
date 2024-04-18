package org.example.service;


import org.example.controller.HttpStatus;
import org.example.dao.ExchangerRateRepository;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.example.service.dto.ExchangeDTO;
import org.example.service.dto.ResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        ResponseEntity<List<ExchangeRate>> resp = exchangeRatesService.findAll();
        assertThat(resp).matches(r -> r.getBody().equals(exchangeRates)).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);
        verify(repo, times(1)).findAll();
        verifyNoMoreInteractions(repo);


    }

    @Test
    void findAll_RepoThrowsException_ReturnsResponseEntity() throws SQLException {
        doThrow(SQLException.class).when(repo).findAll();
        ResponseEntity<List<ExchangeRate>> resp = exchangeRatesService.findAll();
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
        ResponseEntity<ExchangeRate> resp = exchangeRatesService.findByCodes(code1, code2);
        assertThat(resp).matches(r -> r.getBody().equals(exchangeRate)).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).findByCodes(code1, code2);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void findByCodes_RepoReturnsNull_ReturnsResponseEntity() throws SQLException {
        String code1 = "RUB";
        String code2 = "USD";
        doReturn(null).when(repo).findByCodes(code1, code2);
        ResponseEntity<ExchangeRate> resp = exchangeRatesService.findByCodes(code1, code2);
        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.NOT_FOUND)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).findByCodes(code1, code2);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void findByCodes_RepoThrowsException_ReturnsResponseEntity() throws SQLException {
        String code1 = "RUB";
        String code2 = "USD";
        doThrow(SQLException.class).when(repo).findByCodes(code1, code2);
        ResponseEntity resp = exchangeRatesService.findByCodes(code1, code2);
        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).findByCodes(code1, code2);
        verifyNoMoreInteractions(repo);

    }

    @Test
    void save_RepoSaves_ReturnsResponseEntity() throws SQLException {
        int rowsUpdated = 1;
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doReturn(rowsUpdated).when(repo).save(exchangeRate);
        ResponseEntity resp = exchangeRatesService.save(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.CREATED)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).save(exchangeRate);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void save_RepoThrowsDataIntegrityException_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doThrow(DataIntegrityException.class).when(repo).save(exchangeRate);
        ResponseEntity resp = exchangeRatesService.save(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.CONFLICT)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).save(exchangeRate);
        verifyNoMoreInteractions(repo);


    }

    @Test
    void save_RepoThrowsSQLException_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doThrow(SQLException.class).when(repo).save(exchangeRate);
        ResponseEntity resp = exchangeRatesService.save(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).save(exchangeRate);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_RepoReturnValueMoreThanZero_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        int rowsUpdated = 1;
        doReturn(rowsUpdated).when(repo).update(exchangeRate);

        ResponseEntity resp = exchangeRatesService.update(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.OK)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).update(exchangeRate);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_RepoReturnZero_ReturnsResponseEntity() throws SQLException {
        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        int rowsUpdated = 0;
        doReturn(rowsUpdated).when(repo).update(exchangeRate);

        ResponseEntity resp = exchangeRatesService.update(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.NOT_FOUND)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).update(exchangeRate);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_RepoThrowsSQLException_ReturnsResponseEntity() throws SQLException {

        ExchangeRate exchangeRate = mock(ExchangeRate.class);
        doThrow(SQLException.class).when(repo).update(exchangeRate);

        ResponseEntity resp = exchangeRatesService.update(exchangeRate);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(repo, times(1)).update(exchangeRate);
        verifyNoMoreInteractions(repo);

    }

    @Test
    void exchange_FindByCodesMethodReturnsEntityWithServerError_ReturnsResponseEntity() {
        String baseCode = "USD";
        String targetCode = "USD";
        double amount = 10;
        ResponseEntity resFindByCodes = mock(ResponseEntity.class);
        doReturn(HttpStatus.SERVER_ERROR).when(resFindByCodes).getCode();
        String message = "message";
        doReturn(message).when(resFindByCodes).getMessage();
        doReturn(resFindByCodes).when(exchangeRatesService).findByCodes(baseCode, targetCode);

        ResponseEntity<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR)).matches(r -> r.getMessage() != null);

        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(resFindByCodes, times(1)).getCode();
        verify(resFindByCodes, times(1)).getMessage();
        verifyNoMoreInteractions(exchangeRatesService, resFindByCodes);


    }

    @Test
    void exchange_FindByCodesMethodReturnsEntityWithNotFoundError_ReturnsResponseEntity() {
        String baseCode = "USD";
        String targetCode = "USD";
        double amount = 10;
        ResponseEntity resFindByCodes = mock(ResponseEntity.class);
        doReturn(HttpStatus.NOT_FOUND).when(resFindByCodes).getCode();
        String message = "message";
        doReturn(message).when(resFindByCodes).getMessage();
        doReturn(resFindByCodes).when(exchangeRatesService).findByCodes(baseCode, targetCode);

        ResponseEntity<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

        assertThat(resp).matches(r -> r.getBody() == null).matches(r -> r.getCode().equals(HttpStatus.NOT_FOUND)).matches(r -> r.getMessage() != null);

        verify(exchangeRatesService, times(1)).exchange(baseCode, targetCode, amount);
        verify(exchangeRatesService, times(1)).findByCodes(baseCode, targetCode);
        verify(resFindByCodes, times(1)).getCode();
        verify(resFindByCodes, times(1)).getMessage();
        verifyNoMoreInteractions(exchangeRatesService, resFindByCodes);

    }


    @Test
    void exchange_FindByCodesMethodReturnsEntityWithOkStatus_ReturnsResponseEntity() {
        String baseCode = "USD";
        String targetCode = "USD";
        double amount = 10;
        ResponseEntity resFindByCodes = mock(ResponseEntity.class);
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


        ResponseEntity<ExchangeDTO> resp = exchangeRatesService.exchange(baseCode, targetCode, amount);

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


}