package org.example.dao;

import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.example.service.DBService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ITCurrencyRepoJDBCTest {

    public static final int ROWS_COUNT = 7;
    public static final int FIRST_ELEMENT = 0;
    private static Connection connection;

    private CurrencyRepository repo;
    private Currency newCurrency;

    @BeforeEach
    void setUp() {
        repo = new CurrencyRepoJDBC(connection);
        newCurrency = new Currency(0,"KRW", "South Korean Won", "₩");
    }


    @BeforeAll
    static void beforeAll() {
        connection = DBService.getConnection();

    }

    @AfterAll
    static void afterAll() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void findAll_ReturnList() throws SQLException {

        List<Currency> actual = repo.findAll();
        assertThat(actual.size()).isEqualTo(ROWS_COUNT);
        assertThat(actual)
                .allMatch(el -> el.getId() >= 0)
                .allMatch(el -> el.getFullName() != null)
                .allMatch(el -> !el.getFullName().isEmpty())
                .allMatch(el -> el.getSign() != null)
                .allMatch(el -> !el.getSign().isEmpty())
                .allMatch(el -> el.getCode() != null)
                .allMatch(el -> !el.getCode().isEmpty());
    }
    @Test
    void findByCode_CodeExists_ReturnElement() throws SQLException  {
        String code = "RUB";
        Currency currency = repo.findByCode(code);
        assertThat(currency.getCode()).isEqualTo(code);
    }

    @Test
    void findByCode_CodeNotExists_ReturnNull() throws SQLException  {
        String invalidCode = "RUB2";
        assertThat(repo.findByCode(invalidCode)).isNull();
    }

    @Test
    void save_CodeNotExists_SaveNewElement() throws SQLException {
        connection.setAutoCommit(false);
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT);
        repo.save(newCurrency);
        List<Currency> res = repo.findAll();
        assertThat(res.size()).isEqualTo(ROWS_COUNT + 1);
        assertThat(res.get(res.size() - 1))
                .matches(el -> el.getSign().equals(newCurrency.getSign()))
                .matches(el -> el.getFullName().equals(newCurrency.getFullName()))
                .matches(el -> el.getCode().equals(newCurrency.getCode()));

        connection.rollback();
    }

    @Test
    void save_CodeAlreadyExists_ThrowsSqlException() throws SQLException {
        connection.setAutoCommit(false);
        Currency existingElement = repo.findAll().get(FIRST_ELEMENT);
        newCurrency.setCode(existingElement.getCode());
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT);
        assertThatThrownBy(() -> repo.save(newCurrency)).isInstanceOf(SQLException.class);
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT);


        try {
            repo.save(newCurrency);
        }catch (SQLException e){
            System.out.println(e.getMessage());

        }

        connection.rollback();
    }





    @Test
    void deleteById_IdExists_DeleteElement() throws SQLException {
        connection.setAutoCommit(false);
        List<Currency> elements = repo.findAll();
        assertThat(elements.size()).isEqualTo(ROWS_COUNT);
        Currency deletedCr = elements.get(FIRST_ELEMENT);
        int res = repo.deleteById(deletedCr.getId());
        int expectedUpdatedRows = 1;
        assertThat(res).matches(r -> r == expectedUpdatedRows);
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT-1);
        assertThat(repo.findAll()).allMatch(el -> el.getId() != deletedCr.getId());
        connection.rollback();
    }

    @Test
    void deleteById_IdNotExists_ReturnZeroUpdatedRows() throws SQLException {
        connection.setAutoCommit(false);
        int invalidId = -1;
        int res = repo.deleteById(invalidId);
        int expectedUpdatedRows = 0;
        assertThat(res).matches(r -> r == expectedUpdatedRows);
        connection.rollback();
    }

    @Test
    void update_ElementExists_UpdateElement() throws SQLException {
        connection.setAutoCommit(false);
        Currency sourceElement = repo.findAll().get(FIRST_ELEMENT);
        String newName = sourceElement.getFullName() + "₽₽₽";
        sourceElement.setFullName(newName);
        int res = repo.update(sourceElement);

        int expectedUpdatedRows = 1;
        assertThat(res).matches(r -> r == expectedUpdatedRows);

        Currency updatedElement = repo.findAll().get(FIRST_ELEMENT);
        assertThat(updatedElement)
                //difference
                .matches(el -> el.getFullName().equals(newName))
                // no changes
                .matches(el -> el.getId() == sourceElement.getId())
                .matches(el -> el.getCode().equals(sourceElement.getCode()))
                .matches(el -> el.getSign().equals(sourceElement.getSign()));

        connection.rollback();
    }


    @Test
    void update_ElementNotExists_ReturnZeroUpdatedRows() throws SQLException {
        connection.setAutoCommit(false);
        int res = repo.update(newCurrency);
        int expectedUpdatedRows = 0;
        assertThat(res).matches(r -> r == expectedUpdatedRows);
        connection.rollback();
    }
}