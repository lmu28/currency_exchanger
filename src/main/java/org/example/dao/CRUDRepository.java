package org.example.dao;

import java.sql.SQLException;
import java.util.List;

public interface CRUDRepository <T>{
    List<T> findAll() throws SQLException;
    int deleteById(int id) throws SQLException;
    int save (T el) throws SQLException;
    int update (T el) throws SQLException;

}
