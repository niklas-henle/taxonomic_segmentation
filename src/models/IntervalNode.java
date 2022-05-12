package models;

public class IntervalNode {
    Alignment interval;
    int max;
    IntervalNode left, right;

    public IntervalNode(Alignment interval) {
        this.max = interval.qend();
        this.interval = interval;
        this.left = null;
        this.right = null;
    }
    /**
     * Getter and setter methods
     * =============================================================================================================
     */

    public Alignment getInterval() {
        return interval;
    }

    public void setInterval(Alignment interval) {
        this.interval = interval;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public IntervalNode getLeft() {
        return left;
    }

    public void setLeft(IntervalNode left) {
        this.left = left;
    }

    public IntervalNode getRight() {
        return right;
    }

    public void setRight(IntervalNode right) {
        this.right = right;
    }
}

