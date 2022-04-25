package models;
// Taxon segmented sequence

/**
 * Sequence ids with associated taxon
 * 0 -> [ T1, T2, T5 ... ]
 * 1 -> [ T1, T2, ... ]
 * . -> [ T2, T5, ... ]
 * .
 * .
 * n -> [Ti, Tj, Tk ... ]
 */

public class TSSeq {
    String seqId;
    String seq; // query sequence of the alignment
    TaxonSegmentation segmentation; // List of sequence length identifier and taxon associated with the identifier.

    public TSSeq(String seqId) {
        this.seqId = seqId;
    }

    public TSSeq(String seqId, String seq, TaxonSegmentation segmentation) {
        this.seqId = seqId;
        this.seq = seq;
        this.segmentation = segmentation;
    }


    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }


    public TaxonSegmentation getSegmentation() {
        return segmentation;
    }

    public void setSegmentation(TaxonSegmentation segmentation) {
        this.segmentation = segmentation;
    }

}
