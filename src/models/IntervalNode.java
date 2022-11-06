package models;

import models.records.Alignment;

public class IntervalNode {
    Alignment alignment;
    int max, height;
    IntervalNode left, right;

    public IntervalNode(Alignment alignment) {
        this.max = alignment.qend();
        this.alignment = alignment;
    }
    public Alignment getInterval() {
        return alignment;
    }

}

