import java.sql.*;

public class DBConnection {
    static final String URL = "jdbc:mysql://localhost:3306/registration_db";
    static final String USER = "root"; // replace with your DB user
    static final String PASS = "Brian2005@";     // replace with your DB password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
