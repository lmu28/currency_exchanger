package org.example.dao;

import org.example.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public interface ExchangerRateRepository extends CRUDRepository<ExchangeRate>{


    ExchangeRate findByCodes(String baseCode, String targetCode) throws SQLException;
    List<ExchangeRate> findByTargetCodesBasedUSD(String code1, String code2) throws SQLException;
}
