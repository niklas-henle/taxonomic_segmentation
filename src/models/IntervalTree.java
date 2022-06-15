package models;


import java.util.ArrayList;

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

        int lowRoot = root.interval.qend();

        if (interval.qend() < lowRoot) {
            root.left = insert(root.left, interval);
        }
        else {
            root.right = insert(root.right, interval);
        }


        // Update the balance factor of each node
        // And, balance the tree
        root.height = 1 + Math.max(checkForHeight(root.right), checkForHeight(root.left)) ;
        int balance = checkForHeight(root.right) - checkForHeight(root.left);
        if (balance < -1) {
            if ( checkForHeight(root.left.right) - checkForHeight(root.left.left) > 0) {
                root.left = rotateLeft(root.left);
            }
            root = rotateRight(root);
        }

        // Right-heavy?
        if (balance > 1) {
            if ( checkForHeight(root.right.right) - checkForHeight(root.right.left) < 0) {
                root.right = rotateRight(root.right);
            }
            root = rotateLeft(root);
        }


        if (root.max < interval.qend()) {
            root.max = interval.qend();
        }

        return root;
    }

    public int checkForHeight(IntervalNode node) {
        return node == null ? -1 : node.height;
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
        node.left = leftChild.right;
        leftChild.right = node;
        node.height = Math.max(checkForHeight(node.left), checkForHeight(node.right)) + 1;
        leftChild.height = Math.max(checkForHeight(leftChild.left), checkForHeight(leftChild.right)) + 1;
        return leftChild;
    }

    IntervalNode rotateLeft(IntervalNode node) {
        IntervalNode rightChild = node.right;

        node.right = rightChild.left;
        rightChild.left = node;
        node.height = Math.max(checkForHeight(node.left), checkForHeight(node.right)) + 1;
        rightChild.height = Math.max(checkForHeight(rightChild.left), checkForHeight(rightChild.right)) + 1;
        return rightChild;
    }

    /**
     * Wrapper function for tree traversal
     */

    public ArrayList<IntervalNode> traversal() {
        return depthFirstTraversal(root, new ArrayList<>());
    }

    /**
     * depth first traversal of the tree
     * @param node root node
     * @param traversal acc list for the traversal
     * @return arraylist of the interval nodes
     */
    private ArrayList<IntervalNode> depthFirstTraversal( IntervalNode node, ArrayList<IntervalNode> traversal) {
        if (node == null) {
            return traversal;
        }

        traversal.add(node);
        depthFirstTraversal(node.left, traversal);
        depthFirstTraversal(node.right, traversal);
        return traversal;

    }

    public Integer getMaxEndValue() {
       return getMaxEndValue(this.root);
    }

    private Integer getMaxEndValue(IntervalNode root) {

        if(root.right == null) {
            return root.getInterval().qend();
        }

        return getMaxEndValue(root.right);
    }
}
