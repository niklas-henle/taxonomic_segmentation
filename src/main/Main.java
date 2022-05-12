package main;

import jdk.jshell.execution.Util;
import utils.*;
import models.*;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    /**
     * public static void main(String[] args) throws IOException {
     * <p>
     * HashMap<String, TSSeq> tSSeqHashMap = Utils.fastAParser(args[0]);
     * ArrayList<String[]> blastTabFile = Utils.blastTabParser(args[1]);
     * tSSeqHashMap = Utils.generateSegments(blastTabFile, tSSeqHashMap);
     * <p>
     * int maxLength = 0;
     * <p>
     * for (TSSeq test: tSSeqHashMap.values()) {
     * for (ArrayList<String> seg : test.getSegmentation().getSegmentation()
     * ) {
     * if (seg != null) {
     * maxLength = Math.max(seg.size(), maxLength);
     * }
     * <p>
     * }
     * }
     * System.out.println(maxLength);
     * }
     **/

    public static void main(String[] args) {
       /*try {
           DatabaseConnector dbConnector = new DatabaseConnector(args[2]);
           dbConnector.queryAccessionNumber("WP_042695806.1");
           String taxa = Utils.getTaxonomyFromTid(dbConnector.queryAccessionNumber("WP_042695806.1"));
           System.out.println(taxa);
           dbConnector.closeConnection();
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }*//*

      Alignment[] intervals = { new Alignment("", 0,12, 0, 0),
               new Alignment("", 50,80, 0, 0),
               new Alignment("", 45,90, 0, 0),
               new Alignment("", 30,55, 0, 0),
               new Alignment("", 70,75, 0, 0)};
       IntervalTree tree = new IntervalTree();
       for (Alignment i: intervals
            ) {

           tree.addNode(i);
       }
       System.out.println(tree.getRoot().getRight().getRight().getMax());*/

        try {
            TSSeq fastA = Utils.fastAParser(args[0]);
            ArrayList<String[]> blastTab = Utils.blastTabParser(args[1]);

            fastA.setIntervalTree(Utils.buildTreeFromBlastTab(blastTab));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}