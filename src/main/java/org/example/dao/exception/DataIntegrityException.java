package org.example.dao.exception;

import java.sql.SQLException;

public class DataIntegrityException extends SQLException {

    public DataIntegrityException(String reason, String SQLState, int vendorCode) {

        super(reason, SQLState, vendorCode);

    }
}
