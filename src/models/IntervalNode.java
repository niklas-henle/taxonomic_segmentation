package models;

public class IntervalNode {
    Alignment interval;
    int max;
    IntervalNode left, right;

    IntervalNode emptyNode = new IntervalNode();

    public IntervalNode(){};
    public IntervalNode(Alignment interval, int max, IntervalNode left, IntervalNode right) {
        this.interval = interval;
        this.max = max;
        this.left = left;
        this.right = right;
    }


    /**
     * Insert a new interval into the tree
     * @param root
     * @param interval
     * @return
     */
    public IntervalNode insert(IntervalNode root, Alignment interval) {

        if (root == null || root == emptyNode) {
            return new IntervalNode(interval, interval.qend(), emptyNode, emptyNode);
        }

        int lowRoot = root.interval.qstart();

        if (interval.qstart() < lowRoot) {
            root.left = insert(root.left, interval);
        }

        else {
           root.right = insert(root.right, interval);
        }

        if (root.max < interval.qend()) {
            root.max = interval.qend();
        }

        return root;
    }

    /**
     * Check if i1 and i2 intersect with one another
     * @param i1 IntervalNode one
     * @param i2 IntervalNode two
     * @return boolean concerning the intersection
     */
    public boolean intersectingNode(IntervalNode i1, IntervalNode i2) {
        return (i1.getInterval().qstart() < i2.getInterval().qstart()) &&
                (i1.getInterval().qend() < i2.getInterval().qend()) ||
                (i2.getInterval().qstart() < i1.getInterval().qstart()) &&
                        (i2.getInterval().qend() < i1.getInterval().qend());
    }

    /**
     * check if i2 is contained in i1
     * @param i1 Wrapping interval
     * @param i2 included interval
     * @return boolean if i2 is contained in i1
     */

    public boolean containingNode(IntervalNode i1, IntervalNode i2) {
        return (i1.getInterval().qstart() < i2.getInterval().qstart()) &&
                (i1.getInterval().qend() > i2.getInterval().qend());
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
