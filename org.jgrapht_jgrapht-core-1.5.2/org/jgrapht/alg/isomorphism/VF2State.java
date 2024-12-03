/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Arrays;
import java.util.Comparator;
import org.jgrapht.alg.isomorphism.GraphOrdering;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;

abstract class VF2State<V, E> {
    public static final int NULL_NODE = -1;
    protected static final boolean DEBUG = false;
    protected final int[] core1;
    protected final int[] core2;
    protected final int[] in1;
    protected final int[] in2;
    protected final int[] out1;
    protected final int[] out2;
    protected final int n1;
    protected final int n2;
    protected int coreLen;
    protected int t1BothLen;
    protected int t2BothLen;
    protected int t1InLen;
    protected int t2InLen;
    protected int t1OutLen;
    protected int t2OutLen;
    protected int addedVertex1;
    protected int addVertex1;
    protected int addVertex2;
    protected final GraphOrdering<V, E> g1;
    protected final GraphOrdering<V, E> g2;
    protected final Comparator<V> vertexComparator;
    protected final Comparator<E> edgeComparator;

    public VF2State(GraphOrdering<V, E> g1, GraphOrdering<V, E> g2, Comparator<V> vertexComparator, Comparator<E> edgeComparator) {
        this.g1 = g1;
        this.g2 = g2;
        this.vertexComparator = vertexComparator;
        this.edgeComparator = edgeComparator;
        this.n1 = g1.getVertexCount();
        this.n2 = g2.getVertexCount();
        this.core1 = new int[this.n1];
        this.in1 = new int[this.n1];
        this.out1 = new int[this.n1];
        this.core2 = new int[this.n2];
        this.in2 = new int[this.n2];
        this.out2 = new int[this.n2];
        Arrays.fill(this.core1, -1);
        Arrays.fill(this.core2, -1);
        this.coreLen = 0;
        this.addVertex2 = -1;
        this.addVertex1 = -1;
        this.addedVertex1 = -1;
        this.t2OutLen = 0;
        this.t1OutLen = 0;
        this.t2InLen = 0;
        this.t1InLen = 0;
        this.t2BothLen = 0;
        this.t1BothLen = 0;
    }

    public VF2State(VF2State<V, E> s) {
        this.g1 = s.g1;
        this.g2 = s.g2;
        this.core1 = s.core1;
        this.core2 = s.core2;
        this.in1 = s.in1;
        this.in2 = s.in2;
        this.out1 = s.out1;
        this.out2 = s.out2;
        this.coreLen = s.coreLen;
        this.n1 = s.n1;
        this.n2 = s.n2;
        this.t1BothLen = s.t1BothLen;
        this.t2BothLen = s.t2BothLen;
        this.t1InLen = s.t1InLen;
        this.t2InLen = s.t2InLen;
        this.t1OutLen = s.t1OutLen;
        this.t2OutLen = s.t2OutLen;
        this.vertexComparator = s.vertexComparator;
        this.edgeComparator = s.edgeComparator;
        this.addVertex1 = s.addVertex1;
        this.addVertex2 = s.addVertex2;
        this.addedVertex1 = s.addedVertex1;
    }

    public boolean nextPair() {
        if (this.addVertex2 == -1) {
            this.addVertex2 = 0;
        }
        this.addVertex1 = this.addVertex1 == -1 ? 0 : ++this.addVertex1;
        if (this.t1BothLen > this.coreLen && this.t2BothLen > this.coreLen) {
            while (this.addVertex2 < this.n2 && (this.core2[this.addVertex2] != -1 || this.out2[this.addVertex2] == 0 || this.in2[this.addVertex2] == 0)) {
                ++this.addVertex2;
                this.addVertex1 = 0;
            }
            while (this.addVertex1 < this.n1 && (this.core1[this.addVertex1] != -1 || this.out1[this.addVertex1] == 0 || this.in1[this.addVertex1] == 0)) {
                ++this.addVertex1;
            }
        } else if (this.t1OutLen > this.coreLen && this.t2OutLen > this.coreLen) {
            while (this.addVertex2 < this.n2 && (this.core2[this.addVertex2] != -1 || this.out2[this.addVertex2] == 0)) {
                ++this.addVertex2;
                this.addVertex1 = 0;
            }
            while (this.addVertex1 < this.n1 && (this.core1[this.addVertex1] != -1 || this.out1[this.addVertex1] == 0)) {
                ++this.addVertex1;
            }
        } else if (this.t1InLen > this.coreLen && this.t2InLen > this.coreLen) {
            while (this.addVertex2 < this.n2 && (this.core2[this.addVertex2] != -1 || this.in2[this.addVertex2] == 0)) {
                ++this.addVertex2;
                this.addVertex1 = 0;
            }
            while (this.addVertex1 < this.n1 && (this.core1[this.addVertex1] != -1 || this.in1[this.addVertex1] == 0)) {
                ++this.addVertex1;
            }
        } else {
            while (this.addVertex2 < this.n2 && this.core2[this.addVertex2] != -1) {
                ++this.addVertex2;
                this.addVertex1 = 0;
            }
            while (this.addVertex1 < this.n1 && this.core1[this.addVertex1] != -1) {
                ++this.addVertex1;
            }
        }
        if (this.addVertex1 < this.n1 && this.addVertex2 < this.n2) {
            return true;
        }
        this.addVertex2 = -1;
        this.addVertex1 = -1;
        return false;
    }

    public void addPair() {
        ++this.coreLen;
        this.addedVertex1 = this.addVertex1;
        if (this.in1[this.addVertex1] == 0) {
            this.in1[this.addVertex1] = this.coreLen;
            ++this.t1InLen;
            if (this.out1[this.addVertex1] > 0) {
                ++this.t1BothLen;
            }
        }
        if (this.out1[this.addVertex1] == 0) {
            this.out1[this.addVertex1] = this.coreLen;
            ++this.t1OutLen;
            if (this.in1[this.addVertex1] > 0) {
                ++this.t1BothLen;
            }
        }
        if (this.in2[this.addVertex2] == 0) {
            this.in2[this.addVertex2] = this.coreLen;
            ++this.t2InLen;
            if (this.out2[this.addVertex2] > 0) {
                ++this.t2BothLen;
            }
        }
        if (this.out2[this.addVertex2] == 0) {
            this.out2[this.addVertex2] = this.coreLen;
            ++this.t2OutLen;
            if (this.in2[this.addVertex2] > 0) {
                ++this.t2BothLen;
            }
        }
        this.core1[this.addVertex1] = this.addVertex2;
        this.core2[this.addVertex2] = this.addVertex1;
        for (int other : this.g1.getInEdges(this.addVertex1)) {
            if (this.in1[other] != 0) continue;
            this.in1[other] = this.coreLen;
            ++this.t1InLen;
            if (this.out1[other] <= 0) continue;
            ++this.t1BothLen;
        }
        for (int other : this.g1.getOutEdges(this.addVertex1)) {
            if (this.out1[other] != 0) continue;
            this.out1[other] = this.coreLen;
            ++this.t1OutLen;
            if (this.in1[other] <= 0) continue;
            ++this.t1BothLen;
        }
        for (int other : this.g2.getInEdges(this.addVertex2)) {
            if (this.in2[other] != 0) continue;
            this.in2[other] = this.coreLen;
            ++this.t2InLen;
            if (this.out2[other] <= 0) continue;
            ++this.t2BothLen;
        }
        for (int other : this.g2.getOutEdges(this.addVertex2)) {
            if (this.out2[other] != 0) continue;
            this.out2[other] = this.coreLen;
            ++this.t2OutLen;
            if (this.in2[other] <= 0) continue;
            ++this.t2BothLen;
        }
    }

    public boolean isGoal() {
        return this.coreLen == this.n2;
    }

    public abstract boolean isFeasiblePair();

    public void backtrack() {
        int addedVertex2 = this.core1[this.addedVertex1];
        if (this.in1[this.addedVertex1] == this.coreLen) {
            this.in1[this.addedVertex1] = 0;
        }
        for (int other : this.g1.getInEdges(this.addedVertex1)) {
            if (this.in1[other] != this.coreLen) continue;
            this.in1[other] = 0;
        }
        if (this.out1[this.addedVertex1] == this.coreLen) {
            this.out1[this.addedVertex1] = 0;
        }
        for (int other : this.g1.getOutEdges(this.addedVertex1)) {
            if (this.out1[other] != this.coreLen) continue;
            this.out1[other] = 0;
        }
        if (this.in2[addedVertex2] == this.coreLen) {
            this.in2[addedVertex2] = 0;
        }
        for (int other : this.g2.getInEdges(addedVertex2)) {
            if (this.in2[other] != this.coreLen) continue;
            this.in2[other] = 0;
        }
        if (this.out2[addedVertex2] == this.coreLen) {
            this.out2[addedVertex2] = 0;
        }
        for (int other : this.g2.getOutEdges(addedVertex2)) {
            if (this.out2[other] != this.coreLen) continue;
            this.out2[other] = 0;
        }
        this.core2[addedVertex2] = -1;
        this.core1[this.addedVertex1] = -1;
        --this.coreLen;
        this.addedVertex1 = -1;
    }

    protected boolean areCompatibleVertexes(int v1, int v2) {
        return this.vertexComparator == null || this.vertexComparator.compare(this.g1.getVertex(v1), this.g2.getVertex(v2)) == 0;
    }

    protected boolean areCompatibleEdges(int v1, int v2, int u1, int u2) {
        return this.edgeComparator == null || this.edgeComparator.compare(this.g1.getEdge(v1, v2), this.g2.getEdge(u1, u2)) == 0;
    }

    public IsomorphicGraphMapping<V, E> getCurrentMapping() {
        return new IsomorphicGraphMapping<V, E>(this.g1, this.g2, this.core1, this.core2);
    }

    public void resetAddVertexes() {
        this.addVertex2 = -1;
        this.addVertex1 = -1;
    }

    protected void showLog(String method, String str) {
    }
}

