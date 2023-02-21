import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.LinkedList;

/** Query
 * Used for making a connection to the DB and returning specified information
 */
public class Query {
    Connection connection;

    /* ----- Pre-written query calls, just need arguments added ----- */
    String getPatientInfo = "SELECT * FROM Patient WHERE ssn = %d";

    String getDoctorInfo = "SELECT dID, fName, lName, gender, graduatedFrom specialty " +
                           "FROM Doctor INNER JOIN Employee " +
                           "ON doctor.dID = employee.id";

    String getAdmissionInfo = "SELECT a.admitNum, a.patientSSN, a.admitDate, a.totalPayment, s.rNumber, s.startDate, s.endDate, e.doctorID " +
                              "FROM Admission a " +
                              "JOIN StayIn s ON a.admitNum = s.admitNum " +
                              "JOIN Examine e ON a.admitNum = e.admissionNum " +
                              "WHERE a.admitNum = %d";

    String updateAdmissionPayment = "UPDATE Admission SET totalPayment = %d WHERE admitNum = %d";

    // Constructor
    public Query() {}

    /** Get info for a certain object
     * Takes no params, but uses this ID as the identifier in the query (patient ssn, doc ID, admission num)
     * @return formatted result of the query
     *
     * NOTE:
     *      1 = patient
     *      2 = doctor
     *      3 = admission
     *      4 = update admission payment
     *
     */
    public void getInfo(int type) {
        ResultSet result;       // Result from the query, sql obj
        String queryCall;       // Changes based on the type
        Statement stmt;         // Statement obj
        int ID;

        // Create statement object
        try { stmt = connection.createStatement(); }
        catch (SQLException e) {
            System.out.println("Error creating statement. Quitting.");
            return;
        }

        // Check the type
        switch (type) {
            case 1: // Return patient info
                ID = promptForID("Patient SSN");               // Get user input for patient's ssn
                queryCall = String.format(getPatientInfo, ID); // Query to be executed
                result = execute(queryCall, stmt);             // Execute query

                // Check for error
                if(result == null) {
                    System.out.println("Error with query execution. Does the patient exist? \nQuitting.");
                    return;
                }

                // Print results
                try {
                    while (result.next()) {
                        // Retrieve by column name
                        System.out.println("Patient SSN: " + result.getInt("ssn"));
                        System.out.println("Patient First Name: " + result.getString("firstName"));
                        System.out.println("Patient Last Name: " + result.getString("lastName"));
                        System.out.println("Patient Address: " + result.getString("address"));
                    }
                } catch (SQLException e) {
                    System.out.println("Error parsing result. Quitting.");
                    return;
                }
                break;

            case 2:
                // Return doctor info
                ID = promptForID("Doctor ID");                // Get user input for Doctor ID
                queryCall = String.format(getDoctorInfo, ID); // Query to be executed
                result = execute(queryCall, stmt);            // Execute query

                // Check for error
                if(result == null) {
                    System.out.println("Error with query execution. Does the doctor exist? \nQuitting.");
                    return;
                }

                // Print results
                try {
                    while (result.next()) {
                        // Retrieve by column name
                        System.out.println("Doctor ID: " + result.getInt("dID"));
                        System.out.println("Doctor First Name: " + result.getString("fName"));
                        System.out.println("Doctor Last Name: " + result.getString("lName"));
                        System.out.println("Doctor Gender: " + result.getString("gender"));
                        System.out.println("Doctor Graduated From: " + result.getString("graduatedFrom"));
                        System.out.println("Doctor Specialty: " + result.getString("specialty"));
                    }
                } catch (SQLException e) {
                    System.out.println("Error parsing result. Quitting.");
                    return;
                }
                break;

            case 3:
                // Return admission info
                ID = promptForID("Admission Number"); // Get user input for Admission number
                queryCall = String.format(getAdmissionInfo, ID);
                result = execute(queryCall, stmt);

                // Check for error
                if(result == null) {
                    System.out.println("Error executing query. Does the admission exist? \nQuitting.");
                    return;
                }

                // Print results
                try {
                    while (result.next()) {
                        // Retrieve by column name
                        System.out.println("Admission Number: " + result.getInt("admitNum"));
                        System.out.println("Patient SSN: " + result.getString("patientSSN"));
                        System.out.println("Admission Date: " + result.getString("admitDate"));
                        System.out.println("Total Payment: " + result.getString("totalPayment"));
                        System.out.println(String.format(
                                           "Rooms: \n" +
                                           "    RoomNum: %d     FromDate: %s     ToDate: %s",
                                           result.getInt("rNumber"), result.getDate("startDate"), result.getString("endDate")));
                        System.out.println(String.format(
                                           "Doctor examined patient in this admission: \n" +
                                           "    DoctorID: %d \n",
                                            result.getInt("doctorID")));
                    }
                } catch (SQLException e) {
                    System.out.println("Error parsing result. Quitting.");
                    return;
                }
                break;

            case 4: // Update admission info
                ID = promptForID("Admission Number");                 // Get the admission number
                int newTotal = promptForID("the new total payment");  // Get the new total to update to
                queryCall = String.format(updateAdmissionPayment, newTotal, ID); // format query call

                // Try to execute update statement
                try { stmt.executeQuery(queryCall); }
                catch (SQLException e) {
                    System.out.println("Error executing query. Does the admission exist? \nQuitting.");
                    return;
                }
                break;

            default: // Unrecognized type
                System.out.println("ERROR: Invalid type.");
                break;
        }
        DBConnection.closeConnection(this.connection); // Close DB connection
        return;
    }

    /** Asks for and returns the ID [ssn, dID, admissionNum] for the given type
     * @param type the ID type to prompt for. One of ["Patient SSN", "Doctor ID", "Admission Number"].
     * @return the ID collected from user input OR -1 on failure.
     */
    static int promptForID(String type) {
        int ID;                                                 // ID to return
        Scanner scanner = new Scanner(System.in);               // Scanner obj to read input
        LinkedList<String> validIDs = new LinkedList<String>();      // List of valid types
        validIDs.add("Patient SSN");
        validIDs.add("Doctor ID");
        validIDs.add("Admission Number");
        validIDs.add("the new total payment"); // Not an ID, but adding here saves redundant code

        // Check that the type given is valid
        if(!validIDs.contains(type)) {
            System.out.println("Invalid type given. Quitting.");
            return -1;
        }

        System.out.println(String.format("Enter %s: ", type)); // Prompt for ID based on type

        try { ID = scanner.nextInt(); }                        // Get user input
        catch (Exception e){                                   // Error in getting input
            System.out.println("Invalid input format. Quitting.");
            return -1;
        }
        return ID;
    }


    /** Query for patient information based on SSN & return the formatted result
     * @param queryCall query statement as string.
     * @param stmt Statement object to execute query with.
     * @return formatted result of query.
     */
    ResultSet execute(String queryCall, Statement stmt) {
        ResultSet result;                              // Result to return
        try { result = stmt.executeQuery(queryCall); } // Try to execute query
        catch (SQLException e) {                       // Error with query
            System.out.println("Error executing query.");
            return null;
        }
        return result;
    }
}
