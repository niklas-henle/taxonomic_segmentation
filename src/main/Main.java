package main;

import utils.*;
import models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {

        HashMap<String, TSSeq> tSSeqHashMap = Utils.fastAParser(args[0]);
        ArrayList<String[]> blastTabFile = Utils.blastTabParser(args[1]);
        tSSeqHashMap = Utils.generateSegments(blastTabFile, tSSeqHashMap);

        TSSeq test = tSSeqHashMap.get("869");
        int maxLength = 0;
        for (ArrayList<String> seg: test.getSegmentation().getSegmentation()
             ) {
            if (seg != null) {
                maxLength = Math.max(seg.size(), maxLength);
            }

        }
        System.out.println(maxLength);

    }
}
