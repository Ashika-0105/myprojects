package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mysqldb {
    private static final String URL = "jdbc:mysql://localhost:3306/chatApp";
    private static final String USER = "root";
    private static final String PASSWORD = "Ashika0501";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
