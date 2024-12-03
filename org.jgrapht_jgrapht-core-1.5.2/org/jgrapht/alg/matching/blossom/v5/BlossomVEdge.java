/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap$Handle
 */
package org.jgrapht.alg.matching.blossom.v5;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jgrapht.alg.matching.blossom.v5.BlossomVNode;
import org.jheaps.AddressableHeap;

class BlossomVEdge {
    final int pos;
    AddressableHeap.Handle<Double, BlossomVEdge> handle;
    double slack;
    BlossomVNode[] headOriginal = new BlossomVNode[2];
    BlossomVNode[] head = new BlossomVNode[2];
    BlossomVEdge[] prev;
    BlossomVEdge[] next = new BlossomVEdge[2];

    public BlossomVEdge(int pos) {
        this.prev = new BlossomVEdge[2];
        this.pos = pos;
    }

    public BlossomVNode getOpposite(BlossomVNode endpoint) {
        if (endpoint != this.head[0] && endpoint != this.head[1]) {
            return null;
        }
        return this.head[0] == endpoint ? this.head[1] : this.head[0];
    }

    public BlossomVNode getCurrentOriginal(BlossomVNode endpoint) {
        if (endpoint != this.head[0] && endpoint != this.head[1]) {
            return null;
        }
        return this.head[0] == endpoint ? this.headOriginal[0] : this.headOriginal[1];
    }

    public int getDirFrom(BlossomVNode current) {
        return this.head[0] == current ? 1 : 0;
    }

    public String toString() {
        return "BlossomVEdge (" + this.head[0].pos + "," + this.head[1].pos + "), original: [" + this.headOriginal[0].pos + "," + this.headOriginal[1].pos + "], slack: " + this.slack + ", true slack: " + this.getTrueSlack() + (this.getTrueSlack() == 0.0 ? ", tight" : "");
    }

    public double getTrueSlack() {
        double result = this.slack;
        if (this.head[0].tree != null) {
            result = this.head[0].isPlusNode() ? (result -= this.head[0].tree.eps) : (result += this.head[0].tree.eps);
        }
        if (this.head[1].tree != null) {
            result = this.head[1].isPlusNode() ? (result -= this.head[1].tree.eps) : (result += this.head[1].tree.eps);
        }
        return result;
    }

    public void moveEdgeTail(BlossomVNode from, BlossomVNode to) {
        int dir = this.getDirFrom(from);
        from.removeEdge(this, dir);
        to.addEdge(this, dir);
    }

    public BlossomNodesIterator blossomNodesIterator(BlossomVNode root) {
        return new BlossomNodesIterator(root, this);
    }

    public static class BlossomNodesIterator
    implements Iterator<BlossomVNode> {
        private BlossomVNode root;
        private BlossomVNode currentNode;
        private BlossomVNode current;
        private int currentDirection;
        private BlossomVEdge blossomFormingEdge;

        public BlossomNodesIterator(BlossomVNode root, BlossomVEdge blossomFormingEdge) {
            this.root = root;
            this.blossomFormingEdge = blossomFormingEdge;
            this.currentNode = this.current = blossomFormingEdge.head[0];
            this.currentDirection = 0;
        }

        @Override
        public boolean hasNext() {
            if (this.current != null) {
                return true;
            }
            this.current = this.advance();
            return this.current != null;
        }

        public int getCurrentDirection() {
            return this.currentDirection;
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
            if (this.currentNode == this.root && this.currentDirection == 0) {
                this.currentDirection = 1;
                this.currentNode = this.blossomFormingEdge.head[1];
                if (this.currentNode == this.root) {
                    this.currentNode = null;
                }
            } else {
                this.currentNode = this.currentNode.getTreeParent() == this.root && this.currentDirection == 1 ? null : this.currentNode.getTreeParent();
            }
            return this.currentNode;
        }
    }
}

