package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHandler {

    private static final String url ="jdbc:mysql://localhost:3306/PhoneBook";
    private static final String user ="root";
    private static final String password ="Root123.";

    Connection conn;

    public DbHandler(){

        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public java.sql.Connection getConnection(){
        return this.conn;
    }
}
