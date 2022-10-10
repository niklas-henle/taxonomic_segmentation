package models;

import models.records.Alignment;
import models.records.Tuple;

import java.util.*;

import static main.Main.*;

public class Segmentation {

    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {

        ArrayList<IntervalNode> nodes = tree.traversal();
        ArrayList<Alignment>[] tabs = new ArrayList[tree.getMaxEndValue() + 1];
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
        /**
        for(int i = 0; i < tabs.length; i++) {
            if(tabs[i] != null && tabs[i].size() == 1) {
                if(tabs[i].get(0).qstart() != i ||tabs[i].get(0).qend() != i ) {
                    tabs[i] = null;
                }
            }
        }
         **/

        ArrayList<ArrayList<Alignment>> ret = new ArrayList<>(Arrays.asList(tabs));
        ret.removeAll(Collections.singleton(null));

        return ret;

    }

    public HashMap<String, Tuple> generateDPTable(ArrayList<ArrayList<Alignment>> alignments) {

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



        for(int i = 0; i < alignments.size(); i++) {
            ArrayList<Alignment> M = alignments.get(i);
            if (i > 0 ) {
                M.addAll(alignments.get(i - 1));
            }
           // initialise the first column
            if (i == 0) {
                int maxM = 0;
                for(int m = 0; m < M.size(); m++) {
                    dp.put(M.get(m).sseqid(), new Tuple(M.get(m), init.clone()));

                    if (M.get(i).bitscore() > M.get(maxM).bitscore()) {
                        maxM = m;
                    }
                }
                dp.get(M.get(maxM).sseqid()).score()[0] = 1;
                continue;
            }
            System.out.println(i);
            System.out.println(M.size());
            for (Alignment alignment : M) {
                if (i == 931) {
                    System.out.println();
                }
                dp.putIfAbsent(alignment.sseqid(), new Tuple(alignment, init.clone()));

                float vi = emission.get(alignment.sseqid()) * getMaxScore(alignments.get(i - 1), i - 1, dp, alignment);
                float[] mScores = dp.get(alignment.sseqid()).score();
                mScores[i] = vi;
                dp.put(alignment.sseqid(), new Tuple(alignment, mScores));

            }
        }
        float[] t = dp.get("AVA24122.1").score();
        float last = dp.get("PWC61058.1").score()[init.length -1];
        float prev = dp.get("AVA24122.1").score()[init.length -1];

        return dp;
    }


    public ArrayList<Tuple> traceback(HashMap<String, Tuple> matrix) {
        List<float[]> matrix_scores = matrix.values().stream().map(Tuple::score).toList();
        ArrayList<Tuple> traceback = new ArrayList<>();
        String[] keys = matrix.keySet().toArray(new String[0]);
        int endIndex = matrix_scores.get(0).length-1;

        Tuple maxTax = matrix.get(keys[0]);
        float maxValue = 0;
        for (int i = endIndex; i >= 0; i--) {
            for(int j = 0; j < matrix_scores.size() ; j++) {
                if (maxValue <  matrix_scores.get(j)[i]) {
                    maxValue =  matrix_scores.get(j)[i];
                    maxTax = matrix.get(keys[j]);
                }
            }
            traceback.add(maxTax);
            if (i < 20) {
                if (traceback.get((endIndex - i)) != maxTax) {
                    System.out.println(maxValue);
                    System.out.println(maxTax);
                }
            }
        }
        return traceback;
    }

    private float getMaxScore(ArrayList<Alignment> Mi, int w , HashMap<String, Tuple> dp, Alignment current ) {
        float max = 0;
        for (Alignment a : Mi
             ) {
            float previousScore = dp.get(a.sseqid()).score()[w];
            float score = computeScore(a, current, previousScore);

                max = Math.max(max, previousScore + score);

        }
        return max;

    }

    private float computeScore(Alignment prev, Alignment current,  float previousScore) {
        if (prev.equals(current)) {
            gapLength = 0;
            return match;
        }
        else {
            if (previousScore - mismatch > previousScore - gapPenalty*gapLength) {
                gapLength = 0;
                return -mismatch;
            } else {
                float pen =  gapPenalty * gapLength;
                gapLength++;
                return -pen;
            }
        }
    }

}























