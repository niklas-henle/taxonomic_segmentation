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
        }
        else if (interval.qstart() > lowRoot){
            root.right = insert(root.right, interval);
        }
        else return root;


        // Update the balance factor of each node
        // And, balance the tree
        root.height = 1 + Math.max(checkForHeight(root.left), checkForHeight(root.left)) ;
        int balance = checkForHeight(root.left) - checkForHeight(root.right);
        if (balance > 1) {
            if (root.left != null && interval.qstart() < root.left.interval.qstart()) {
                return rotateRight(root);
            } else if (root.left != null && interval.qstart() > root.left.interval.qstart()) {
                root.left = rotateLeft(root.left);
                return rotateRight(root);
            }
        }
        if (balance < -1) {
            if (interval.qstart() > root.right.interval.qstart()) {
                return rotateLeft(root);
            } else if (interval.qstart() < root.right.interval.qstart()) {
                root.right = rotateRight(root.right);
                return rotateLeft(root);
            }
        }


        if (root.max < interval.qend()) {
            root.max = interval.qend();
        }

        return root;
    }

    boolean isLeaf(IntervalNode node) {
        if (node == null) {
            return true;
        }
        return node.right == null && node.left == null;
    }
    public int checkForHeight(IntervalNode node) {
        return node == null ? 0 : node.height;
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

    IntervalNode rotateRight(IntervalNode node) {
        IntervalNode leftChild = node.left;
        IntervalNode rightChild = leftChild.right;
        leftChild.right = node;
        node.left = rightChild;
        node.height = Math.max(checkForHeight(node.left), checkForHeight(node.right)) + 1;
        leftChild.height = Math.max(checkForHeight(leftChild.left), checkForHeight(leftChild.right)) + 1;
        return leftChild;
    }

    IntervalNode rotateLeft(IntervalNode node) {
        IntervalNode rightChild = node.right;
        IntervalNode leftChild = rightChild.left;
        rightChild.left = node;
        node.right = leftChild;
        node.height = Math.max(checkForHeight(node.left), checkForHeight(node.right)) + 1;
        rightChild.height = Math.max(checkForHeight(rightChild.left), checkForHeight(rightChild.right)) + 1;
        return rightChild;
    }
}
