package models;

import models.records.Alignment;

public class IntervalNode {
    Alignment alignment;
    int max, height;
    IntervalNode left, right;

    /**
     * Interval node holding the alignment als interval.
     * keeps track of maximum value and height of the subtrees
     * @param alignment the alignment contained in the node
     */
    public IntervalNode(Alignment alignment) {
        this.max = alignment.qend();
        this.alignment = alignment;
    }
    public Alignment getInterval() {
        return alignment;
    }

}

