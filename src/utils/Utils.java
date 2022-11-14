package utils;

import models.records.Alignment;
import models.IntervalTree;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

    /**
     * build the interval tree based on the alignments gathered from the blast file.
     * @param alignments list of alignments from the blast file
     * @return Interval tree with the alignments as nodes.
     */
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
    public static HashMap<String, ArrayList<Alignment>> blastTabParser(String path, DatabaseConnector db) throws IOException {

        System.out.println("==========================================================");
        System.out.println("Starting to parse Blasttab ");
        long startTime = System.currentTimeMillis();

        HashMap<String, ArrayList<Alignment>> fileContent = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line = reader.readLine();
        while(line != null) {
            String[] blast = line.split("\t");
            String id = blast[1];
            if (db != null) {
                id = db.queryAccessionNumber(id);
                if (id == null) {
                    line = reader.readLine();
                    continue;
                }
            }
            fileContent.putIfAbsent(blast[0], new ArrayList<>());
            fileContent.get(blast[0]).add(new Alignment(blast[0], id, Math.min(Integer.parseInt(blast[6]),
                    Integer.parseInt(blast[7])),Math.max(Integer.parseInt(blast[6]),
                    Integer.parseInt(blast[7])), Float.parseFloat(blast[10]), Float.parseFloat(blast[11])));
            line = reader.readLine();
        }
        if (db != null) {
            db.closeConnection();
        }
        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");
        return fileContent;
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

    /**
     * read in the given mapping file as a HashMap
     * @param path path to the mapping file
     * @param depth one of "domain", "phylum", "class", "family", "order", "genus", "species"
     * @return Hashmap with the Accession as key and the GTDB taxonomy as value
     * @throws IOException
     */
    public static HashMap<String, String> readInTaxonmicMap(String path, String depth) throws IOException {
        System.out.println("==========================================================");
        System.out.println("Starting to parse Mapping file ");
        long startTime = System.currentTimeMillis();
        String prefix = switch (depth.toLowerCase()) {
            case "domain" -> "d__";
            case "phylum" -> "p__";
            case "class" -> "c__";
            case "order" -> "o__";
            case "family" -> "f__";
            case "genus" -> "g__";
            case "species" -> "s__";
        };

        HashMap<String, String> fileContent = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line = reader.readLine();
        while(line != null) {
            String[] entry = line.split("\t");
            fileContent.put(entry[0].split("\\.")[0], entry[1].split(prefix)[1].split(";")[0]);
            line = reader.readLine();
        }

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");
        return fileContent;
    }

    /**
     * Read the given mapping file as Hashmap
     * @param path path to the mapping file
     * @return Hashmap containing the GTDB identifier as Keys and the Accession as values.
     * @throws IOException
     */
    public static HashMap<String, String> readInGtdbMap(String path) throws IOException {
        System.out.println("==========================================================");
        System.out.println("Starting to parse Mapping file ");
        long startTime = System.currentTimeMillis();

        HashMap<String, String> fileContent = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line = reader.readLine();
        while(line != null) {
            String[] entry = line.split("\t");
            if (entry.length >= 5){
                fileContent.put(entry[0], entry[4].split("\"")[1]);
            }
            line = reader.readLine();
        }

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");
        return fileContent;
    }

}
