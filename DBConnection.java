package com.college.cms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection – Singleton that holds the single MySQL JDBC connection.
 *
 * HOW TO CONFIGURE:
 *   Change DB_PASSWORD to your local MySQL root password.
 *   If your MySQL runs on a different port/host, update DB_URL.
 *
 * Usage anywhere in the project:
 *   Connection conn = DBConnection.getInstance().getConnection();
 */
public class DBConnection {

    // ── JDBC settings ─────────────────────────────────────────
    private static final String DB_URL      =
        "jdbc:mysql://localhost:3306/cms_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "root"; // ← change to your MySQL password

    // ── Singleton ─────────────────────────────────────────────
    private static DBConnection instance;
    private Connection connection;

    // Private constructor: load driver and open connection
    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "MySQL JDBC Driver not found.\n" +
                "Add mysql-connector-java-x.x.x.jar to your project build path.\n" +
                e.getMessage());
        }
    }

    /** Returns the singleton instance, creating it on first call. */
    public static DBConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    /** Returns the open JDBC Connection. */
    public Connection getConnection() {
        return connection;
    }

    /** Closes the connection cleanly (call on app exit). */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("[DBConnection] Error closing: " + e.getMessage());
        }
    }
}
