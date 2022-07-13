package main;

import utils.*;
import models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
            System.out.println(fastA.getSeq().length());

            fastA.setIntervalTree(Utils.buildTreeFromBlastTab(new ArrayList<>(blastTab.subList(0,10))));
            Alignment root = fastA.getIntervalTree().getRoot().getInterval();
            Segmentation seg = new Segmentation();
            ArrayList<ArrayList<Alignment>> tab = seg.generateTable(fastA.getIntervalTree());
            System.out.println(seg.generateDPTable(tab, null,0, 0, new HashMap<String, ArrayList<Integer>>()));

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
