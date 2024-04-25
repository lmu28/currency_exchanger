package org.example.dao;

import org.example.dao.exception.DataIntegrityException;
import org.example.dao.executor.ExecutorService;
import org.example.model.Currency;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyRepoJDBC implements CurrencyRepository {
    private final ExecutorService es;
    private final Connection connection;


    public CurrencyRepoJDBC(Connection connection) {
        this.connection = connection;
        es = new ExecutorService();
    }
    @Override
    public List<Currency> findAll() throws SQLException {
        String sql = "SELECT * FROM currencies;";
        List<Object> params = null;
        return es.executeQuery(connection, sql,params, res -> {
            List<Currency> resultList = new ArrayList<>();
            while (res.next()) resultList.add(buildCurrency(res));
            return resultList;
        });

    }


    @Override
    public Currency findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM currencies where code = ?;";
        List<Object> params = List.of(code);
        return es.executeQuery(connection, sql,params, res -> {
            if(res.next()) return buildCurrency(res);
            return null;

        });
    }

    @Override
    public int save(Currency currency) throws SQLException {
        String sql = "INSERT INTO currencies(code, full_name, sign)" +
                "VALUES (?,?,?)";

        List<Object> params = List.of(currency.getCode(),currency.getFullName(),currency.getSign());
        try {
            return es.executeUpdate(connection,sql,params);
        } catch (SQLException e) {
            String notUniqueMessage = "not unique";
            if (e.getMessage().contains(notUniqueMessage)) throw new DataIntegrityException(e.getMessage(), e.getSQLState(), e.getErrorCode());
            throw e;
        }
    }


    @Override
    public int deleteById(int id)throws SQLException {
        String sql = "delete from currencies where id = ?";
        List<Object> params = List.of(id);
        return es.executeUpdate(connection, sql,params);
    }

    @Override
    public int update(Currency el) throws SQLException {
        String sql = "update currencies " +
                        "set code = ?, " +
                        "full_name = ?, " +
                        "sign = ? " +
                        "where id = ?";
        List<Object> params = List.of(el.getCode(),el.getFullName(),el.getSign(),el.getId());
        return es.executeUpdate(connection, sql,params);
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String fullName = resultSet.getString("full_name");
        String sign = resultSet.getString("sign");
        String code = resultSet.getString("code");
        return new Currency(id, code, fullName, sign);


    }
}
