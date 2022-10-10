package models;

public class TSSeq {
    String seqId;
    String seq; // query sequence of the alignment
    IntervalTree intervalTree; // List of sequence length identifier and taxon associated with the identifier.

    public TSSeq(String seqId) {
        this.seqId = seqId;
    }

    public TSSeq(String seqId, String seq, IntervalTree intervals) {
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

    public IntervalTree getIntervalTree() {
        return intervalTree;
    }

    public void setIntervalTree(IntervalTree intervalTree) {
        this.intervalTree = intervalTree;
    }
}
