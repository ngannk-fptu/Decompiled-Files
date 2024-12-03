/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.MergeableAddressableHeap
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.matching.blossom.v5;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jgrapht.alg.matching.blossom.v5.BlossomVEdge;
import org.jgrapht.alg.matching.blossom.v5.BlossomVNode;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTreeEdge;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.tree.PairingHeap;

class BlossomVTree {
    private static int currentId = 1;
    BlossomVTreeEdge[] first;
    BlossomVTreeEdge currentEdge;
    int currentDirection;
    double eps;
    double accumulatedEps;
    BlossomVNode root;
    BlossomVTree nextTree;
    MergeableAddressableHeap<Double, BlossomVEdge> plusPlusEdges;
    MergeableAddressableHeap<Double, BlossomVEdge> plusInfinityEdges;
    MergeableAddressableHeap<Double, BlossomVNode> minusBlossoms;
    int id;

    public BlossomVTree() {
    }

    public BlossomVTree(BlossomVNode root) {
        this.root = root;
        root.tree = this;
        root.isTreeRoot = true;
        this.first = new BlossomVTreeEdge[2];
        this.plusPlusEdges = new PairingHeap();
        this.plusInfinityEdges = new PairingHeap();
        this.minusBlossoms = new PairingHeap();
        this.id = currentId++;
    }

    public static BlossomVTreeEdge addTreeEdge(BlossomVTree from, BlossomVTree to) {
        BlossomVTreeEdge treeEdge = new BlossomVTreeEdge();
        treeEdge.head[0] = to;
        treeEdge.head[1] = from;
        if (from.first[0] != null) {
            from.first[0].prev[0] = treeEdge;
        }
        if (to.first[1] != null) {
            to.first[1].prev[1] = treeEdge;
        }
        treeEdge.next[0] = from.first[0];
        treeEdge.next[1] = to.first[1];
        from.first[0] = treeEdge;
        to.first[1] = treeEdge;
        to.currentEdge = treeEdge;
        to.currentDirection = 0;
        return treeEdge;
    }

    public void setCurrentEdges() {
        TreeEdgeIterator iterator = this.treeEdgeIterator();
        while (iterator.hasNext()) {
            BlossomVTreeEdge treeEdge = iterator.next();
            BlossomVTree opposite = treeEdge.head[iterator.getCurrentDirection()];
            opposite.currentEdge = treeEdge;
            opposite.currentDirection = iterator.getCurrentDirection();
        }
    }

    public void clearCurrentEdges() {
        this.currentEdge = null;
        TreeEdgeIterator iterator = this.treeEdgeIterator();
        while (iterator.hasNext()) {
            iterator.next().head[iterator.getCurrentDirection()].currentEdge = null;
        }
    }

    public void printTreeNodes() {
        System.out.println("Printing tree nodes");
        TreeNodeIterator iterator = this.treeNodeIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public String toString() {
        return "BlossomVTree pos=" + this.id + ", eps = " + this.eps + ", root = " + this.root;
    }

    public void addPlusPlusEdge(BlossomVEdge edge) {
        edge.handle = this.plusPlusEdges.insert((Object)edge.slack, (Object)edge);
    }

    public void addPlusInfinityEdge(BlossomVEdge edge) {
        edge.handle = this.plusInfinityEdges.insert((Object)edge.slack, (Object)edge);
    }

    public void addMinusBlossom(BlossomVNode blossom) {
        blossom.handle = this.minusBlossoms.insert((Object)blossom.dual, (Object)blossom);
    }

    public void removePlusPlusEdge(BlossomVEdge edge) {
        edge.handle.delete();
    }

    public void removePlusInfinityEdge(BlossomVEdge edge) {
        edge.handle.delete();
    }

    public void removeMinusBlossom(BlossomVNode blossom) {
        blossom.handle.delete();
    }

    public TreeNodeIterator treeNodeIterator() {
        return new TreeNodeIterator(this.root);
    }

    public TreeEdgeIterator treeEdgeIterator() {
        return new TreeEdgeIterator();
    }

    public class TreeEdgeIterator
    implements Iterator<BlossomVTreeEdge> {
        private int currentDirection;
        private BlossomVTreeEdge currentEdge;
        private BlossomVTreeEdge result;

        public TreeEdgeIterator() {
            this.currentEdge = BlossomVTree.this.first[0];
            this.currentDirection = 0;
            if (this.currentEdge == null) {
                this.currentEdge = BlossomVTree.this.first[1];
                this.currentDirection = 1;
            }
            this.result = this.currentEdge;
        }

        @Override
        public boolean hasNext() {
            if (this.result != null) {
                return true;
            }
            this.result = this.advance();
            return this.result != null;
        }

        @Override
        public BlossomVTreeEdge next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            BlossomVTreeEdge res = this.result;
            this.result = null;
            return res;
        }

        public int getCurrentDirection() {
            return this.currentDirection;
        }

        private BlossomVTreeEdge advance() {
            if (this.currentEdge == null) {
                return null;
            }
            this.currentEdge = this.currentEdge.next[this.currentDirection];
            if (this.currentEdge == null && this.currentDirection == 0) {
                this.currentDirection = 1;
                this.currentEdge = BlossomVTree.this.first[1];
            }
            return this.currentEdge;
        }
    }

    public static class TreeNodeIterator
    implements Iterator<BlossomVNode> {
        private BlossomVNode currentNode;
        private BlossomVNode current;
        private BlossomVNode treeRoot;

        public TreeNodeIterator(BlossomVNode root) {
            this.currentNode = this.current = root;
            this.treeRoot = root;
        }

        @Override
        public boolean hasNext() {
            if (this.current != null) {
                return true;
            }
            this.current = this.advance();
            return this.current != null;
        }

        @Override
        public BlossomVNode next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            BlossomVNode result = this.current;
            this.current = null;
            return result;
        }

        private BlossomVNode advance() {
            if (this.currentNode == null) {
                return null;
            }
            if (this.currentNode.firstTreeChild != null) {
                this.currentNode = this.currentNode.firstTreeChild;
                return this.currentNode;
            }
            while (this.currentNode != this.treeRoot && this.currentNode.treeSiblingNext == null) {
                this.currentNode = this.currentNode.parentEdge.getOpposite(this.currentNode);
            }
            this.currentNode = this.currentNode.treeSiblingNext;
            if (this.currentNode == this.treeRoot.treeSiblingNext) {
                this.currentNode = null;
            }
            return this.currentNode;
        }
    }
}

