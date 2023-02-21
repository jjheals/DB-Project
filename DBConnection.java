import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    DBConnection() {}

    /** Creates a new connection to a DB
     * @param username DB login username
     * @param password DB login password
     * @return a Connection object
     * @throws SQLException if there is any error in creating the connection
     */
    public static Connection openConnection(String username, String password) throws SQLException {
        Connection connection;

        // Check drivers
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle JDBC Driver Registered Successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle driver error. Check driver installation. Quitting");
            e.printStackTrace();
            throw new SQLException();
        }

        // Try to create connection
        try {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@oracle.wpi.edu:1521:orcl",
                    username,
                    password);
        } catch (SQLException e) {
            System.out.println("Connection Failed. Check output console");
            e.printStackTrace();
            throw new SQLException();
        }
        return connection;
    }

    /** Closes the connection to DB
     * @param connection Connection object to close
     */
    public static void closeConnection(Connection connection) {
        try {
            connection.close();
            System.out.println("Connection closed successfully.");
        } catch (SQLException e){
            System.out.println("Connection failed to close.");
        }
    }
}
