package models;

import models.records.Alignment;
import models.records.Tuple;

import java.util.*;

import static main.Main.*;

public class Segmentation {
    public int[] eventIndexes;
    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {
        System.out.println("==========================================================");
        System.out.println("Starting to generate computation Table");
        long startTime = System.currentTimeMillis();
        int maxEnd = tree.getMaxValue();
        int minValue = tree.getMinValue();

        ArrayList<ArrayList<Alignment>> tabs = new ArrayList<>();

        for(int i = minValue; i < maxEnd+1; i++) {
            ArrayList<Alignment> alignmentsIncluding = tree.getIntervalsIncludingFromRoot(i);
            if (i > 32000) {
                System.out.println(alignmentsIncluding);
            }
            tabs.add(alignmentsIncluding.size() != 0 ? alignmentsIncluding: null);
        }

        tabs.removeAll(Collections.singleton(null));
        List<ArrayList<Alignment>> list = new ArrayList<>();
        for (int i = 0; i < tabs.size(); i++) {
            if (i == 0) {
                list.add(tabs.get(i));
            }
            else if (tabs.get(i) != null  && (hasDifference(tabs.get(i-1), tabs.get(i))
                    || hasDifference(tabs.get(i), tabs.get(i-1)))){
                list.add(tabs.get(i));
            }
        }

        list.removeAll(Collections.singleton(null));

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");

        return new ArrayList<>(list);


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
            if (previousScore -mismatch * length > previousScore - gapPenalty*gapLength) {
                gapLength = 0;
                return -mismatch * length;
            } else {

                float pen = gapPenalty * gapLength;
                gapLength += length;
                return -pen;
            }
        }
    }

    private boolean hasDifference(ArrayList<Alignment> a ,ArrayList<Alignment> b){
        ArrayList<Alignment> a_clone = (ArrayList<Alignment>) a.clone();
        a_clone.removeAll(b);
        return a_clone.size() != 0;

    }

    private int getNextStartFromList(ArrayList<Alignment> current ,ArrayList<Alignment> next) {
        ArrayList<Alignment> newStarts = (ArrayList<Alignment>) next.clone();
        if (hasDifference(newStarts, current)){
            ArrayList<Alignment> newEnds = (ArrayList<Alignment>) current.clone();
            if (hasDifference(newEnds, next)) {
                return next.get(0).qstart();
            }
            return  newEnds.get(0).qend();
        }
        return newStarts.get(0).qstart();
    }


}























