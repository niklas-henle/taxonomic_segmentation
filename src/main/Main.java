package main;

import utils.*;
import models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

   /**
    public static void main(String[] args) throws IOException {

        HashMap<String, TSSeq> tSSeqHashMap = Utils.fastAParser(args[0]);
        ArrayList<String[]> blastTabFile = Utils.blastTabParser(args[1]);
        tSSeqHashMap = Utils.generateSegments(blastTabFile, tSSeqHashMap);

        int maxLength = 0;

        for (TSSeq test: tSSeqHashMap.values()) {
            for (ArrayList<String> seg : test.getSegmentation().getSegmentation()
            ) {
                if (seg != null) {
                    maxLength = Math.max(seg.size(), maxLength);
                }

            }
        }
        System.out.println(maxLength);
    }
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
       }*/

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
       System.out.println(tree.getRoot().getRight().getRight().getMax());

   }
}
