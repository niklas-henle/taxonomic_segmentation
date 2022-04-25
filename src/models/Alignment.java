package models;

public class Alignment {
    String sseqid;
    int qstart, qend, bitscore;
    float eval;

    public Alignment(String sseqid, int qstart, int qend, float eval, int bitscore) {
        this.sseqid = sseqid;
        this.qstart = qstart;
        this.qend = qend;
        this.eval = eval;
        this.bitscore = bitscore;
    }

    // Getter and setter methods for all the attributes

    public int getQend() {
        return qend;
    }

    public void setQend(int qend) {
        this.qend = qend;
    }

    public int getBitscore() {
        return bitscore;
    }

    public void setBitscore(int bitscore) {
        this.bitscore = bitscore;
    }

    public float getEval() {
        return eval;
    }

    public void setEval(float eval) {
        this.eval = eval;
    }

    public String getSseqid() {
        return sseqid;
    }

    public void setSseqid(String sseqid) {
        this.sseqid = sseqid;
    }
}
