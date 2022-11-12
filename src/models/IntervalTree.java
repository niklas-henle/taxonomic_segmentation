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

        int lowRoot = root.alignment.qstart();

        if (alignment.qstart() < lowRoot) {
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

        root.max = alignment.qend() > root.max? alignment.qend(): root.max;
        return root;
    }

    public int checkForHeight(IntervalNode node) {
        return node == null ? -1 : node.height;
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

    public int getMaxValue() {
       return getMaxEndValue(this.root);
    }

    private int getMaxEndValue(IntervalNode root) {

        if(root.right == null) {
            return root.getInterval().qend();
        }

        return getMaxEndValue(root.right);
    }

    public int getMinValue() {
        return getMinValue(this.root);
    }

    private int getMinValue(IntervalNode root) {

        if(root.left == null) {
            return root.getInterval().qstart();
        }

        return getMinValue(root.left);
    }

    public ArrayList<Alignment> getIntervalsIncludingFromRoot(int index) {
        return getIntervalsIncluding(this.root, index, new ArrayList<>());
    }

    private ArrayList<Alignment> getIntervalsIncluding(IntervalNode node, int index, ArrayList<Alignment> alignments) {

        if (node == null) {
            return alignments;
        }


        if (node.getInterval().contains(index) && !alignments.contains(node.getInterval())) {
            alignments.add(node.getInterval());
        }

        getIntervalsIncluding(node.left, index, alignments);
        getIntervalsIncluding(node.right,index, alignments);
        return alignments;
    }
}


























