package main;

import jdk.jshell.execution.Util;
import utils.*;
import models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {


        try {
            DatabaseConnector dbConnector = new DatabaseConnector(args[2]);
            dbConnector.queryAccessionNumber("WP_042695806.1");
            String taxa = Utils.getTaxonomyFromTid(dbConnector.queryAccessionNumber("WP_042695806.1"));
            System.out.println(taxa);
            dbConnector.closeConnection();

            TSSeq fastA = Utils.fastAParser(args[0]);
            ArrayList<String[]> blastTab = Utils.blastTabParser(args[1]);

            fastA.setIntervalTree(Utils.buildTreeFromBlastTab(blastTab));
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
