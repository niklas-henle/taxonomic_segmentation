package main;

import utils.*;
import models.*;

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

            TSSeq fastA = Utils.fastAParser(args[0]);
            ArrayList<String[]> blastTab = Utils.blastTabParser(args[1]);
            System.out.println(fastA.getSeq().length());

            fastA.setIntervalTree(Utils.buildTreeFromBlastTab(new ArrayList<>(blastTab.subList(0,20))));

            Segmentation seg = new Segmentation();
            ArrayList<ArrayList<Alignment>> tab = seg.generateTable(fastA.getIntervalTree());
            HashMap<String, float[]> dp = seg.generateDPTable(tab);
            ArrayList<String> tb = seg.traceback(dp);

            for (String s: tb
                 ) {
                System.out.print(s + " ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
