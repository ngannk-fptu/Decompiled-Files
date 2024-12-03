/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.util.CollectionUtil;

public class TwoApproxMetricTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.checkGraph(graph);
        Set<V> vertices = graph.vertexSet();
        int n = vertices.size();
        if (vertices.size() == 1) {
            return this.getSingletonTour(graph);
        }
        SimpleGraph<V, DefaultEdge> mst = new SimpleGraph<V, DefaultEdge>(null, DefaultEdge::new, false);
        vertices.forEach(mst::addVertex);
        for (E e : new KruskalMinimumSpanningTree<V, E>(graph).getSpanningTree().getEdges()) {
            mst.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
        }
        HashSet found = CollectionUtil.newHashSetWithExpectedSize(n);
        ArrayList tour = new ArrayList(n + 1);
        V start = vertices.iterator().next();
        DepthFirstIterator dfsIt = new DepthFirstIterator(mst, start);
        while (dfsIt.hasNext()) {
            Object v = dfsIt.next();
            if (!found.add(v)) continue;
            tour.add(v);
        }
        return this.vertexListToTour(tour, graph);
    }
}

