package com.example.productivitycoach.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/Productivity_coach";
    private static final String USER = "postgres";
    private static final String PASS = "mangolotus2005";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé !", e);
        }
    }
}
