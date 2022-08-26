package models;

import java.util.*;

public class Segmentation {

    int gapPenalty = 5;
    int match = 2;
    int mismatch = -3 ;
    int gapCount = 0;

    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {

        ArrayList<IntervalNode> nodes = tree.traversal();
        ArrayList<Alignment>[] tabs = new ArrayList[tree.getMaxEndValue() + 1];
        for (IntervalNode n : nodes) {

            for(int i=n.interval.qstart(); i < n.interval.qend()+1; i++) {
                if (tabs[i] != null ) {
                    tabs[i].add(n.interval);
                }
                else {
                    tabs[i] = new ArrayList<>(Arrays.asList(n.interval));
                }
            }
        }
        for(int i = 0; i < tabs.length; i++) {
            if(tabs[i].size() == 1) {
                if(tabs[i].get(0).qstart() != i ||tabs[i].get(0).qend() != i ) {
                    tabs[i] = null;
                }
            }
        }

        ArrayList<ArrayList<Alignment>> ret = new ArrayList<>(Arrays.asList(tabs));
        ret.removeAll(Collections.singleton(null));

        return ret;

    }

    public HashMap<String, float[]> generateDPTable(ArrayList<ArrayList<Alignment>> alignments) {

        HashMap<String, float[]> dp = new HashMap<>();
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

        for (String key: emission.keySet()) {
            emission.put(key, emission.get(key) / alignments.size());
        }



        for(int i = 0; i < alignments.size(); i++) {
            ArrayList<Alignment> M = alignments.get(i);

           // initialise the first column
            if (i == 0) {
                int maxM = 0;
                for(int m = 0; m < M.size(); m++) {
                    dp.put(M.get(m).sseqid(), init.clone());

                    if (M.get(i).bitscore() > M.get(maxM).bitscore()) {
                        maxM = m;
                    }
                }
                dp.get(M.get(maxM).sseqid())[0] = 1;
                continue;
            }

            for(int m = 0; m < M.size(); m++) {

                dp.putIfAbsent(M.get(m).sseqid(), init.clone());

                float vi = emission.get(M.get(m).sseqid()) * getMaxScore(alignments.get(i-1), i-1, dp, M.get(m));
                float[] mScores = dp.get(M.get(m).sseqid());
                mScores[i] = vi;
                dp.put(M.get(m).sseqid(), mScores);

            }
        }

        return dp;
    }


    public ArrayList<String> traceback(HashMap<String, float[]> matrix) {
        List<float[]> matrix_scores = matrix.values().stream().toList();
        ArrayList<String> traceback = new ArrayList<>();
        String[] keys = matrix.keySet().toArray(new String[0]);
        float maxValue = 0;
        int endIndex = matrix_scores.get(0).length-1;

        for (int i = endIndex; i > 0; i--) {
            maxValue = 0;
            String maxTax = keys[0];
            for(int j = 0; j < matrix_scores.size() ; j++) {
                if (maxValue <  matrix_scores.get(j)[i]) {
                    maxValue =  matrix_scores.get(j)[i];
                    maxTax = keys[j];
                }
            }
            traceback.add(maxTax);
        }
        return traceback;
    }

    private float getMaxScore(ArrayList<Alignment> Mi, int w , HashMap<String, float[]> dp, Alignment current ) {
        float max = 0;
        for (Alignment a : Mi
             ) {
            float score = computeScore(a, current);

                max = Math.max(max, dp.get(a.sseqid())[w] + score);

        }
        return max;

    }

    private float computeScore(Alignment prev, Alignment current) {
        if (prev.equals(current)) {
            return match;
        }
        else {
            return mismatch;
        }
    }

}























