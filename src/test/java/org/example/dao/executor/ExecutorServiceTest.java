package org.example.dao.executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExecutorServiceTest {

    private ExecutorService executorService;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        doNothing().when(statement).close();
        doNothing().when(resultSet).close();
        doReturn(true).when(statement).execute(anyString());
        doReturn(resultSet).when(statement).getResultSet();
        doReturn(statement).when(connection).createStatement();

        executorService = new ExecutorService();
    }

    @Test
    void executeUpdate_ReturnsInt() throws SQLException {
        int expectedResult = 0;
        String query = "INSERT INTO table_name VALUES (value1, value2)";
        doReturn(expectedResult).when(statement).executeUpdate(anyString());

        assertThat(executorService.executeUpdate(connection,query)).isEqualTo(expectedResult);
        verify(statement, times(1)).executeUpdate(anyString());
        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).close();
        verifyNoMoreInteractions(statement);
        verifyNoMoreInteractions(connection);

    }

    @Test
    void executeQuery_ReturnObject() throws SQLException {
        String query = "SELECT * FROM table_name";
        UpdateHandler<Object> updateHandler = Mockito.mock(UpdateHandler.class);
        Object expected = new Object();
        doReturn(expected).when(updateHandler).handle(resultSet);
        assertThat(executorService.executeQuery(connection,query,updateHandler)).isEqualTo(expected);

        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).close();
        verify(statement, times(1)).execute(anyString());
        verify(statement, times(1)).getResultSet();
        verify(resultSet, times(1)).close();
        verify(updateHandler, times(1)).handle(resultSet);

        verifyNoMoreInteractions(statement);
        verifyNoMoreInteractions(connection);
        verifyNoMoreInteractions(resultSet);
        verifyNoMoreInteractions(updateHandler);


    }
}