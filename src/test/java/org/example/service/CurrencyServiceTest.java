package org.example.service;

import org.example.controller.HttpStatus;
import org.example.controller.dto.ResponseEntity;
import org.example.dao.CurrencyRepository;
import org.example.dao.exception.DataIntegrityException;
import org.example.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {
    private CurrencyService currencyService;

    @Mock
    private CurrencyRepository currencyRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        currencyService = new CurrencyService(currencyRepo);
    }

    @Test
    void findAll_RepoReturnsList_ReturnsResponseEntity() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        doReturn(currencies).when(currencyRepo).findAll();
        ResponseEntity<List<Currency>> resp = currencyService.findAll();
        assertThat(resp)
                .matches(r -> r.getBody().equals(currencies))
                .matches(r -> r.getCode().equals(HttpStatus.OK))
                .matches(r -> r.getMessage() != null);
        verify(currencyRepo, times(1)).findAll();
        verifyNoMoreInteractions(currencyRepo);


    }
    @Test
    void findAll_RepoThrowsException_ReturnsResponseEntity() throws SQLException {
        doThrow(SQLException.class).when(currencyRepo).findAll();
        ResponseEntity<List<Currency>> resp = currencyService.findAll();
        assertThat(resp)
                .matches(r -> r.getBody() == null )
                .matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR))
                .matches(r -> r.getMessage() != null);

        verify(currencyRepo, times(1)).findAll();
        verifyNoMoreInteractions(currencyRepo);
    }

    @Test
    void save_RepoSaves_ReturnsResponseEntity() throws SQLException {
        int rowsUpdated = 1;
        Currency currency = mock(Currency.class);
        doReturn(rowsUpdated).when(currencyRepo).save(currency);
        ResponseEntity resp = currencyService.save(currency);

        assertThat(resp)
                .matches(r -> r.getBody() == null )
                .matches(r -> r.getCode().equals(HttpStatus.CREATED))
                .matches(r -> r.getMessage() != null);

        verify(currencyRepo, times(1)).save(currency);
        verifyNoMoreInteractions(currencyRepo);


    }

    @Test
    void save_RepoThrowsDataIntegrityException_ReturnsResponseEntity() throws SQLException {
        Currency currency = mock(Currency.class);
        doThrow(DataIntegrityException.class).when(currencyRepo).save(currency);
        ResponseEntity resp = currencyService.save(currency);

        assertThat(resp)
                .matches(r -> r.getBody() == null )
                .matches(r -> r.getCode().equals(HttpStatus.CONFLICT))
                .matches(r -> r.getMessage() != null);

        verify(currencyRepo, times(1)).save(currency);
        verifyNoMoreInteractions(currencyRepo);


    }

    @Test
    void save_RepoThrowsSQLException_ReturnsResponseEntity() throws SQLException {
        Currency currency = mock(Currency.class);
        doThrow(SQLException.class).when(currencyRepo).save(currency);
        ResponseEntity resp = currencyService.save(currency);

        assertThat(resp)
                .matches(r -> r.getBody() == null )
                .matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR))
                .matches(r -> r.getMessage() != null);

        verify(currencyRepo, times(1)).save(currency);
        verifyNoMoreInteractions(currencyRepo);
    }

    @Test
    void findByCode_RepoReturnsValidElement_ReturnsResponseEntity() throws SQLException {
        Currency currency = mock(Currency.class);
        String code = "USD";
        doReturn(currency).when(currencyRepo).findByCode(code);
        ResponseEntity<Currency> resp  = currencyService.findByCode(code);
        assertThat(resp)
                .matches(r -> r.getBody().equals(currency))
                .matches(r -> r.getCode().equals(HttpStatus.OK))
                .matches(r -> r.getMessage() != null);

        verify(currencyRepo, times(1)).findByCode(code);
        verifyNoMoreInteractions(currencyRepo);


    }

    @Test
    void findByCode_RepoReturnsNull_ReturnsResponseEntity() throws SQLException {
        String code = "USD";
        doReturn(null).when(currencyRepo).findByCode(code);
        ResponseEntity<Currency> resp  = currencyService.findByCode(code);
        assertThat(resp)
                .matches(r -> r.getBody() == null )
                .matches(r -> r.getCode().equals(HttpStatus.NOT_FOUND))
                .matches(r -> r.getMessage() != null);

        verify(currencyRepo, times(1)).findByCode(code);
        verifyNoMoreInteractions(currencyRepo);


    }

    @Test
    void findByCode_RepoThrowsException_ReturnsResponseEntity() throws SQLException {
        String code = "USD";
        doThrow(SQLException.class).when(currencyRepo).findByCode(code);
        ResponseEntity resp = currencyService.findByCode(code);
        assertThat(resp)
                .matches(r -> r.getBody() == null )
                .matches(r -> r.getCode().equals(HttpStatus.SERVER_ERROR))
                .matches(r -> r.getMessage() != null);

        verify(currencyRepo, times(1)).findByCode(code);
        verifyNoMoreInteractions(currencyRepo);

    }

}