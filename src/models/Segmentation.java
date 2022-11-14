package models;

import models.records.Alignment;
import models.records.Tuple;

import java.util.*;

import static main.Main.*;

public class Segmentation {
    public int[] eventIndexes;
    HashMap<String, Float> emission = new HashMap<>();
    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {
        System.out.println("==========================================================");
        System.out.println("Starting to generate computation Table");
        long startTime = System.currentTimeMillis();
        int maxEnd = tree.getMaxValue();
        int minValue = tree.getMinValue();

        ArrayList<ArrayList<Alignment>> tabs = new ArrayList<>();

        for(int i = minValue; i < maxEnd+1; i++) {
            ArrayList<Alignment> alignmentsIncluding = tree.getIntervalsIncludingFromRoot(i);
            tabs.add(alignmentsIncluding.size() != 0 ? alignmentsIncluding: null);
        }

        tabs.removeAll(Collections.singleton(null));
        List<ArrayList<Alignment>> list = new ArrayList<>();
        for (int i = 0; i < tabs.size(); i++) {
            if (i == 0) {
                list.add(tabs.get(i));
            }
            else if (tabs.get(i) != null  && (getDifference(tabs.get(i-1), tabs.get(i)).size() != 0
                    || getDifference(tabs.get(i), tabs.get(i-1)).size() != 0)){
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

        float [] init = new float[alignments.size()];

        Arrays.fill(init, 0);
        for (ArrayList<Alignment> al : alignments
             ) {
            for (Alignment a: al
                 ) {
                    emission.put(a.sseqid(), emission.containsKey(a.sseqid())? emission.get(a.sseqid()) + 1f: 1f);
                    dp.put(a.sseqid(), new Tuple(a, init.clone()));
            }
        }

        emission.replaceAll((k, v) -> emission.get(k) / alignments.size());


        eventIndexes = new int[alignments.size()];
        for(int i = 0; i < alignments.size(); i++) {
            ArrayList<Alignment> M = alignments.get(i);
            int nextStart = i < alignments.size()-1? getNextStartFromList(M, (ArrayList<Alignment>) alignments.get(i + 1).clone()): M.get(0).qend();
            int thisStart = i > 1? eventIndexes[i-1]: M.get(0).qstart();

            eventIndexes[i] = nextStart;



            for (Tuple v: dp.values()) {
                Alignment alignment = v.alignment();
                float score;
                if (i == 0) {
                    if (M.contains(alignment)) {
                        score = emission.get(alignment.sseqid()) * match * (nextStart-thisStart);
                    }
                    else {
                        score = emission.get(alignment.sseqid()) * -(gapOpenPenalty + gapPenalty * (nextStart-thisStart));
                        gapLength.put(alignment.sseqid(), nextStart-thisStart);
                    }

                }
                else {
                    score = emission.get(alignment.sseqid()) * getMaxScore(M, i - 1, dp, alignment, nextStart - thisStart);
                }
                dp.get(alignment.sseqid()).score()[i] = score;
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
        float maxValue = matrix_scores.get(0)[endIndex];
        int maxTaxInd = 0;
        for(int i = 0; i < keys.length; i++) {
            float curScore = matrix_scores.get(i)[endIndex];
            if (maxValue < curScore) {
                maxValue = curScore;
                maxTax = matrix.get(keys[i]);
                maxTaxInd = i;
            }
        }

        for (int i = endIndex; i >= 0; i--) {
            if(i == 0){
                traceback.add(maxTax.alignment());
                break;
            }
            float c_value = matrix_scores.get(maxTaxInd)[i];
            float n_value = matrix_scores.get(maxTaxInd)[i-1];
            if (emission.get(maxTax.alignment().sseqid()) * (n_value + match*(eventIndexes[i]-eventIndexes[i-1])) != c_value){
                for(int j = 0; j < matrix_scores.size(); j++) {
                    if(emission.get(keys[j]) * (matrix_scores.get(j)[i-1] - switchPenalty*(eventIndexes[i]-eventIndexes[i-1])) == c_value ){
                        maxTaxInd = j;
                        maxTax = matrix.get(keys[j]);
                        break;
                    }
                }
                traceback.add(maxTax.alignment());

            }
            else {
                traceback.add(maxTax.alignment());
            }
        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Took " + timeElapsed + " ms");

        return traceback;
    }

    private float getMaxScore(ArrayList<Alignment> Mi, int w , HashMap<String, Tuple> dp, Alignment current, int length ) {
        float max = -Float.MAX_VALUE;
        for (Alignment a : Mi
             ) {
            float previousScore = dp.get(current.sseqid()).score()[w];
            float switchScore = dp.get(a.sseqid()).score()[w];
            float score = computeScore(a, current, previousScore, switchScore,length);
            max = Math.max(max, score);

        }
        return max;

    }



    private float computeScore(Alignment prev, Alignment current,  float previousScore, float switchScore ,int length) {
        int currentGapLength = gapLength.getOrDefault(current.sseqid(), 0);
        if (prev.equals(current)) {
            gapLength.put(current.sseqid(),0);
            return previousScore + match * length;
        }
        else {

            float pen = currentGapLength == 0? gapOpenPenalty + gapPenalty * currentGapLength: gapPenalty * currentGapLength;

            if (switchScore - switchPenalty * length > previousScore - pen) {
                gapLength.put(current.sseqid(),0);
                return switchScore-switchPenalty * length;
            } else {
                gapLength.put(current.sseqid(), currentGapLength + length);
                return previousScore-pen;
            }
        }
    }

    private ArrayList<Alignment> getDifference(ArrayList<Alignment> a ,ArrayList<Alignment> b){
        ArrayList<Alignment> a_clone = (ArrayList<Alignment>) a.clone();
        a_clone.removeAll(b);
        return a_clone;

    }

    private int getNextStartFromList(ArrayList<Alignment> current ,ArrayList<Alignment> next) {
        ArrayList<Alignment> newStarts = getDifference((ArrayList<Alignment>) next.clone(), current);
        if (newStarts.size() == 0){
            ArrayList<Alignment> newEnds = getDifference((ArrayList<Alignment>) current.clone(), next);
            if (newEnds.size() == 0) {
                return next.get(0).qstart();
            }
            return  newEnds.get(0).qend();
        }
        return newStarts.get(0).qstart();
    }


}























