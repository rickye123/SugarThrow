package com.example.richa.sugarthrow;

import java.sql.*;
import java.util.Map;

public class ServerConnector {

    private Connection connection;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String CONNECTION_STRING = "jdbc:mysql://";
    static final String NOSSL = "?useSSL=false";

    /**
     * getter method to return the connection object
     * @return - Connection object c
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Open the database using the connection string,
     * the user and their password. Throw error if
     * connection cannot be established
     */
    public void open(Map<String, String> connectionVariables) {

        try {
            // set up the database connection
            // jdbc:mysql://<hostname>:<port>/<dbname>?useSSL=false<user><password>
            connection = DriverManager.getConnection(CONNECTION_STRING +
                            connectionVariables.get("database") + NOSSL,
                    connectionVariables.get("dbuser"), connectionVariables.get("dbpassword"));
        } catch (SQLException connectorException) {
            connection = null;
            throw new RuntimeException(connectorException);
        }
    }

    /**
     * check to see if user has the JDBC driver class
     */
    public void establishJDBCDriver() {

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException connectorException) {
            throw new Error(connectorException);
        }
    }

    /**
     * Close the database connection. Throw an error
     * if cannot close the connection
     */
    public void close() {
        try {
            if (connection == null) return;
            connection.close();
            connection = null;
        } catch (SQLException connectorException) {
            throw new RuntimeException(connectorException);
        }
    }

}
