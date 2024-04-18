package org.example.dao;

import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.junit.jupiter.api.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


class ITExchangeRateRepoJDBCTest {

    public static final int ROWS_COUNT = 4;
    public static final int FIRST_ELEMENT_INDEX = 0;
    private static Connection connection;

    private ExchangerRateRepository repo;

    private ExchangeRate exchangeRate;
    private Currency base;
    private Currency target;


    @BeforeAll
    static void beforeAll() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + "C:\\Java\\Workspace\\java_backend_learning_course\\currency_exchanger\\src\\test\\resources\\db\\CurrencyExchange.db";
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @BeforeEach
    void setUp() throws SQLException {
        connection.setAutoCommit(true);
        repo = new ExchangeRateRepoJDBC(connection);
        base = new Currency(4, "RUB", "Russian Ruble", "₽");
        target = new Currency(10, "INR", "Indian Rupee", "₹");
        exchangeRate = new ExchangeRate(0, base, target, 1.12);
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
    void findAll_ReturnList() throws SQLException  {

        List<ExchangeRate> actual = repo.findAll();

        assertThat(actual.size()).isEqualTo(ROWS_COUNT);
        assertThat(actual)
                .allMatch(el -> el.getId() >= 0)
                .allMatch(el -> el.getRate() >= 0)
                .allMatch(el -> el.getBaseCurrency() instanceof Currency)
                .allMatch(el -> el.getTargetCurrency() instanceof Currency);
    }

    @Test
    void save_CodePairNotExists_SaveNewElement() throws SQLException {
        connection.setAutoCommit(false);
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT);
        repo.save(exchangeRate);
        List<ExchangeRate> res = repo.findAll();
        assertThat(res.size()).isEqualTo(ROWS_COUNT + 1);
        assertThat(res.get(res.size() - 1))
                .matches(el -> el.getRate() == exchangeRate.getRate())
                .matches(el -> el.getBaseCurrency().getId() == exchangeRate.getBaseCurrency().getId())
                .matches(el -> el.getBaseCurrency().getCode().equals(exchangeRate.getBaseCurrency().getCode()))
                .matches(el -> el.getBaseCurrency().getFullName().equals(exchangeRate.getBaseCurrency().getFullName()))
                .matches(el -> el.getBaseCurrency().getSign().equals(exchangeRate.getBaseCurrency().getSign()))
                .matches(el -> el.getTargetCurrency().getId() == exchangeRate.getTargetCurrency().getId())
                .matches(el -> el.getTargetCurrency().getCode().equals(exchangeRate.getTargetCurrency().getCode()))
                .matches(el -> el.getTargetCurrency().getSign().equals(exchangeRate.getTargetCurrency().getSign()))
                .matches(el -> el.getTargetCurrency().getFullName().equals(exchangeRate.getTargetCurrency().getFullName()));
        connection.rollback();
    }


    @Test
    void save_CodePairAlreadyExists_ThrowsSqlException() throws SQLException {
        connection.setAutoCommit(false);
        ExchangeRate existingElement = repo.findAll().get(FIRST_ELEMENT_INDEX);
        exchangeRate.setBaseCurrency(existingElement.getBaseCurrency());
        exchangeRate.setTargetCurrency(existingElement.getTargetCurrency());
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT);
        assertThatThrownBy(() -> repo.save(exchangeRate)).isInstanceOf(SQLException.class);
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT);

        connection.rollback();
    }









    @Test
    void update_ElementExists_UpdateElement() throws SQLException {
        connection.setAutoCommit(false);
        ExchangeRate sourceElement = repo.findAll().get(FIRST_ELEMENT_INDEX);
        double rateBefore = sourceElement.getRate();
        int rateDif = 20;
        sourceElement.setRate(rateBefore- rateDif);
        int res = repo.update(sourceElement);

        int expectedUpdatedRows = 1;
        assertThat(res).matches(r -> r == expectedUpdatedRows);

        ExchangeRate updatedElement = repo.findAll().get(FIRST_ELEMENT_INDEX);
        assertThat(updatedElement)
                //difference
                .matches(el -> el.getRate() == rateBefore-rateDif)
                // no changes
                .matches(el -> el.getId() == sourceElement.getId())
                .matches(el -> el.getBaseCurrency().getId() == sourceElement.getBaseCurrency().getId())
                .matches(el -> el.getBaseCurrency().getCode().equals(sourceElement.getBaseCurrency().getCode()))
                .matches(el -> el.getBaseCurrency().getFullName().equals(sourceElement.getBaseCurrency().getFullName()))
                .matches(el -> el.getBaseCurrency().getSign().equals(sourceElement.getBaseCurrency().getSign()))
                .matches(el -> el.getTargetCurrency().getId() == sourceElement.getTargetCurrency().getId())
                .matches(el -> el.getTargetCurrency().getCode().equals(sourceElement.getTargetCurrency().getCode()))
                .matches(el -> el.getTargetCurrency().getSign().equals(sourceElement.getTargetCurrency().getSign()))
                .matches(el -> el.getTargetCurrency().getFullName().equals(sourceElement.getTargetCurrency().getFullName()));

        connection.rollback();
    }


    @Test
    void update_ElementNotExists_ReturnZeroUpdatedRows() throws SQLException {
        connection.setAutoCommit(false);
        int res = repo.update(exchangeRate);
        int expectedUpdatedRows = 0;
        assertThat(res).matches(r -> r == expectedUpdatedRows);
        connection.rollback();
    }

    @Test
    void deleteById_IdExists_DeleteElement() throws SQLException {
        connection.setAutoCommit(false);
        List<ExchangeRate> elements = repo.findAll();
        assertThat(elements.size()).isEqualTo(ROWS_COUNT);
        ExchangeRate deletedEl = elements.get(FIRST_ELEMENT_INDEX);
        int res = repo.deleteById(deletedEl.getId());
        int expectedUpdatedRows = 1;
        assertThat(res).matches(r -> r == expectedUpdatedRows);
        assertThat(repo.findAll().size()).isEqualTo(ROWS_COUNT-1);
        assertThat(repo.findAll()).allMatch(el -> el.getId() != deletedEl.getId());
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
     void findByCodes_CodesExists_ReturnsElement() throws SQLException {
        connection.setAutoCommit(false);
        List<ExchangeRate> elements = repo.findAll();
        ExchangeRate exchangeRate = elements.get(FIRST_ELEMENT_INDEX);
        String code1 = exchangeRate.getBaseCurrency().getCode();
        String code2 = exchangeRate.getTargetCurrency().getCode();

        assertThat(repo.findByCodes(code1,code2))
                .matches(el -> el.getRate() == exchangeRate.getRate())
                .matches(el -> el.getBaseCurrency().getId() == exchangeRate.getBaseCurrency().getId())
                .matches(el -> el.getBaseCurrency().getCode().equals(exchangeRate.getBaseCurrency().getCode()))
                .matches(el -> el.getBaseCurrency().getFullName().equals(exchangeRate.getBaseCurrency().getFullName()))
                .matches(el -> el.getBaseCurrency().getSign().equals(exchangeRate.getBaseCurrency().getSign()))
                .matches(el -> el.getTargetCurrency().getId() == exchangeRate.getTargetCurrency().getId())
                .matches(el -> el.getTargetCurrency().getCode().equals(exchangeRate.getTargetCurrency().getCode()))
                .matches(el -> el.getTargetCurrency().getSign().equals(exchangeRate.getTargetCurrency().getSign()))
                .matches(el -> el.getTargetCurrency().getFullName().equals(exchangeRate.getTargetCurrency().getFullName()));

        connection.rollback();
    }

    @Test
    void findByCodes_PairCodesNotExists_ReturnsNull() throws SQLException {
        connection.setAutoCommit(false);
        String invalidCode = "1USD";

        assertThat(repo.findByCodes(invalidCode,invalidCode)).isNull();


        connection.rollback();
    }

}