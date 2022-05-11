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
    IntervalNode intervalTree; // List of sequence length identifier and taxon associated with the identifier.

    public TSSeq(String seqId) {
        this.seqId = seqId;
    }

    public TSSeq(String seqId, String seq, IntervalNode intervals) {
        this.seqId = seqId;
        this.seq = seq;
        this.intervalTree = intervals;
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

    public IntervalNode getIntervalTree() {
        return intervalTree;
    }

    public void setIntervalTree(IntervalNode intervalTree) {
        this.intervalTree = intervalTree;
    }
}
