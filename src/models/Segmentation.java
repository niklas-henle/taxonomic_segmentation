package models;

import models.records.Alignment;
import models.records.Tuple;

import java.util.*;
import java.util.stream.Collectors;

import static main.Main.*;

public class Segmentation {
    public int[] eventIndexes;
    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {
        System.out.println("==========================================================");
        System.out.println("Starting to generate computation Table");
        long startTime = System.currentTimeMillis();

        ArrayList<IntervalNode> nodes = tree.traversal();
        int maxEnd = tree.getMaxEndValue();
        ArrayList<Alignment>[] tabs = new ArrayList[maxEnd + 1];
        for (IntervalNode n : nodes) {
            for(int i=n.interval.qstart(); i < n.interval.qend()+1; i++) {
                if (tabs[i] != null ) {
                    tabs[i].add(n.interval);
                }
                else {
                    tabs[i] = new ArrayList<>(List.of(n.interval));
                }
            }
        }

        ArrayList<ArrayList<Alignment>> tmp =  new ArrayList<>(Arrays.asList(tabs));
        tmp.removeAll(Collections.singleton(null));
        tabs = tmp.toArray(new ArrayList[0]);
        List<ArrayList<Alignment>> list = new ArrayList<>();
        for (int i = 0; i < tabs.length; i++) {
            if (i == 0) {
                list.add(tabs[i]);
            }
            else if (tabs[i] != null  && !tabs[i-1].equals(tabs[i])){
                list.add(tabs[i]);
            }
        }
        tabs = list.toArray(new ArrayList[0]);

        ArrayList<ArrayList<Alignment>> ret = new ArrayList<>(Arrays.asList(tabs));
        ret.removeAll(Collections.singleton(null));

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");

        return ret;


    }

    public HashMap<String, Tuple> generateDPTable(ArrayList<ArrayList<Alignment>> alignments) {
        System.out.println("==========================================================");
        System.out.println("Starting to generate Dynamic Table");
        long startTime = System.currentTimeMillis();

        HashMap<String, Tuple> dp = new HashMap<>();
        HashMap<String, Float> emission = new HashMap<>();

        float [] init = new float[alignments.size()];

        Arrays.fill(init, 0);
        for (ArrayList<Alignment> al : alignments
             ) {
            for (Alignment a: al
                 ) {
                if (emission.containsKey(a.sseqid())) {
                    emission.put(a.sseqid(), emission.get(a.sseqid()) + 1f);
                }
                else {
                    emission.put(a.sseqid(), 1f);
                }
            }
        }

        emission.replaceAll((k, v) -> emission.get(k) / alignments.size());


        eventIndexes = new int[alignments.size()];
        for(int i = 0; i < alignments.size(); i++) {
            ArrayList<Alignment> M = (ArrayList<Alignment>) alignments.get(i).clone();
            int nextStart = M.get(0).qend();
            int thisStart = M.get(0).qstart();
            if (i < alignments.size()-1) {
                nextStart = getNextStartFromList(M, (ArrayList<Alignment>) alignments.get(i + 1).clone());
            }
            if (i < alignments.size()-1) {
                nextStart = getNextStartFromList(M, (ArrayList<Alignment>) alignments.get(i + 1).clone());
            }
            if (i > 1) {
                thisStart = eventIndexes[i-1];
            }
            eventIndexes[i] = nextStart;


            if (i > 0 ) {
                M.addAll(alignments.get(i - 1));
            }


            for (Alignment alignment : M) {

                // Initialise first column
                if (i == 0) {
                    dp.put(alignment.sseqid(), new Tuple(alignment, init.clone()));
                    continue;
                }
                dp.putIfAbsent(alignment.sseqid(), new Tuple(alignment, init.clone()));

                float vi = emission.get(alignment.sseqid()) * getMaxScore(alignments.get(i - 1), i - 1, dp, alignment, nextStart-thisStart);
                float[] mScores = dp.get(alignment.sseqid()).score();
                mScores[i] = vi;
                dp.put(alignment.sseqid(), new Tuple(alignment, mScores));

            }
        }

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");
        return dp;
    }

    private int getNextStartFromList(ArrayList<Alignment> current ,ArrayList<Alignment> next) {
        ArrayList<Alignment> newStarts = (ArrayList<Alignment>) next.clone();
        newStarts.removeAll(current);
        if (newStarts.size() == 0){
            ArrayList<Alignment> newEnds = (ArrayList<Alignment>) current.clone();
            newEnds.removeAll(next);
            return  newEnds.get(0).qend();
        }
        return newStarts.get(0).qstart();
    }


    public ArrayList<Alignment> traceback(HashMap<String, Tuple> matrix) {

        System.out.println("==========================================================");
        System.out.println("Starting traceback");
        long startTime = System.currentTimeMillis();

        List<float[]> matrix_scores = matrix.values().stream().map(Tuple::score).toList();
        ArrayList<Alignment> traceback = new ArrayList<>();
        String[] keys = matrix.keySet().toArray(new String[0]);

        int endIndex = matrix_scores.get(0).length-1;


        Tuple maxTax = matrix.get(keys[0]);
        float maxValue = 0;

        for (int i = endIndex; i >= 0; i--) {
            for(int j = 0; j < matrix_scores.size() ; j++) {
                if (maxValue <=  matrix_scores.get(j)[i]) {
                    maxValue =  matrix_scores.get(j)[i];
                    maxTax = matrix.get(keys[j]);
                }
            }
            traceback.add(maxTax.alignment());
        }

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");

        return traceback;
    }

    private float getMaxScore(ArrayList<Alignment> Mi, int w , HashMap<String, Tuple> dp, Alignment current, int length ) {
        float max = 0;
        for (Alignment a : Mi
             ) {
            float previousScore = dp.get(a.sseqid()).score()[w];
            float score = computeScore(a, current, previousScore, length);
            max = Math.max(max, previousScore + score);

        }
        return max;

    }

    private float computeScore(Alignment prev, Alignment current,  float previousScore, int length) {
        if (prev.equals(current)) {
            gapLength = 0;
            return match * length;
        }
        else {
            if (previousScore - mismatch > previousScore - gapPenalty*gapLength) {
                gapLength = 0;
                return -mismatch;
            } else {

                float pen = gapPenalty * gapLength;
                gapLength += length;
                return -pen;
            }
        }
    }

}























