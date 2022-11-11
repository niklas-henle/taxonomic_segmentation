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
    public static int mismatch = 4;
    public static int gapPenalty = 1;
    public static int gapLength = 0;
    public static String rank = "Genus";
    public static void main(String[] args) {

        Options flags  = new Options();

        Option fastA = new Option("f", "fasta", true, "FastA file");
        Option blastF = new Option("b", "blasttab", true, "blastTab file");
        Option database = new Option("db", "database", true, "database");
        Option taxa = new Option("t", "taxonomyMap", true, "taxonomy map");
        Option gtdb = new Option("gtdb", "gtdbMappingFile", true, "gtdb mapping file");
        Option output = new Option ("o", "ouputfile", true, "Path to output file");
        fastA.setRequired(true);
        flags.addOption(fastA);

        blastF.setRequired(true);
        flags.addOption(blastF);

        flags.addOption(database);
        flags.addOption(taxa);
        flags.addOption(gtdb);


        Option matchF = new Option("m", "match", true, "Matching score");

        Option switchF = new Option("s", "switch penalty ", true, "Mismatch penalty");
        Option gapF = new Option("g", "gap", true, "gap penalty");
        Option rankF = new Option("r", "rank", true, "Alignment Rank");

        flags.addOption(matchF);
        flags.addOption(switchF);
        flags.addOption(gapF);
        flags.addOption(rankF);
        flags.addOption(output);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(flags, args);

            match = Integer.parseInt(cmd.getOptionValue(matchF, String.valueOf(match)));
            mismatch = Integer.parseInt(cmd.getOptionValue(switchF, String.valueOf(mismatch)));
            gapPenalty = Integer.parseInt(cmd.getOptionValue(gapF, String.valueOf(gapPenalty)));
            rank = cmd.getOptionValue(rankF, rank);

            BufferedWriter writer = new BufferedWriter(new FileWriter(cmd.getOptionValue("outputfile", "taxonomic_segmentation.txt")));
            HashMap<String, ArrayList<Alignment>> blastTab;

            if (cmd.getOptionValue("gtdbMappingFile") != null
                    && cmd.getOptionValue("taxonomyMap") != null
                    && cmd.getOptionValue(database) != null ){

                blastTab = Utils.blastTabParser(cmd.getOptionValue(blastF),
                        new DatabaseConnector(cmd.getOptionValue(database),
                                cmd.getOptionValue("gtdbMappingFile"),
                                cmd.getOptionValue("taxonomyMap")));
            }
            else {
                blastTab = Utils.blastTabParser(cmd.getOptionValue(blastF),null);
            }

            HashMap<String, String> reads = Utils.fastAParser(cmd.getOptionValue(fastA));

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

                }
                String currentRead = reads.get(blast.get(0).readId());

                writer.write("==========================================================\n");
                writer.write(args[0]);
                writer.write("\n");
                writer.write("Read ID: " + blast.get(0).readId());
                writer.write("\n");
                writer.write("==========================================================\n");

                System.out.println("==========================================================");
                for (int i = 0; i < tb.size(); i++){
                    writer.write("=======");
                    writer.write("\n");
                    if (i == 0){
                        writer.write(tb.get(i).sseqid() + " starts at " + tb.get(i).qstart());
                        writer.write("\n");
                        writer.write(currentRead.substring(tb.get(i).qstart()-1, seg.eventIndexes[i]-2));
                    } else {
                        writer.write(tb.get(i).sseqid() + " starts at " + seg.eventIndexes[i] + "\n");
                        if (i == tb.size()) {
                            writer.write(currentRead.substring(seg.eventIndexes[i]-1, currentRead.length()-1));
                            writer.write("Ends at " + currentRead.length() +"\n");
                        } else {
                            writer.write(currentRead.substring(seg.eventIndexes[i]-1, seg.eventIndexes[i+1]-1));
                        }
                        writer.write("\n");
                    }
                }


            }

            writer.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
