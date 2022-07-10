package models;

import java.sql.Array;
import java.util.*;

public class Segmentation {

    public record Tuple(Integer value, ArrayList<String> ids) {

    }


    public ArrayList<ArrayList<Alignment>> generateTable(IntervalTree tree) {

        HashMap<String, ArrayList<Alignment>> tab = new HashMap<>();

        ArrayList<IntervalNode> nodes = tree.traversal();
        System.out.println(nodes.size());
        for (IntervalNode n: nodes
             ) {
            if(tab.containsKey(n.interval.sseqid())) {
                tab.get(n.interval.sseqid()).add(n.interval);
            } else {
                tab.put(n.interval.sseqid(), new ArrayList<>(Arrays.asList(n.interval)));
            }
        }

        ArrayList<Alignment>[] tabs = new ArrayList[tree.getMaxEndValue()+1];
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
        ArrayList<ArrayList<Alignment>> ret = new ArrayList<>(Arrays.asList(tabs));
        ret.removeAll(Collections.singleton(null));

        ArrayList<String> tax = new ArrayList<>();
        for(ArrayList<Alignment> l: ret) {
            if (l != null) {
                for (Alignment a: l
                     ) {


                }
            }
            else {
                System.out.println("Empty");
            }
        }

        return new ArrayList<>(Arrays.asList(tabs));

    }


}
