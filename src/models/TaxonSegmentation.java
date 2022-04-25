package models;

import java.util.ArrayList;

public class TaxonSegmentation {
    public String seqId;
    public ArrayList<ArrayList<String>> segmentation = new ArrayList<>();

    public TaxonSegmentation(String seqId) {
        this.seqId = seqId;
    }

    /**
     * Add the taxon to the segment in which it is contained
     * @param start start of the alignment segment
     * @param stop end of the alignment segment
     * @param taxon taxon which is aligned to the segment
     */
    public void addSegmentation(int start, int stop, String taxon){
        for(int i = start; i <= stop; i++) {
            segmentation.get(i).add(taxon);
        }

    }



    // getter and setter methods
    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public ArrayList<ArrayList<String>> getSegmentation() {
        return segmentation;
    }

    public void setSegmentation(ArrayList<ArrayList<String>> segmentation) {
        this.segmentation = segmentation;
    }
}
