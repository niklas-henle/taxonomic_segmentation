package main;

import models.records.Alignment;
import models.records.Tuple;
import utils.*;
import models.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static int match = 3;
    public static int mismatch = 4;
    public static int gapPenalty = 1;
    public static int gapLength = 0;
    public static String rank = "Genus";
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

            HashMap<String, String> reads = Utils.fastAParser(args[0]);
            HashMap<String, ArrayList<Alignment>> blastTab = Utils.blastTabParser(args[1]);
            for ( ArrayList<Alignment> blast: blastTab.values()) {
                System.out.println("==========================================================");
                System.out.println("Read ID: " + blast.get(0).readId());
                IntervalTree tree = Utils.buildTreeFromBlastTab(blast);

                Segmentation seg = new Segmentation();
                ArrayList<ArrayList<Alignment>> tab = seg.generateTable(tree);
                HashMap<String, Tuple> dp = seg.generateDPTable(tab);
                ArrayList<Alignment> tb = seg.traceback(dp);
                Collections.reverse(tb);
                Alignment currentTbAl = tb.get(0);
                for (int i = 0; i < tb.size(); i++){

                            System.out.println("=======");
                            System.out.println(tb.get(i).sseqid());
                            System.out.println("Start :" + Math.min(tb.get(i).qstart(), seg.eventIndexes[i]));
                            System.out.println("End :" + (i == tb.size()-1 ? tb.get(i).qend():Math.max(currentTbAl.qend(), seg.eventIndexes[i+1])));
                            currentTbAl = tb.get(i);


                        //System.out.println("End :" + Math.max(tb.get(i).qstart(), seg.eventIndexes[i]));
                }

                writer.write("==========================================================\n");
                writer.write(args[0]);
                writer.write("\n");
                writer.write("Read ID: " + blast.get(0).readId());
                writer.write("\n");
                writer.write("==========================================================\n");

                System.out.println("==========================================================");
                System.out.println(args[0]);
                System.out.println("==========================================================");
                for (int i = 0; i < tb.size(); i++){
                    writer.write("=======");
                    writer.write("\n");
                    writer.write(tb.get(i).sseqid());
                    writer.write("\n");
                    writer.write("Start :" + Math.min(tb.get(i).qstart(), seg.eventIndexes[i]));
                    writer.write("\n");
                    writer.write("End :" + Math.max(tb.get(i).qstart(), seg.eventIndexes[i]));
                    writer.write("\n");
                }

                //}
                //}

            }

            writer.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
