package models;


import models.records.Alignment;

import java.util.ArrayList;

public class IntervalTree {

    IntervalNode root;

    /**
     * public function for recursively adding a node to the tree
     * @param alignment alignment used as alignment
     */
    public void addNode(Alignment alignment) {
        this.root = insert(this.root, alignment);
    }

    /**
     * Insert a new alignment into the tree
     *
     * @param root root node
     * @param alignment alignment used as alignment
     * @return new alignmentNode
     */

    private IntervalNode insert(IntervalNode root, Alignment alignment) {

        if (root == null) {
            return new IntervalNode(alignment);
        }

        int lowRoot = root.alignment.qend();

        if (alignment.qend() < lowRoot) {
            root.left = insert(root.left, alignment);
        }
        else {
            root.right = insert(root.right, alignment);
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


        if (root.max < alignment.qend()) {
            root.max = alignment.qend();
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
     * @param i1 Wrapping alignment
     * @param i2 included alignment
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
     * @return arraylist of the alignment nodes
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

    public int getMaxEndValue() {
       return getMaxEndValue(this.root);
    }

    private int getMaxEndValue(IntervalNode root) {

        if(root.right == null) {
            return root.getInterval().qend();
        }

        return getMaxEndValue(root.right);
    }
}
