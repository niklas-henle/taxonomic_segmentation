package utils;

import models.TSSeq;
import models.TaxonSegmentation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {


    /**
     * Use the information of the blastTab file to generate a segmentation for the sequence
     * @param blastTab blastTab array.
     * @return Hashmap Seiq, TSSeqs
     */
    public static HashMap<String, TSSeq> generateSegments(ArrayList<String[]> blastTab, HashMap<String, TSSeq> TSSeqs) {

        TaxonSegmentation currentTaxonSegment = null;
        for(String[] entry: blastTab) {
            if(currentTaxonSegment == null) {
                TSSeq currentTSSeq = TSSeqs.get(entry[0]);
                currentTaxonSegment = new TaxonSegmentation(entry[0], currentTSSeq.getSeq().length());

            } else if (!currentTaxonSegment.getSeqId().equals(entry[0])) {
                // If we finished the current sequence add it to the TaxonSegment and the sequence
                TSSeq currentTSSeq = TSSeqs.get(currentTaxonSegment.getSeqId());
                currentTSSeq.setSegmentation(currentTaxonSegment);
                TSSeqs.put(currentTSSeq.getSeqId(), currentTSSeq);
                if (TSSeqs.get(entry[0]).getSeq() == null) {
                    System.out.println(TSSeqs);
                }
                currentTaxonSegment = new TaxonSegmentation(entry[0], TSSeqs.get(entry[0]).getSeq().length());
            }
            currentTaxonSegment.addSegmentation(Integer.parseInt(entry[6]), Integer.parseInt(entry[7]), entry[1]);

        }
        
        return TSSeqs;
    }

    /**
     * Parse fastA file into a list of TSSeqs.
     * @param path path to fastA file
     * @return return Hashmap of Seqid, TSSeq
     * @throws IOException throws IOException
     */
    public static HashMap<String,TSSeq> fastAParser(String path) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(path));
        HashMap<String, TSSeq> parsedSequences = new HashMap<>();
        String line = reader.readLine();
        TSSeq currentTSSeq = null;
        StringBuilder currentSeq = new StringBuilder();

        while(line != null) {
            if (line.startsWith(">")) {

                if (currentTSSeq != null) {
                    currentTSSeq.setSeq(currentSeq.toString());
                    parsedSequences.put(currentTSSeq.getSeqId(), currentTSSeq);
                }
                currentTSSeq = new TSSeq(line.split(">")[1]);
                currentSeq = new StringBuilder();
            }
            else {
                currentSeq.append(line);
            }
            line = reader.readLine();
        }

        if (currentTSSeq != null) {
            currentTSSeq.setSeq(currentSeq.toString());
            parsedSequences.put(currentTSSeq.getSeqId(), currentTSSeq);
        }

        return parsedSequences;
    }

    /**
     *
     * @param path path to the blastTab file
     * @return Arraylist of the separated blastTab entries
     * <a href="https://www.metagenomics.wiki/tools/blast/blastn-output-format-6"></a>
     * @throws IOException throws exception
     */
    public static ArrayList<String[]> blastTabParser(String path) throws IOException {

        ArrayList<String[]> fileContent = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line = reader.readLine();
        while(line != null) {
            fileContent.add(line.split("\t"));
            line = reader.readLine();
        }
        return fileContent;
    }

}
