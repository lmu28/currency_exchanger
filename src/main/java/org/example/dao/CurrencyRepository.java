package org.example.dao;

import org.example.dao.CRUDRepository;
import org.example.model.Currency;

import java.sql.SQLException;

public interface CurrencyRepository extends CRUDRepository<Currency> {


    Currency findByCode(String code) throws SQLException;
//    Currency findById(int id);
}
