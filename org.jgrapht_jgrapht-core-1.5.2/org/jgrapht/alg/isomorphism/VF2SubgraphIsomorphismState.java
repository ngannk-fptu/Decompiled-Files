/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Comparator;
import org.jgrapht.alg.isomorphism.GraphOrdering;
import org.jgrapht.alg.isomorphism.VF2State;

class VF2SubgraphIsomorphismState<V, E>
extends VF2State<V, E> {
    public VF2SubgraphIsomorphismState(GraphOrdering<V, E> g1, GraphOrdering<V, E> g2, Comparator<V> vertexComparator, Comparator<E> edgeComparator) {
        super(g1, g2, vertexComparator, edgeComparator);
    }

    public VF2SubgraphIsomorphismState(VF2State<V, E> s) {
        super(s);
    }

    @Override
    public boolean isFeasiblePair() {
        int other2;
        int other1;
        int other22;
        Object pairstr = null;
        Object abortmsg = null;
        if (!this.areCompatibleVertexes(this.addVertex1, this.addVertex2)) {
            return false;
        }
        int termOutPred1 = 0;
        int termOutPred2 = 0;
        int termInPred1 = 0;
        int termInPred2 = 0;
        int newPred1 = 0;
        int newPred2 = 0;
        int termOutSucc1 = 0;
        int termOutSucc2 = 0;
        int termInSucc1 = 0;
        int termInSucc2 = 0;
        int newSucc1 = 0;
        int newSucc2 = 0;
        int[] outE1 = this.g1.getOutEdges(this.addVertex1);
        for (int i = 0; i < outE1.length; ++i) {
            int other12 = outE1[i];
            if (this.core1[other12] != -1) {
                other22 = this.core1[other12];
                if (this.g2.hasEdge(this.addVertex2, other22) && this.areCompatibleEdges(this.addVertex1, other12, this.addVertex2, other22)) continue;
                return false;
            }
            int in1O1 = this.in1[other12];
            int out1O1 = this.out1[other12];
            if (in1O1 == 0 && out1O1 == 0) {
                ++newSucc1;
                continue;
            }
            if (in1O1 > 0) {
                ++termInSucc1;
            }
            if (out1O1 <= 0) continue;
            ++termOutSucc1;
        }
        int[] outE2 = this.g2.getOutEdges(this.addVertex2);
        for (int i = 0; i < outE2.length; ++i) {
            other22 = outE2[i];
            if (this.core2[other22] != -1) {
                other1 = this.core2[other22];
                if (this.g1.hasEdge(this.addVertex1, other1)) continue;
                return false;
            }
            int in2O2 = this.in2[other22];
            int out2O2 = this.out2[other22];
            if (in2O2 == 0 && out2O2 == 0) {
                ++newSucc2;
                continue;
            }
            if (in2O2 > 0) {
                ++termInSucc2;
            }
            if (out2O2 <= 0) continue;
            ++termOutSucc2;
        }
        if (termInSucc1 < termInSucc2 || termOutSucc1 < termOutSucc2 || newSucc1 < newSucc2) {
            return false;
        }
        int[] inE1 = this.g1.getInEdges(this.addVertex1);
        for (int i = 0; i < inE1.length; ++i) {
            other1 = inE1[i];
            if (this.core1[other1] != -1) {
                other2 = this.core1[other1];
                if (this.g2.hasEdge(other2, this.addVertex2) && this.areCompatibleEdges(other1, this.addVertex1, other2, this.addVertex2)) continue;
                return false;
            }
            int in1O1 = this.in1[other1];
            int out1O1 = this.out1[other1];
            if (in1O1 == 0 && out1O1 == 0) {
                ++newPred1;
                continue;
            }
            if (in1O1 > 0) {
                ++termInPred1;
            }
            if (out1O1 <= 0) continue;
            ++termOutPred1;
        }
        int[] inE2 = this.g2.getInEdges(this.addVertex2);
        for (int i = 0; i < inE2.length; ++i) {
            other2 = inE2[i];
            if (this.core2[other2] != -1) {
                int other13 = this.core2[other2];
                if (this.g1.hasEdge(other13, this.addVertex1)) continue;
                return false;
            }
            int in2O2 = this.in2[other2];
            int out2O2 = this.out2[other2];
            if (in2O2 == 0 && out2O2 == 0) {
                ++newPred2;
                continue;
            }
            if (in2O2 > 0) {
                ++termInPred2;
            }
            if (out2O2 <= 0) continue;
            ++termOutPred2;
        }
        return termInPred1 >= termInPred2 && termOutPred1 >= termOutPred2 && newPred1 >= newPred2;
    }
}

