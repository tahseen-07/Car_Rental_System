// Path: src/com/carrental/util/DbConnection.java
package com.carrental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnection {
    private static final Logger LOGGER = Logger.getLogger(DbConnection.class.getName());

    // Read connection values from environment variables with sensible defaults
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/car_rental_db");
    private static final String USERNAME = System.getenv().getOrDefault("DB_USER", "root");
    // Do not hardcode passwords in source; read from env or use default for quick testing
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "Tahseen@123");

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining DB connection", e);
        }
        return connection;
    }
}