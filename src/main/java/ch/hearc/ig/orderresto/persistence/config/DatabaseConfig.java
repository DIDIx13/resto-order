package ch.hearc.ig.orderresto.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:oracle:thin:@x:x:x"; //host port sid
    private static final String USER = "x"; // username
    private static final String PASS = "x"; // password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
