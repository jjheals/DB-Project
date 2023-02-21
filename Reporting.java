import java.sql.SQLException;

public class Reporting {
    public static void main(String[] args) {
        Query query = new Query(); // To make the query when needed

        // Try to connect to DB
        try { query.connection = DBConnection.openConnection(args[0], args[1]); }   // Pass the connection to query
        catch (SQLException e) {                                                    // Error
            System.out.println("DB Connection failed. Quitting.");
            return;
        }

        // Check how many arguments were given, act accordingly
        switch (args.length) {
            case 2: // 2 Arguments - List options
                System.out.println("Must include one of the following options: \n" +
                                   "1- Report Patient's basic information.\n" +
                                   "2- Report Doctor's basic information.\n" +
                                   "3- Report Admissions information.\n" +
                                   "4- Update Admissions payment.\n" +
                                   "\n" +
                                   "Usage: java Reporting.java [username] [password] [option]\n" +
                                   "\n" +
                                   "Quitting.");
                break;
            case 3: // 3 Arguments - Info for a specific object
                query.getInfo(Integer.parseInt(args[2])); // Make query
                break;
            default: // Invalid number of arguments
                System.out.println("Error: Invalid number of arguments. Quitting.");
        }
    }
}
