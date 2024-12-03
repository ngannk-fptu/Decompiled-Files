/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap$Handle
 */
package org.jgrapht.alg.matching.blossom.v5;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jgrapht.alg.matching.blossom.v5.BlossomVEdge;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTree;
import org.jheaps.AddressableHeap;

class BlossomVNode {
    AddressableHeap.Handle<Double, BlossomVNode> handle;
    boolean isTreeRoot;
    boolean isBlossom;
    boolean isOuter;
    boolean isProcessed;
    boolean isMarked;
    Label label;
    BlossomVEdge[] first = new BlossomVEdge[2];
    double dual;
    BlossomVEdge matched;
    BlossomVEdge bestEdge;
    BlossomVTree tree;
    BlossomVEdge parentEdge;
    BlossomVNode firstTreeChild;
    BlossomVNode treeSiblingNext;
    BlossomVNode treeSiblingPrev;
    BlossomVNode blossomParent;
    BlossomVNode blossomGrandparent;
    BlossomVEdge blossomSibling;
    int pos;

    public BlossomVNode(int pos) {
        this.label = Label.PLUS;
        this.pos = pos;
    }

    public void addEdge(BlossomVEdge edge, int dir) {
        if (this.first[dir] == null) {
            edge.next[dir] = edge.prev[dir] = edge;
            this.first[dir] = edge.prev[dir];
        } else {
            edge.prev[dir] = this.first[dir].prev[dir];
            edge.next[dir] = this.first[dir];
            this.first[dir].prev[dir].next[dir] = edge;
            this.first[dir].prev[dir] = edge;
        }
        edge.head[1 - dir] = this;
    }

    public void removeEdge(BlossomVEdge edge, int dir) {
        if (edge.prev[dir] == edge) {
            this.first[dir] = null;
        } else {
            edge.prev[dir].next[dir] = edge.next[dir];
            edge.next[dir].prev[dir] = edge.prev[dir];
            if (this.first[dir] == edge) {
                this.first[dir] = edge.next[dir];
            }
        }
    }

    public BlossomVNode getTreeGrandparent() {
        BlossomVNode t = this.parentEdge.getOpposite(this);
        return t.parentEdge.getOpposite(t);
    }

    public BlossomVNode getTreeParent() {
        return this.parentEdge == null ? null : this.parentEdge.getOpposite(this);
    }

    public void addChild(BlossomVNode child, BlossomVEdge parentEdge, boolean grow) {
        child.parentEdge = parentEdge;
        child.tree = this.tree;
        child.treeSiblingNext = this.firstTreeChild;
        if (grow) {
            child.firstTreeChild = null;
        }
        if (this.firstTreeChild == null) {
            child.treeSiblingPrev = child;
        } else {
            child.treeSiblingPrev = this.firstTreeChild.treeSiblingPrev;
            this.firstTreeChild.treeSiblingPrev = child;
        }
        this.firstTreeChild = child;
    }

    public BlossomVNode getOppositeMatched() {
        return this.matched.getOpposite(this);
    }

    public void removeFromChildList() {
        if (this.isTreeRoot) {
            this.treeSiblingPrev.treeSiblingNext = this.treeSiblingNext;
            if (this.treeSiblingNext != null) {
                this.treeSiblingNext.treeSiblingPrev = this.treeSiblingPrev;
            }
        } else {
            if (this.treeSiblingPrev.treeSiblingNext == null) {
                this.parentEdge.getOpposite((BlossomVNode)this).firstTreeChild = this.treeSiblingNext;
            } else {
                this.treeSiblingPrev.treeSiblingNext = this.treeSiblingNext;
            }
            if (this.treeSiblingNext == null) {
                if (this.parentEdge.getOpposite((BlossomVNode)this).firstTreeChild != null) {
                    this.parentEdge.getOpposite((BlossomVNode)this).firstTreeChild.treeSiblingPrev = this.treeSiblingPrev;
                }
            } else {
                this.treeSiblingNext.treeSiblingPrev = this.treeSiblingPrev;
            }
        }
    }

    public void moveChildrenTo(BlossomVNode blossom) {
        if (this.firstTreeChild != null) {
            if (blossom.firstTreeChild == null) {
                blossom.firstTreeChild = this.firstTreeChild;
            } else {
                BlossomVNode t = blossom.firstTreeChild.treeSiblingPrev;
                this.firstTreeChild.treeSiblingPrev.treeSiblingNext = blossom.firstTreeChild;
                blossom.firstTreeChild.treeSiblingPrev = this.firstTreeChild.treeSiblingPrev;
                this.firstTreeChild.treeSiblingPrev = t;
                blossom.firstTreeChild = this.firstTreeChild;
            }
            this.firstTreeChild = null;
        }
    }

    public BlossomVNode getPenultimateBlossom() {
        BlossomVNode current = this;
        while (true) {
            if (!current.blossomGrandparent.isOuter) {
                current = current.blossomGrandparent;
                continue;
            }
            if (current.blossomGrandparent == current.blossomParent) break;
            current.blossomGrandparent = current.blossomParent;
        }
        BlossomVNode prev = this;
        while (prev != current) {
            BlossomVNode next = prev.blossomGrandparent;
            prev.blossomGrandparent = current;
            prev = next;
        }
        return current;
    }

    public BlossomVNode getPenultimateBlossomAndFixBlossomGrandparent() {
        BlossomVNode current = this;
        BlossomVNode prev = null;
        while (true) {
            if (!current.blossomGrandparent.isOuter) {
                prev = current;
                current = current.blossomGrandparent;
                continue;
            }
            if (current.blossomGrandparent == current.blossomParent) break;
            current.blossomGrandparent = current.blossomParent;
        }
        if (prev != null) {
            BlossomVNode prevNode = this;
            while (prevNode != prev) {
                BlossomVNode nextNode = prevNode.blossomGrandparent;
                prevNode.blossomGrandparent = prev;
                prevNode = nextNode;
            }
        }
        return current;
    }

    public boolean isPlusNode() {
        return this.label == Label.PLUS;
    }

    public boolean isMinusNode() {
        return this.label == Label.MINUS;
    }

    public boolean isInfinityNode() {
        return this.label == Label.INFINITY;
    }

    public double getTrueDual() {
        if (this.isInfinityNode() || !this.isOuter) {
            return this.dual;
        }
        return this.isPlusNode() ? this.dual + this.tree.eps : this.dual - this.tree.eps;
    }

    public IncidentEdgeIterator incidentEdgesIterator() {
        return new IncidentEdgeIterator();
    }

    public String toString() {
        return "BlossomVNode pos = " + this.pos + ", dual: " + this.dual + ", true dual: " + this.getTrueDual() + ", label: " + this.label + (this.isMarked ? ", marked" : "") + (this.isProcessed ? ", processed" : "") + (String)(this.blossomParent == null || this.isOuter ? "" : ", blossomParent = " + this.blossomParent.pos) + (String)(this.matched == null ? "" : ", matched = " + this.matched);
    }

    public static enum Label {
        PLUS,
        MINUS,
        INFINITY;

    }

    public class IncidentEdgeIterator
    implements Iterator<BlossomVEdge> {
        private int currentDir;
        private int nextDir;
        private BlossomVEdge nextEdge;

        public IncidentEdgeIterator() {
            this.nextDir = BlossomVNode.this.first[0] == null ? 1 : 0;
            this.nextEdge = BlossomVNode.this.first[this.nextDir];
        }

        public int getDir() {
            return this.currentDir;
        }

        @Override
        public boolean hasNext() {
            return this.nextEdge != null;
        }

        @Override
        public BlossomVEdge next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            BlossomVEdge result = this.nextEdge;
            this.advance();
            return result;
        }

        private void advance() {
            this.currentDir = this.nextDir;
            this.nextEdge = this.nextEdge.next[this.nextDir];
            if (this.nextEdge == BlossomVNode.this.first[0]) {
                this.nextEdge = BlossomVNode.this.first[1];
                this.nextDir = 1;
            } else if (this.nextEdge == BlossomVNode.this.first[1]) {
                this.nextEdge = null;
            }
        }
    }
}

