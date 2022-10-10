package main;

import models.records.Alignment;
import models.records.Tuple;
import utils.*;
import models.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
            /*
            File bins = new File("./data/bins");
            File[] binListing = bins.listFiles();
            File alignments = new File("./data/alignments");
            File[] alignmentListing = alignments.listFiles();
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            if (binListing != null) {
             for (int i =0; i < binListing.length; i++) {
                    String binPath = binListing[i].getAbsolutePath();
                    String alignmentPath = alignmentListing[i].getAbsolutePath();
                    if (binPath.contains(".DS_Store") || alignmentPath.contains(".DS_Store")) {
                        continue;
                    }*/
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            TSSeq fastA = Utils.fastAParser(args[0]);
            ArrayList<String[]> blastTab = Utils.blastTabParser(args[1]);
            System.out.println(fastA.getSeq().length());

            fastA.setIntervalTree(Utils.buildTreeFromBlastTab(new ArrayList<>(blastTab)));

            Segmentation seg = new Segmentation();
            ArrayList<ArrayList<Alignment>> tab = seg.generateTable(fastA.getIntervalTree());
            HashMap<String, Tuple> dp = seg.generateDPTable(tab);
            ArrayList<Tuple> tb = seg.traceback(dp);

            HashMap<String, Integer> count = new HashMap<>();

            for (Tuple c: tb
            ) {
                String k = c.alignment().sseqid();
                if(count.containsKey(k)) {
                    count.put(k, count.get(k) + 1);
                } else {
                    count.put(k,1);
                }

            }
            writer.write("==========================================================\n");
            writer.write(args[0]);
            writer.write("\n");
            writer.write("==========================================================\n");

            System.out.println("==========================================================");
            System.out.println(args[0]);
            System.out.println("==========================================================");
            for (String k: count.keySet()
            ) {
                writer.write(k + ": " + count.get(k) +"\n");
                System.out.println(k + ": " + count.get(k));
            }

        //}
        //}
            writer.close();




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
