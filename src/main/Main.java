package main;

import utils.*;
import models.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static int match = 3;
    public static int mismatch = 4;
    public static int gapPenalty = 1;
    public static int gapLength = 0;
    public static void main(String[] args) {

        if (args.length == 6) {
            match = Integer.parseInt(args[3]);
            mismatch = Integer.parseInt(args[4]);
            gapPenalty = Integer.parseInt(args[5]);
        }

        try {
            File bins = new File("./data/bins");
            File[] binListing = bins.listFiles();
            File alignments = new File("./data/alignments");
            File[] alignmentListing = alignments.listFiles();

            if (binListing != null) {
                for (int i =0; i < binListing.length; i++) {
                    String binPath = binListing[i].getAbsolutePath();
                    String alignmentPath = alignmentListing[i].getAbsolutePath();
                    if (binPath.contains(".DS_Store") || alignmentPath.contains(".DS_Store")) {
                        continue;
                    }
                    TSSeq fastA = Utils.fastAParser(binListing[i].getAbsolutePath());
                    ArrayList<String[]> blastTab = Utils.blastTabParser(alignmentListing[i].getAbsolutePath());
                    System.out.println(fastA.getSeq().length());

                    fastA.setIntervalTree(Utils.buildTreeFromBlastTab(new ArrayList<>(blastTab)));

                    Segmentation seg = new Segmentation();
                    ArrayList<ArrayList<Alignment>> tab = seg.generateTable(fastA.getIntervalTree());
                    HashMap<String, float[]> dp = seg.generateDPTable(tab);
                    ArrayList<String> tb = seg.traceback(dp);

                    HashMap<String, Integer> count = new HashMap<>();
                    for (String c: tb
                    ) {
                        if(count.containsKey(c)) {
                            count.put(c, count.get(c) + 1);
                        } else {
                            count.put(c,1);
                        }
                    }

                    System.out.println("==========================================================");
                    System.out.println(binPath);
                    System.out.println("==========================================================");
                    for (String k: count.keySet()
                    ) {
                        System.out.println(k + ": " + count.get(k));
                    }

                }
            }




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
