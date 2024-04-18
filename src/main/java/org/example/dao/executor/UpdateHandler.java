package org.example.dao.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UpdateHandler<T> {

    T handle(ResultSet resultSet) throws SQLException;

}
