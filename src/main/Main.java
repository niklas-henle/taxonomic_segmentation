package main;

import models.records.Alignment;
import models.records.Tuple;
import utils.*;
import models.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.cli.*;

public class Main {
    public static int match = 3;
    public static int switchPenalty = 5;
    public static int gapPenalty = 1;
    public static int gapOpenPenalty = 2;
    public static HashMap<String, Integer> gapLength = new HashMap<>();
    public static String rank = "Genus";
    public static HashMap<String, ArrayList<Summary>> summaryMap = new HashMap<>();
    private record Summary(String readId, Alignment a){}


    public static void main(String[] args) {

        Options flags  = new Options();

        Option fastA = new Option("f", "fasta", true, "FastA file");
        Option blastF = new Option("b", "blasttab", true, "blastTab file");
        Option database = new Option("db", "database", true, "database");
        Option taxa = new Option("t", "taxonomyMap", true, "taxonomy map");
        Option gtdb = new Option("gtdb", "gtdbMappingFile", true, "gtdb mapping file");
        Option output = new Option ("o", "ouputfile", true, "Path to output file");
        Option displayRead = new Option ("dr", "displayRead", false, "Display reads in output");
        Option summary = new Option ("summary", "summary", false, "Display reads in output");
        Option yml = new Option("yml", "yml", false, "Write the input data into a yml file");

        fastA.setRequired(true);
        flags.addOption(fastA);

        blastF.setRequired(true);
        flags.addOption(blastF);

        flags.addOption(database);
        flags.addOption(taxa);
        flags.addOption(gtdb);
        flags.addOption(displayRead);
        flags.addOption(summary);
        flags.addOption(yml);


        Option matchF = new Option("m", "match", true, "Matching score");

        Option switchF = new Option("s", "switch penalty ", true, "Mismatch penalty");
        Option gapF = new Option("g", "gap", true, "gap penalty");
        Option rankF = new Option("r", "rank", true, "Alignment Rank");
        Option gapOpen = new Option("go", "gapOpenPenalty", true, "gap opening penalty");

        flags.addOption(matchF);
        flags.addOption(switchF);
        flags.addOption(gapF);
        flags.addOption(rankF);
        flags.addOption(output);
        flags.addOption(gapOpen);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(flags, args);

            match = Integer.parseInt(cmd.getOptionValue(matchF, String.valueOf(match)));
            switchPenalty = Integer.parseInt(cmd.getOptionValue(switchF, String.valueOf(switchPenalty)));
            gapPenalty = Integer.parseInt(cmd.getOptionValue(gapF, String.valueOf(gapPenalty)));
            rank = cmd.getOptionValue(rankF, rank);
            gapOpenPenalty = Integer.parseInt(cmd.getOptionValue(gapOpen, String.valueOf(gapOpenPenalty)));
            String filename = cmd.getOptionValue(output, "taxonomic_segmentation");
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename+".txt"));
            HashMap<String, ArrayList<Alignment>> blastTab;

            if (cmd.getOptionValue(gtdb) != null
                    && cmd.getOptionValue(taxa) != null
                    && cmd.getOptionValue(database) != null ) {

                blastTab = Utils.blastTabParser(cmd.getOptionValue(blastF),
                        new DatabaseConnector(cmd.getOptionValue(database),
                                cmd.getOptionValue(gtdb),
                                cmd.getOptionValue(taxa)));
            }
            else {
                blastTab = Utils.blastTabParser(cmd.getOptionValue(blastF),null);
            }

            HashMap<String, String> reads = Utils.fastAParser(cmd.getOptionValue(fastA));


            if (cmd.hasOption(yml)) writeBlastToYml(blastTab, filename+"_original");

            for ( ArrayList<Alignment> blast: blastTab.values()) {
                String currentId = blast.get(0).readId();
                System.out.println("==========================================================");
                System.out.println("Read ID: " + currentId);
                IntervalTree tree = Utils.buildTreeFromBlastTab(blast);

                Segmentation seg = new Segmentation();
                ArrayList<ArrayList<Alignment>> tab = seg.generateTable(tree);
                HashMap<String, Tuple> dp = seg.generateDPTable(tab);
                ArrayList<Alignment> tb = seg.traceback(dp);
                Collections.reverse(tb);

                generateOutput(currentId, seg, tb, reads.get(currentId), tree.getMaxValue(),writer, cmd.hasOption(displayRead));

            }
            if (cmd.hasOption(summary)) generateSummary(filename, summaryMap);

            writer.close();



        } catch (IOException | SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * write the input blast file to a yaml file.
     * @param blastTab input file to be parsed
     * @param filename name of the file
     * @throws IOException
     */
    private static void writeBlastToYml(HashMap<String, ArrayList<Alignment>> blastTab, String filename) throws IOException {
        HashMap<String, ArrayList<Summary>> original = new HashMap<>();

        for (ArrayList<Alignment> b: blastTab.values()
             ) {
            for (Alignment a: b
                 ) {
                original.putIfAbsent(a.sseqid(), new ArrayList<>());
                original.get(a.sseqid()).add(new Summary(a.readId(), a));
            }
        }

        generateSummary(filename, original);

    }


    /**
     * generate the output for the run of a read.
     * @param readId current read id
     * @param seg segmentation object containing the list of eventsindex
     * @param tb traceback
     * @param currentRead sequence of the current read
     * @param maxEnd maximal end value of the alignments
     * @param writer Writer
     * @param showRead flag if the read should be written or not
     * @throws IOException
     */
    private static void generateOutput(String readId, Segmentation seg, ArrayList<Alignment> tb, String currentRead, int maxEnd, BufferedWriter writer, boolean showRead) throws IOException {
        System.out.println("Generating output for "+ readId);
        System.out.println("==========================================================");
        long startTime = System.currentTimeMillis();

        writer.write("==========================================================\n");
        writer.write("Read ID: " + readId);
        writer.write("\n");
        writer.write("==========================================================\n");

        System.out.println("==========================================================");
        Alignment previous = tb.get(0);
        int prevStart = seg.eventIndexes[0];

        for (int i = 0; i < tb.size(); i++){
            if (i == 0) {
                writer.write("\n");
                writer.write(tb.get(i).sseqid() + " starts at " + seg.eventIndexes[i] + "\n");

            }
            else {
                if (!previous.equals(tb.get(i))) {
                    writer.write("=======");
                    writer.write("\n");
                    writer.write(tb.get(i).sseqid() + " starts at " + seg.eventIndexes[i] + "\n");
                    previous = tb.get(i);
                    prevStart = seg.eventIndexes[i];
                }
                if (i < tb.size() - 1 && !previous.equals(tb.get(i + 1))) {
                    if (showRead) {
                        writer.write(currentRead.substring(prevStart, seg.eventIndexes[i + 1] - 1));

                        writer.write("\n");
                    }
                    summaryMap.putIfAbsent(previous.sseqid(), new ArrayList<>());
                    summaryMap.get(previous.sseqid()).add(new Summary(readId, new Alignment(readId,
                            previous.sseqid(), prevStart, seg.eventIndexes[i + 1] - 1, 0, 0)));

                }
            }

            if (i == tb.size()-1) {
                if (showRead) {
                    writer.write(currentRead.substring(prevStart, maxEnd-1));
                    writer.write("\n");
                }
                summaryMap.putIfAbsent(previous.sseqid(), new ArrayList<>());
                summaryMap.get(previous.sseqid()).add(new Summary(readId, new Alignment(readId,
                        previous.sseqid(), prevStart, maxEnd-1, 0,0 )));
                writer.write("Ends at " + (maxEnd-1) +"\n");
                writer.write("\n");
            }

        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");

    }

    /**
     * Generate a summary in form of a yaml file containing the alignment id with the reads it is contained in.
     * @param filename filename
     * @param summary List of summary object containing the read id and a list of alignments
     * @throws IOException
     */
    private static void generateSummary(String filename, HashMap<String, ArrayList<Summary>> summary) throws IOException {
        System.out.println("Generating Summary");
        System.out.println("==========================================================");
        long startTime = System.currentTimeMillis();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename+"_summary.yml"));
        for (String taxa: summary.keySet()
             ) {
            String lastReadId = "";
            ArrayList<Summary> taxSum = summary.get(taxa);
            writer.write(taxa + ":\n" );
            for (Summary sum: taxSum
                 ) {
                if(!lastReadId.equals(sum.readId)) {
                    writer.write("  " + sum.readId + ":\n");
                }
                writer.write("    [" + sum.a.qstart() + ":" +sum.a.qend()+"]\n");
                lastReadId = sum.readId;
            }

        }
        writer.close();
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");
    }

}
