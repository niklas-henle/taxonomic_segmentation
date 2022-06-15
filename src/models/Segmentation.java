package models;

import java.sql.Array;
import java.util.*;

public class Segmentation {

    public record Tuple(Integer value, ArrayList<String> ids) {

    }


    public HashMap<String, ArrayList<Alignment>> generateTable(IntervalTree tree) {

        HashMap<String, ArrayList<Alignment>> tab = new HashMap<>();

       ArrayList<IntervalNode> nodes = tree.traversal();

        for (IntervalNode n: nodes
             ) {
            if(tab.containsKey(n.interval.sseqid())) {
                tab.get(n.interval.sseqid()).add(n.interval);
            } else {
                tab.put(n.interval.sseqid(), new ArrayList<>(Arrays.asList(n.interval)));
            }
        }

        for (String s: tab.keySet()) {
            if (tab.get(s).size() > 1) {
                System.out.println(s + ": " + tab.get(s).size());
            }
        }
        List<Alignment>[] tabs = new List[57255];
        for(IntervalNode n: nodes){
            if(tabs[n.interval.qstart()] != null) {
                tabs[n.interval.qstart()].add(n.interval);
            } else {
                tabs[n.interval.qstart()] = new ArrayList<>(Arrays.asList(n.interval));
            }
            if(tabs[n.interval.qend()] != null) {
                tabs[n.interval.qend()].add(n.interval);
            } else {
                tabs[n.interval.qend()] = new ArrayList<>(Arrays.asList(n.interval));
            }
        }

        for (int i = 0; i < tabs.length; i++) {
           if(tabs[i] == null) {
               System.out.println("empty at " + i);
           }
        }

        return tab;

    }


}
