package models;

import java.util.*;

public class Segmentation {

    int gapPenalty = 5;
    int match = 2;
    int mismatch = 3 ;
    int gapCount = 0;

    private record Tuple(Alignment alignment, int score){
        protected Tuple max(Tuple comp) {
            return score < comp.score() ? comp : this;
        }
    }

    /*
    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {

        HashMap<String, ArrayList<Alignment>> tab = new HashMap<>();

        ArrayList<IntervalNode> nodes = tree.traversal();
        System.out.println(nodes.size());
        for (IntervalNode n : nodes
        ) {
            if (tab.containsKey(n.interval.sseqid())) {
                tab.get(n.interval.sseqid()).add(n.interval);
            } else {
                tab.put(n.interval.sseqid(), new ArrayList<>(List.of(n.interval)));
            }
        }

        ArrayList<Alignment>[] tabs = new ArrayList[tree.getMaxEndValue() + 1];
        for (IntervalNode n : nodes) {
            if (tabs[n.interval.qstart()] != null) {
                tabs[n.interval.qstart()].add(n.getInterval());
            } else {
                tabs[n.interval.qstart()] = new ArrayList<>(Arrays.asList(n.interval));
            }
            if (tabs[n.interval.qend()] != null) {
                tabs[n.interval.qend()].add(n.interval);
            } else {
                tabs[n.interval.qend()] = new ArrayList<>(Arrays.asList(n.interval));
            }
        }
        ArrayList<ArrayList<Alignment>> ret = new ArrayList<>(Arrays.asList(tabs));
        ret.removeAll(Collections.singleton(null));

        return ret;
    }

  */

    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {

        int max = tree.getMaxEndValue();

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
        System.out.println(tabs.length);
        ArrayList<ArrayList<Alignment>> ret = new ArrayList<>(Arrays.asList(tabs));
        ret.removeAll(Collections.singleton(null));
        System.out.println(ret.size());
        return ret;

    }




    /**
    public HashMap<String, int[]> generateDPTable(ArrayList<ArrayList<Alignment>> alignments) {


        int gapPenalty = 5;
        int match = 2;
        int mismatch = 3 ;
        int gapCount = 0;

        HashMap<String, int[]> F = new HashMap<>();
    **/
        /*
          initialise first row as potential starting points.
          Distinguish the starting points by marking them as 1.
          Every other point will be set to 0

          sid || position
              | 0 | 1 | 2 | 3 .... | n
          --------------------------------
          sid1 | 1 | 0 | 0 | 0 .... | 0
          sid2 | 1 | 0 | 0 | 0 .... | 0
          ...
         */
        /**
        Alignment previous = alignments.get(0).get(0);

        for (Alignment a: alignments.get(0)) {
            F.putIfAbsent(a.sseqid(), new int[alignments.size()]);
            F.get(a.sseqid())[0] = 1;
            previous = previous.eval() < a.eval() ? a: previous;
        }
        if (alignments.size() < 1) return F;

        for (int i = 1; i < alignments.size(); i++) {

            Alignment best = alignments.get(i).get(0);

            for (Alignment a: alignments.get(i).subList(1, alignments.get(i).size())) {
                best = best.eval() > a.eval() ? a: best;
            }

            F.putIfAbsent(best.sseqid(), new int[alignments.size()]);

            // continue with the previous alignment (point is within alignment thus no gap)
            Tuple continuedAlignment = new Tuple(previous,  F.get(previous.sseqid())[i-1] + match);

            // mismatch and thus introduce gap
            Tuple introduceGap = new Tuple(previous, F.get(previous.sseqid())[i-1] - ((gapCount+1) * gapPenalty));

            // mismatch and thus switch
            Tuple introduceMismatch = new Tuple(best, F.get(previous.sseqid())[i-1] - mismatch);

            // use best scoring one
            Tuple next = continuedAlignment.max(introduceGap.max(introduceMismatch));

            F.get(next.alignment.sseqid())[i] = next.score;
            if (next.score == F.get(previous.sseqid())[i-1] - ((gapCount+1) * gapPenalty)) {
                gapCount++;
            }
            previous = next.alignment;


        }



        return F;
    }
    **/

    public HashMap<String, ArrayList<Integer>> generateDPTable(ArrayList<ArrayList<Alignment>> source,
                                                                Alignment lastAlignment, int gapCount,
                                                                int i, HashMap<String, ArrayList<Integer>> tab )
    {
        //System.out.println(source.get(0));
        if (i == source.size() -1 ) {
            return tab;
        }



        if (i == 0) {
            for (Alignment a: source.get(i)) {
                tab.putIfAbsent(a.sseqid(), new ArrayList<>(0));
                tab = generateDPTable(source, a, 0, i + 1, tab);
            }
        }
        else {
            if (lastAlignment != null && !source.get(i).contains(lastAlignment) && source.get(i).get(0).qstart() > lastAlignment.qend()) {
                tab.get(lastAlignment.sseqid()).add(score(lastAlignment, null, gapCount + 1, i));
                tab = generateDPTable(source, lastAlignment, gapCount, i + 1, tab);
            }


            for (Alignment a : source.get(i)) {

                tab.putIfAbsent(a.sseqid(), new ArrayList<>());
                tab.get(a.sseqid()).add(score(lastAlignment, a, gapCount, i));

                tab = generateDPTable(source, a, gapCount, i + 1, tab);


            }
        }

        return tab;
    }


    private int score(Alignment last, Alignment current, int gapCount, int i ) {


        if (last == null){
            return 0;
        }

        // introduce gap penalty.
        int gap =  -gapPenalty * gapCount;

        return Math.max(match, Math.max(-mismatch, gap));


    }



}























