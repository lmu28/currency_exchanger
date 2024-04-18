package org.example.dao;

import org.example.dao.exception.DataIntegrityException;
import org.example.dao.executor.ExecutorService;
import org.example.model.Currency;

import java.sql.Connection;
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
        return es.executeQuery(connection, sql, res -> {
            List<Currency> resultList = new ArrayList<>();
            while (res.next()) {
                int id = res.getInt("id");
                String code = res.getString("code");
                String fullName = res.getString("full_name");
                String sign = res.getString("sign");
                resultList.add(new Currency(id, code, fullName, sign));
            }
            return resultList;
        });

    }

//    @Override
//    public Currency findById(int id) {
//        String sql = String.format("SELECT * FROM currencies where id = %s;", id);
//        return es.executeQuery(connection, sql, res -> {
//            if (res.next()){
//                String code = res.getString("code");
//                String fullName = res.getString("full_name");
//                String sign = res.getString("sign");
//                return new Currency(id, code, fullName, sign);
//            }
//            return null;
//
//        });
//    }

    @Override
    public Currency findByCode(String code) throws SQLException {
        String sql = String.format("SELECT * FROM currencies where code = '%s';", code);
        return es.executeQuery(connection, sql, res -> {
            if(res.next()){
                int id = res.getInt("id");
                String fullName = res.getString("full_name");
                String sign = res.getString("sign");
                return new Currency(id, code, fullName, sign);
            }
            return null;

        });
    }

    @Override
    public int save(Currency currency) throws SQLException {
        String sql = String.format("INSERT INTO currencies(code, full_name, sign)" +
                "VALUES ('%s','%s','%s')"
                ,currency.getCode(),currency.getFullName(),currency.getSign());


        try {
            return es.executeUpdate(connection,sql);
        } catch (SQLException e) {
            String notUniqueMessage = "not unique";
            if (e.getMessage().contains(notUniqueMessage)) throw new DataIntegrityException(e.getMessage(), e.getSQLState(), e.getErrorCode());
            throw e;
        }
    }


    @Override
    public int deleteById(int id)throws SQLException {
        String sql = String.format("delete from currencies where id = '%s'", id);
        return es.executeUpdate(connection, sql);
    }

    @Override
    public int update(Currency el) throws SQLException {
        String sql = String.format("update currencies " +
                        "set code = '%s', " +
                        "full_name = '%s', " +
                        "sign = '%s' " +
                        "where id = '%s'"
                ,el.getCode(),el.getFullName(),el.getSign(),el.getId());
        return es.executeUpdate(connection, sql);
    }
}
