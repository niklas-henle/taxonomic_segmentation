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

    /**
     * check for the height of the IntervalNode
     * @param node node to check the height for
     * @return height of the node
     */
    public int checkForHeight(IntervalNode node) {
        return node == null ? -1 : node.height;
    }

    /**
     * Perform a right rotation in order to keep the tree balanced
     * @param node node to rotate around
     * @return new rotated node
     */
    IntervalNode rotateRight(IntervalNode node) {
        IntervalNode leftChild = node.left;
        node.left = leftChild.right;
        leftChild.right = node;
        node.height = Math.max(checkForHeight(node.left), checkForHeight(node.right)) + 1;
        leftChild.height = Math.max(checkForHeight(leftChild.left), checkForHeight(leftChild.right)) + 1;
        return leftChild;
    }

    /**
     * Perform a left rotation in order to keep the tree balanced
     * @param node node to rotate around
     * @return new rotated node
     */
    IntervalNode rotateLeft(IntervalNode node) {
        IntervalNode rightChild = node.right;

        node.right = rightChild.left;
        rightChild.left = node;
        node.height = Math.max(checkForHeight(node.left), checkForHeight(node.right)) + 1;
        rightChild.height = Math.max(checkForHeight(rightChild.left), checkForHeight(rightChild.right)) + 1;
        return rightChild;
    }

    /**
     * wrapper function to get the max value of the tree.
     * Initiate the worker with the root node
     * @return int max value of the tree.
     */
    public int getMaxValue() {
       return getMaxEndValue(this.root);
    }

    /**
     * Worker function to get the max value of the tree
     * @param root current node
     * @return int maximal value contained in the tree
     */
    private int getMaxEndValue(IntervalNode root) {

        if(root.right == null) {
            return root.getInterval().qend();
        }

        return getMaxEndValue(root.right);
    }

    /**
     * wrapper function to get the min value of the tree.
     * Initiate the worker with the root node
     * @return int min value of the tree.
     */
    public int getMinValue() {
        return getMinValue(this.root);
    }

    /**
     * Worker function to get the min value of the tree
     * @param root current node
     * @return int minimal value contained in the tree
     */
    private int getMinValue(IntervalNode root) {

        if(root.left == null) {
            return root.getInterval().qstart();
        }

        return getMinValue(root.left);
    }

    /**
     * Wrapper function to retrieve the nodes that contain the index.
     * Initiates the worker with the root and an empty array list as accumulator
     * @param index index to look out for
     * @return ArrayList of Alignments that contain the index
     */
    public ArrayList<Alignment> getIntervalsIncludingFromRoot(int index) {
        return getIntervalsIncluding(this.root, index, new ArrayList<>());
    }

    /**
     * Worker function to retrieve the nodes that contain the index.
     * Recursive method with alignments as the accumulator
     * @param node current node
     * @param index index to look out for
     * @param alignments storage for the list of alignments containing the index
     * @return list of alignments containing the index
     */
    private ArrayList<Alignment> getIntervalsIncluding(IntervalNode node, int index, ArrayList<Alignment> alignments) {

        if (node == null || node.max < index) {
            return alignments;
        }


        if (node.getInterval().contains(index) && !alignments.contains(node.getInterval())) {
            alignments.add(node.getInterval());
        }

        if (node.alignment.qstart() < index) {
            alignments = getIntervalsIncluding(node.right, index, alignments);
        }
        alignments = getIntervalsIncluding(node.left, index, alignments);

        return alignments;
    }
}


























