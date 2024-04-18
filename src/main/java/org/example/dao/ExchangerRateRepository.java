package org.example.dao;

import org.example.model.ExchangeRate;

import java.sql.SQLException;

public interface ExchangerRateRepository extends CRUDRepository<ExchangeRate>{


    ExchangeRate findByCodes(String baseCode, String targetCode) throws SQLException;
}
