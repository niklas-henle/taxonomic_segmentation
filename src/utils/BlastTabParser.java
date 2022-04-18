package utils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BlastTabParser {

    public static void main(String[] args) throws IOException {
        ArrayList<String[]> test = blastTabParser("../../data/alignments/epbrr4mpbp8-Alphaproteobacteria-28211.blasttab");

    }

    private static ArrayList<String[]> blastTabParser(String path) throws IOException {

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
