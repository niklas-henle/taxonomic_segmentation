package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection class containing helper functions to query and close connection
 */
public class DatabaseConnector {

    Connection connection;

    /**
     * Constructor for the DatabaseConnector. Connect to the local database provided in db.
     * @param db path to the database
     * @throws SQLException SQL error
     */
    public DatabaseConnector(String db) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:"+db);
    }


    /**
     * Use the Accession number to retrieve the taxonomy id.
     * @param an Accession number
     * @return taxonomy id
     */
    public String queryAccessionNumber(String an) {
        an = an.split("\\.")[0]; // Strip accession number of version

        try {
            Statement query = connection.createStatement();
            ResultSet res = query.executeQuery(String.format("SELECT GTDB FROM mappings WHERE Accession='%s'", an));
            return res.getString("GTDB");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    /**
     * Close the current connection
     */
    public void closeConnection() {
        try
        {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e)
        {
            // connection close failed.
            System.err.println(e.getMessage());
        }
    }

}
