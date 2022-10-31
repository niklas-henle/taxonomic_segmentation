package utils;

import models.records.Alignment;
import models.IntervalTree;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

    public static IntervalTree buildTreeFromBlastTab(ArrayList<Alignment> alignments) {
        System.out.println("==========================================================");
        System.out.println("Starting to build Tree from BlastTab ");
        long startTime = System.currentTimeMillis();
        IntervalTree tree = new IntervalTree();
        for (Alignment a: alignments
             ) {
            tree.addNode(a);
        }

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");
        return tree;

    }

    /**
     * Parse fastA file into a list of TSSeqs.
     * @param path path to fastA file
     * @return return Hashmap of Seqid, TSSeq
     * @throws IOException throws IOException
     */
    public static HashMap<String, String> fastAParser(String path) throws IOException {
        System.out.println("==========================================================");
        System.out.println("Starting to parse the .fasta file ");
        long startTime = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        HashMap<String, String> reads = new HashMap<>();
        StringBuilder currentSeq = new StringBuilder();

        String currentReadId = line.split(">")[1];
        line = reader.readLine();
        while(line != null) {
            if (line.startsWith(">")) {
                String readId = line.split(">")[1];
                if (!currentReadId.equals(readId)) {
                    reads.put(currentReadId, currentSeq.toString());
                    currentReadId = readId;
                    currentSeq = new StringBuilder();
                }
            }
            else {
                currentSeq.append(line);
            }
            line = reader.readLine();
        }
        reads.put(currentReadId, currentSeq.toString());

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;

        System.out.println("Took " + timeElapsed + " ms");

        return reads;
    }

    /**
     *
     * @param path path to the blastTab file
     * @return Arraylist of the separated blastTab entries
     * <a href="https://www.metagenomics.wiki/tools/blast/blastn-output-format-6"></a>
     * @throws IOException throws exception
     */
    public static HashMap<String, ArrayList<Alignment>> blastTabParser(String path) throws IOException {

        System.out.println("==========================================================");
        System.out.println("Starting to parse Blasttab ");
        long startTime = System.currentTimeMillis();

        HashMap<String, ArrayList<Alignment>> fileContent = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line = reader.readLine();
        while(line != null) {
            String[] blast = line.split("\t");
            fileContent.putIfAbsent(blast[0], new ArrayList<>());
            fileContent.get(blast[0]).add(new Alignment(blast[0], blast[1], Math.min(Integer.parseInt(blast[6]),
                    Integer.parseInt(blast[7])),Math.max(Integer.parseInt(blast[6]),
                    Integer.parseInt(blast[7])), Float.parseFloat(blast[10]), Float.parseFloat(blast[11])));
            line = reader.readLine();
        }

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");
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

    /**
     * check if the resultset contains the column label
     * @param res ResultSet
     * @param label Label
     * @return true/false
     */
    public static boolean hasColumn(ResultSet res, String label) {
        try {
            res.findColumn(label);
            return res.getString(label) != null;
        } catch (SQLException e) {
            return false;
        }
    }

}
