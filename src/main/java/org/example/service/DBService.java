package org.example.service;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBService {
    private static final String DB_PROPERTIES_FILE = "db.properties";
    public static final String DB_LOCATION_FIELD = "db.location";
    private static final String DB_LOCATION;
    private static Connection connection;

    static {
        Properties prop = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("db/"+ DB_PROPERTIES_FILE)) {
            if (is == null) {
                throw new IOException("unable to find db.properties");
            }
            prop.load(is);
            DB_LOCATION = prop.getProperty(DB_LOCATION_FIELD);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DBService() {
    }

    public static Connection getConnection() {
        if (connection == null) connection = openSqliteConnection();
        return connection;
    }



    private static Connection openSqliteConnection(){
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + DB_LOCATION;
            connection = DriverManager.getConnection(url);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }


}
