package models;

import java.sql.Array;
import java.util.*;

public class Segmentation {


    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {

        HashMap<String, ArrayList<Alignment>> tab = new HashMap<>();

        ArrayList<IntervalNode> nodes = tree.traversal();
        System.out.println(nodes.size());
        for (IntervalNode n : nodes
        ) {
            if (tab.containsKey(n.interval.sseqid())) {
                tab.get(n.interval.sseqid()).add(n.interval);
            } else {
                tab.put(n.interval.sseqid(), new ArrayList<>(Arrays.asList(n.interval)));
            }
        }

        ArrayList<Alignment>[] tabs = new ArrayList[tree.getMaxEndValue() + 1];
        for (IntervalNode n : nodes) {
            if (tabs[n.interval.qstart()] != null) {
                tabs[n.interval.qstart()].add(n.interval);
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
    public HashMap<String, ArrayList<Integer>> generateDPTable(ArrayList<ArrayList<Alignment>> source,
                                                                Alignment lastAlignment, int gapCount,
                                                                int i, HashMap<String, ArrayList<Integer>> tab )
    {

        if (i == source.size() -1 ) {
            return tab;
        }


        /**
         * Check if the current position is 0. If so take every available element and place
         * them as possible starting point (initiated with 0)
         */
        if (i == 0) {
            for (Alignment a: source.get(i)) {
                tab.putIfAbsent(a.sseqid(), new ArrayList<>(List.of(0)));
            }
        }

        if (lastAlignment != null &&  !source.get(i).contains(lastAlignment) && i > lastAlignment.qend()) {
            gapCount++;
            tab.get(lastAlignment.sseqid()).add(score(lastAlignment, null , gapCount, i, tab));
            generateDPTable(source, lastAlignment, gapCount, i +1, tab );
        }


        for (Alignment a: source.get(i)) {
            /**
             * Check if the lastAlignment is contained in the list
             * Switch lastAlignment to current alignment
             */
            tab.putIfAbsent(a.sseqid(), new ArrayList<>());
            tab.get(a.sseqid()).add(score(lastAlignment, a, gapCount, i, tab));

            tab = generateDPTable(source, a, gapCount, i +1, tab );


        }

        return tab;
    }

    private int score(Alignment last, Alignment current, int gapCount, int i, HashMap<String, ArrayList<Integer>> tab ) {

        int gapPenalty = 4;
        int match = 10;
        int mismatch = 5;

        if (last == null){
            return 0;
        }

        int lastScore = tab.get(last.sseqid()).get(tab.get(last.sseqid()).size() - 1);

        if (current == null) {
            // introduce gap penalty.
            return lastScore + (gapPenalty * gapCount);
        }
        else if (current.equals(last)) {
            return lastScore + match;
        }
        else {
            return lastScore - mismatch;
        }


    }




}























