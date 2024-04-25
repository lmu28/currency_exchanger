package org.example.dao;

import org.example.dao.exception.DataIntegrityException;
import org.example.dao.executor.ExecutorService;
import org.example.model.Currency;
import org.example.model.ExchangeRate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateRepoJDBC implements ExchangerRateRepository{



    private Connection connection;
    private ExecutorService es;

    public ExchangeRateRepoJDBC(Connection connection) {
        this.connection = connection;
        es = new ExecutorService();
    }

    public List<ExchangeRate> findAll() throws SQLException {
        String sql = "select er.id as er_id, c1.id as c1_id, c2.id as c2_id, er.rate as er_rate" +
                ", c1.code as c1_code, c1.full_name as c1_full_name, c1.sign as c1_sign" +
                ", c2.code as c2_code, c2.full_name as c2_full_name, c2.sign as c2_sign\n" +
                "from exchange_rates as er\n" +
                "join currencies as c1 on (er.base_currency_id = c1.id)\n" +
                "join currencies as c2 on (er.target_currency_id = c2.id);";

        List<Object> params = null;

        return es.executeQuery(connection, sql,params, resultSet -> {
            List<ExchangeRate> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(buildExchangeRate(resultSet));
            }
            return resultList;
        });
    }


    @Override
    public int save(ExchangeRate element)throws SQLException {
        String sql = "Insert into exchange_rates(base_currency_id,target_currency_id,rate)" +
                        "values (?,?,?)";
        List<Object> params = List.of(element.getBaseCurrency().getId(), element.getTargetCurrency().getId(), element.getRate());

        try {
            return es.executeUpdate(connection,sql,params);
        } catch (SQLException e) {
            String notUniqueMessage = "not unique";
            if (e.getMessage().contains(notUniqueMessage)) throw new DataIntegrityException(e.getMessage(), e.getSQLState(), e.getErrorCode());
            throw e;
        }
    }

    @Override
    public int update(ExchangeRate el) throws SQLException {
        String sql = "update exchange_rates " +
                        "set rate = ?, " +
                        "base_currency_id = ?, " +
                        "target_currency_id = ? " +
                        "where id = ?";

        List<Object> params = List.of(el.getRate(), el.getBaseCurrency().getId(), el.getTargetCurrency().getId(),el.getId());
        return es.executeUpdate(connection, sql,params);
    }

    @Override
    public int deleteById(int id) throws SQLException {
        String sql = "delete from exchange_rates where id = ?";
        List<Object> params = List.of(id);
        return es.executeUpdate(connection, sql,params);
    }

    @Override
    public ExchangeRate findByCodes(String baseCode, String targetCode) throws SQLException {
        String sql ="select er.id        as er_id\n" +
                "     , c1.id        as c1_id\n" +
                "     , c2.id        as c2_id\n" +
                "     , er.rate      as er_rate\n" +
                "     , c1.code      as c1_code\n" +
                "     , c1.full_name as c1_full_name\n" +
                "     , c1.sign      as c1_sign\n" +
                "     , c2.code      as c2_code\n" +
                "     , c2.full_name as c2_full_name\n" +
                "     , c2.sign      as c2_sign\n" +
                "from exchange_rates as er\n" +
                "         join currencies as c1 on (er.base_currency_id = c1.id)\n" +
                "         join currencies as c2 on (er.target_currency_id = c2.id)\n" +
                "where c1.code = ?\n" +
                "  and c2.code = ?;";
       List<Object> params = List.of(baseCode,targetCode);

        return es.executeQuery(connection,sql,params, resultSet -> {

            if (resultSet.next()) return buildExchangeRate(resultSet);
            return null;
        });

    }


    @Override
    public List<ExchangeRate> findByTargetCodesBasedUSD(String code1, String code2) throws SQLException {
        String query = "select er.id        as er_id\n" +
                "     , c1.id        as c1_id\n" +
                "     , c2.id        as c2_id\n" +
                "     , er.rate      as er_rate\n" +
                "     , c1.code      as c1_code\n" +
                "     , c1.full_name as c1_full_name\n" +
                "     , c1.sign      as c1_sign\n" +
                "     , c2.code      as c2_code\n" +
                "     , c2.full_name as c2_full_name\n" +
                "     , c2.sign      as c2_sign\n" +
                "from exchange_rates as er\n" +
                "         join currencies c1 on (c1.id = er.base_currency_id)\n" +
                "         join currencies c2 on (c2.id = er.target_currency_id)\n" +
                "where c1.code = 'USD'\n" +
                "  and (c2.code == ? or c2.code == ?)";


        List<Object> params = List.of(code1,code2);
        return es.executeQuery(connection, query,params, resultSet -> {
            List<ExchangeRate> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(buildExchangeRate(resultSet));
            }
            return resultList;
        });
    }


    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {

        int baseCurrencyId = resultSet.getInt("c1_id");
        String baseCurrencyCode = resultSet.getString("c1_code");
        String baseCurrencyName = resultSet.getString("c1_full_name");
        String baseCurrencySign = resultSet.getString("c1_sign");
        Currency baseCurrency = new Currency(baseCurrencyId, baseCurrencyCode, baseCurrencyName, baseCurrencySign);

        int targetCurrencyId = resultSet.getInt("c2_id");
        String targetCurrencyCode = resultSet.getString("c2_code");
        String targetCurrencyName = resultSet.getString("c2_full_name");
        String targetCurrencySign = resultSet.getString("c2_sign");
        Currency targetCurrency = new Currency(targetCurrencyId, targetCurrencyCode, targetCurrencyName, targetCurrencySign);

        int id = resultSet.getInt("er_id");
        double rate = resultSet.getDouble("er_rate");
        return new ExchangeRate(id,baseCurrency,targetCurrency,rate);


    }
}
