/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.independentset;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.interfaces.IndependentSetAlgorithm;

public class ChordalGraphIndependentSetFinder<V, E>
implements IndependentSetAlgorithm<V> {
    private final Graph<V, E> graph;
    private final ChordalityInspector<V, E> chordalityInspector;
    private IndependentSetAlgorithm.IndependentSet<V> maximumIndependentSet;

    public ChordalGraphIndependentSetFinder(Graph<V, E> graph) {
        this(graph, ChordalityInspector.IterationOrder.MCS);
    }

    public ChordalGraphIndependentSetFinder(Graph<V, E> graph, ChordalityInspector.IterationOrder iterationOrder) {
        this.graph = Objects.requireNonNull(graph);
        this.chordalityInspector = new ChordalityInspector<V, E>(graph, iterationOrder);
    }

    private void lazyComputeMaximumIndependentSet() {
        if (this.maximumIndependentSet == null && this.chordalityInspector.isChordal()) {
            HashSet<V> restricted = new HashSet<V>();
            HashSet<V> is = new HashSet<V>();
            List<V> perfectEliminationOrder = this.chordalityInspector.getPerfectEliminationOrder();
            ListIterator<V> reverse = perfectEliminationOrder.listIterator(perfectEliminationOrder.size());
            while (reverse.hasPrevious()) {
                V previous = reverse.previous();
                if (restricted.contains(previous)) continue;
                is.add(previous);
                for (E edge : this.graph.edgesOf(previous)) {
                    V opposite = Graphs.getOppositeVertex(this.graph, edge, previous);
                    if (previous.equals(opposite)) continue;
                    restricted.add(opposite);
                }
            }
            this.maximumIndependentSet = new IndependentSetAlgorithm.IndependentSetImpl(is);
        }
    }

    @Override
    public IndependentSetAlgorithm.IndependentSet<V> getIndependentSet() {
        this.lazyComputeMaximumIndependentSet();
        return this.maximumIndependentSet;
    }
}

