/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.MergeableAddressableHeap
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.matching.blossom.v5;

import org.jgrapht.alg.matching.blossom.v5.BlossomVEdge;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTree;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.tree.PairingHeap;

class BlossomVTreeEdge {
    BlossomVTree[] head = new BlossomVTree[2];
    BlossomVTreeEdge[] prev = new BlossomVTreeEdge[2];
    BlossomVTreeEdge[] next = new BlossomVTreeEdge[2];
    MergeableAddressableHeap<Double, BlossomVEdge> plusPlusEdges = new PairingHeap();
    MergeableAddressableHeap<Double, BlossomVEdge> plusMinusEdges0 = new PairingHeap();
    MergeableAddressableHeap<Double, BlossomVEdge> plusMinusEdges1 = new PairingHeap();

    public void removeFromTreeEdgeList() {
        for (int dir = 0; dir < 2; ++dir) {
            if (this.prev[dir] != null) {
                this.prev[dir].next[dir] = this.next[dir];
            } else {
                this.head[1 - dir].first[dir] = this.next[dir];
            }
            if (this.next[dir] == null) continue;
            this.next[dir].prev[dir] = this.prev[dir];
        }
        this.head[1] = null;
        this.head[0] = null;
    }

    public String toString() {
        return "BlossomVTreeEdge (" + this.head[0].id + ":" + this.head[1].id + ")";
    }

    public void addToCurrentMinusPlusHeap(BlossomVEdge edge, int direction) {
        edge.handle = this.getCurrentMinusPlusHeap(direction).insert((Object)edge.slack, (Object)edge);
    }

    public void addToCurrentPlusMinusHeap(BlossomVEdge edge, int direction) {
        edge.handle = this.getCurrentPlusMinusHeap(direction).insert((Object)edge.slack, (Object)edge);
    }

    public void addPlusPlusEdge(BlossomVEdge edge) {
        edge.handle = this.plusPlusEdges.insert((Object)edge.slack, (Object)edge);
    }

    public void removeFromCurrentMinusPlusHeap(BlossomVEdge edge) {
        edge.handle.delete();
        edge.handle = null;
    }

    public void removeFromCurrentPlusMinusHeap(BlossomVEdge edge) {
        edge.handle.delete();
        edge.handle = null;
    }

    public void removeFromPlusPlusHeap(BlossomVEdge edge) {
        edge.handle.delete();
        edge.handle = null;
    }

    public MergeableAddressableHeap<Double, BlossomVEdge> getCurrentMinusPlusHeap(int currentDir) {
        return currentDir == 0 ? this.plusMinusEdges0 : this.plusMinusEdges1;
    }

    public MergeableAddressableHeap<Double, BlossomVEdge> getCurrentPlusMinusHeap(int currentDir) {
        return currentDir == 0 ? this.plusMinusEdges1 : this.plusMinusEdges0;
    }
}

