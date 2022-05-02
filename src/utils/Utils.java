package utils;

import models.TSSeq;
import models.TaxonSegmentation;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

                currentTaxonSegment = new TaxonSegmentation(entry[0], TSSeqs.get(entry[0]).getSeq().length());
            }
            currentTaxonSegment.addSegmentation(Integer.parseInt(entry[6]), Integer.parseInt(entry[7]), entry[1]);

        }

        if(currentTaxonSegment != null) {
            TSSeq currentTSSeq = TSSeqs.get(currentTaxonSegment.getSeqId());
            currentTSSeq.setSegmentation(currentTaxonSegment);
            TSSeqs.put(currentTSSeq.getSeqId(), currentTSSeq);
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

    /**
     * Use the taxonomy id to retrieve the scientific name.
     * @param tid taxonomy id
     * @return scientific name of the tid associated organism
     */
    public static String getTaxonomyFromTid(String tid) {
        try {
            URL url = new URL(String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&id=%s" +
                            "&retmode=json",
                    tid));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tid;
    }

}