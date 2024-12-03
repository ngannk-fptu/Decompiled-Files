/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Comparator;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.GraphOrdering;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.VF2MappingIterator;
import org.jgrapht.alg.isomorphism.VF2State;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismState;

class VF2SubgraphMappingIterator<V, E>
extends VF2MappingIterator<V, E> {
    public VF2SubgraphMappingIterator(GraphOrdering<V, E> ordering1, GraphOrdering<V, E> ordering2, Comparator<V> vertexComparator, Comparator<E> edgeComparator) {
        super(ordering1, ordering2, vertexComparator, edgeComparator);
    }

    @Override
    protected IsomorphicGraphMapping<V, E> match() {
        VF2SubgraphIsomorphismState s;
        if (this.stateStack.isEmpty()) {
            Graph g1 = this.ordering1.getGraph();
            Graph g2 = this.ordering2.getGraph();
            if (g1.vertexSet().size() < g2.vertexSet().size() || g1.edgeSet().size() < g2.edgeSet().size()) {
                return null;
            }
            s = new VF2SubgraphIsomorphismState(this.ordering1, this.ordering2, this.vertexComparator, this.edgeComparator);
            if (g2.vertexSet().isEmpty()) {
                return this.hadOneMapping != null ? null : s.getCurrentMapping();
            }
        } else {
            ((VF2State)this.stateStack.pop()).backtrack();
            s = (VF2SubgraphIsomorphismState)this.stateStack.pop();
        }
        while (true) {
            if (s.nextPair()) {
                if (!((VF2State)s).isFeasiblePair()) continue;
                this.stateStack.push(s);
                s = new VF2SubgraphIsomorphismState(s);
                s.addPair();
                if (s.isGoal()) {
                    this.stateStack.push(s);
                    return s.getCurrentMapping();
                }
                s.resetAddVertexes();
                continue;
            }
            if (this.stateStack.isEmpty()) {
                return null;
            }
            s.backtrack();
            s = (VF2State)this.stateStack.pop();
        }
    }
}

