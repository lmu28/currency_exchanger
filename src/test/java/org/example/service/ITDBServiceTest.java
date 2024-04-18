package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

class ITDBServiceTest {


    @Test
    void getConnection_ConnectionIsNull_OpenNewConnection() {
        assertThat(DBService.getConnection()).isInstanceOf(Connection.class);
    }

    @Test
    void getConnection_ConnectionIsNotNull_ReturnExistingConnection() {
        Connection initConnection = DBService.getConnection();

        assertThat(DBService.getConnection()).isEqualTo(initConnection);
    }
}