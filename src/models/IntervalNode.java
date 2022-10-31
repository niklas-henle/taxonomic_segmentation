package models;

import models.records.Alignment;

public class IntervalNode {
    Alignment interval;
    int max, height;
    IntervalNode left, right;

    public IntervalNode(Alignment interval) {
        this.max = interval.qend();
        this.interval = interval;
    }
    public Alignment getInterval() {
        return interval;
    }

}

