package models;

public class IntervalTree {
    public IntervalNode getRoot() {
        return root;
    }

    public void setRoot(IntervalNode root) {
        this.root = root;
    }

    IntervalNode root;

    /**
     * public function for recursively adding a node to the tree
     * @param interval alignment used as interval
     */
    public void addNode(Alignment interval) {
        this.root = insert(this.root, interval);
    }

    /**
     * Insert a new interval into the tree
     *
     * @param root root node
     * @param interval alignment used as interval
     * @return new intervalNode
     */
    private IntervalNode insert(IntervalNode root, Alignment interval) {

        if (root == null) {
            return new IntervalNode(interval);
        }

        int lowRoot = root.interval.qstart();

        if (interval.qstart() < lowRoot) {
            root.left = insert(root.left, interval);
        } else {
            root.right = insert(root.right, interval);
        }

        if (root.max < interval.qend()) {
            root.max = interval.qend();
        }

        return root;
    }

    /**
     * Check if i1 and i2 intersect with one another
     *
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
     *
     * @param i1 Wrapping interval
     * @param i2 included interval
     * @return boolean if i2 is contained in i1
     */

    public boolean containingNode(IntervalNode i1, IntervalNode i2) {
        return (i1.getInterval().qstart() < i2.getInterval().qstart()) &&
                (i1.getInterval().qend() > i2.getInterval().qend());
    }

}
