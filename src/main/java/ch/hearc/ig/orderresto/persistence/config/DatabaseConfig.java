package ch.hearc.ig.orderresto.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:oracle:thin:@db.ig.he-arc.ch:1521:ens"; //host port sid
    private static final String USER = "DARWIN_AMELI"; // username
    private static final String PASS = "DARWIN_AMELI"; // password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
