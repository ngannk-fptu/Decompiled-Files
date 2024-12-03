/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayDeque;
import java.util.HashMap;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;

public class BFSShortestPath<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    public BFSShortestPath(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        HashMap<Object, Pair<Double, Object>> distanceAndPredecessorMap = new HashMap<Object, Pair<Double, Object>>();
        distanceAndPredecessorMap.put(source, Pair.of(0.0, null));
        ArrayDeque<Object> queue = new ArrayDeque<Object>();
        queue.add(source);
        while (!queue.isEmpty()) {
            Object v = queue.poll();
            for (Object e : this.graph.outgoingEdgesOf(v)) {
                Object u = Graphs.getOppositeVertex(this.graph, e, v);
                if (distanceAndPredecessorMap.containsKey(u)) continue;
                queue.add(u);
                double newDist = (Double)((Pair)distanceAndPredecessorMap.get(v)).getFirst() + 1.0;
                distanceAndPredecessorMap.put(u, Pair.of(newDist, e));
            }
        }
        return new TreeSingleSourcePathsImpl(this.graph, source, distanceAndPredecessorMap);
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        return this.getPaths(source).getPath(sink);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink) {
        return new BFSShortestPath<V, E>(graph).getPath(source, sink);
    }
}

