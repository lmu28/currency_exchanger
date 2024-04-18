package org.example.dao.executor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExecutorService {


    public int executeUpdate(Connection connection, String query, List<Object> params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        setParams(preparedStatement,params);
        int res = preparedStatement.executeUpdate();
        preparedStatement.close();
        return res;


}

    public <T> T executeQuery(Connection connection, String query,List<Object> params, UpdateHandler<T> updateHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        setParams(preparedStatement,params);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        T handled = updateHandler.handle(resultSet);
        resultSet.close();
        preparedStatement.close();
        return handled;

    }

    private void setParams(PreparedStatement preparedStatement,List<Object> params ) throws SQLException {
        if (params == null) params = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            preparedStatement.setObject(i+1,params.get(i));
        }

    }
}
