package models;

import models.records.Alignment;

public class IntervalNode {
    Alignment alignment;
    int max, min, height;
    IntervalNode left, right;

    public IntervalNode(Alignment alignment) {
        this.max = alignment.qend();
        this.min = alignment.qstart();
        this.alignment = alignment;
    }
    public Alignment getInterval() {
        return alignment;
    }

}

