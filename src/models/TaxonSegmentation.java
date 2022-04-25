package models;

import java.util.ArrayList;

public class TaxonSegmentation {
    private String seqId;
    private ArrayList<String>[] segmentation;

    public TaxonSegmentation(String seqId, int seqLength) {
        this.seqId = seqId;
        this.segmentation = new ArrayList[seqLength];

    }

    /**
     * Add the taxon to the segment in which it is contained
     * @param start start of the alignment segment
     * @param stop end of the alignment segment
     * @param taxon taxon which is aligned to the segment
     */
    public void addSegmentation(int start, int stop, String taxon){
        for(int i = start; i <= stop; i++) {
            if(segmentation[i] == null) {
                segmentation[i] = new ArrayList<>();
            }
            segmentation[i].add(taxon);

        }

    }



    // getter and setter methods
    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public ArrayList<String>[] getSegmentation() {
        return segmentation;
    }

    public void setSegmentation(ArrayList<String>[] segmentation) {
        this.segmentation = segmentation;
    }
}
