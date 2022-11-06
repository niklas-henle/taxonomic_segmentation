package utils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static main.Main.rank;

/**
 * Database connection class containing helper functions to query and close connection
 */
public class DatabaseConnector {

    Connection connection;
    HashMap<String, String> gtdb;
    HashMap<String, String> taxa;

    /**
     * Constructor for the DatabaseConnector. Connect to the local database provided in db.
     * @param db path to the database
     * @throws SQLException SQL error
     */
    public DatabaseConnector(String db, String gtdb, String taxa) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:"+db);
        try {
            this.gtdb = Utils.readInGtdbMap(gtdb);
            this.taxa = Utils.readInTaxonmicMap(taxa, rank);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            if (Utils.hasColumn(res, "GTDB")) {
                return taxa.get(gtdb.get(res.getString("GTDB")));
            }
            else {
                return null;
            }
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
