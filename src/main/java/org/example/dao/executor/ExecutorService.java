package org.example.dao.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExecutorService {


    public int executeUpdate(Connection connection, String query) throws SQLException {

        Statement statement = connection.createStatement();
        int res = statement.executeUpdate(query);
        statement.close();
        return res;


}

    public <T> T executeQuery(Connection connection, String query, UpdateHandler<T> updateHandler) throws SQLException {
        // TODO испоьзовать PreparedStatement
        Statement statement = connection.createStatement();
        statement.execute(query);
        ResultSet resultSet = statement.getResultSet();
        T handled = updateHandler.handle(resultSet);
        resultSet.close();
        statement.close();
        return handled;

    }
}
