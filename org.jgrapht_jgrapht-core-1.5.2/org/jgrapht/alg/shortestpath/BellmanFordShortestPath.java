/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.NegativeCycleDetectedException;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.graph.GraphWalk;

public class BellmanFordShortestPath<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    protected final Comparator<Double> comparator;
    protected final int maxHops;

    public BellmanFordShortestPath(Graph<V, E> graph) {
        this(graph, 1.0E-9);
    }

    public BellmanFordShortestPath(Graph<V, E> graph, double epsilon) {
        this(graph, 1.0E-9, Integer.MAX_VALUE);
    }

    public BellmanFordShortestPath(Graph<V, E> graph, double epsilon, int maxHops) {
        super(graph);
        this.comparator = new ToleranceDoubleComparator(epsilon);
        if (maxHops < 1) {
            throw new IllegalArgumentException("Number of hops must be positive");
        }
        this.maxHops = maxHops;
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        return this.getPaths(source).getPath(sink);
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        int n = this.graph.vertexSet().size();
        HashMap<Object, Double> distance = new HashMap<Object, Double>();
        HashMap pred = new HashMap();
        for (Object v : this.graph.vertexSet()) {
            distance.put(v, Double.POSITIVE_INFINITY);
        }
        distance.put(source, 0.0);
        Set[] updated = (Set[])Array.newInstance(Set.class, 2);
        updated[0] = new LinkedHashSet();
        updated[1] = new LinkedHashSet();
        int curUpdated = 0;
        updated[curUpdated].add(source);
        for (int i = 0; i < Math.min(n - 1, this.maxHops); ++i) {
            Set curVertexSet = updated[curUpdated];
            Set nextVertexSet = updated[(curUpdated + 1) % 2];
            for (Object v : curVertexSet) {
                for (Object e : this.graph.outgoingEdgesOf(v)) {
                    Object u = Graphs.getOppositeVertex(this.graph, e, v);
                    double newDist = (Double)distance.get(v) + this.graph.getEdgeWeight(e);
                    if (this.comparator.compare(newDist, (Double)distance.get(u)) >= 0) continue;
                    distance.put(u, newDist);
                    pred.put(u, e);
                    nextVertexSet.add(u);
                }
            }
            curVertexSet.clear();
            curUpdated = (curUpdated + 1) % 2;
            if (nextVertexSet.isEmpty()) break;
        }
        if (this.maxHops >= n) {
            for (Object v : updated[curUpdated]) {
                for (Object e : this.graph.outgoingEdgesOf(v)) {
                    Object u = Graphs.getOppositeVertex(this.graph, e, v);
                    double newDist = (Double)distance.get(v) + this.graph.getEdgeWeight(e);
                    if (this.comparator.compare(newDist, (Double)distance.get(u)) >= 0) continue;
                    pred.put(u, e);
                    throw new NegativeCycleDetectedException("Graph contains a negative-weight cycle", this.computeNegativeCycle(e, pred));
                }
            }
        }
        HashMap distanceAndPredecessorMap = new HashMap();
        for (Object v : this.graph.vertexSet()) {
            distanceAndPredecessorMap.put(v, Pair.of((Double)distance.get(v), pred.get(v)));
        }
        return new TreeSingleSourcePathsImpl(this.graph, source, distanceAndPredecessorMap);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink) {
        return new BellmanFordShortestPath<V, E>(graph).getPath(source, sink);
    }

    private GraphPath<V, E> computeNegativeCycle(E edge, Map<V, E> pred) {
        E e;
        HashSet visited = new HashSet();
        Object start = this.graph.getEdgeTarget(edge);
        visited.add(start);
        Object cur = Graphs.getOppositeVertex(this.graph, edge, start);
        while (!visited.contains(cur)) {
            visited.add(cur);
            E e2 = pred.get(cur);
            cur = Graphs.getOppositeVertex(this.graph, e2, cur);
        }
        ArrayList<E> cycle = new ArrayList<E>();
        double weight = 0.0;
        start = cur;
        do {
            e = pred.get(cur);
            cycle.add(e);
            weight += this.graph.getEdgeWeight(e);
        } while ((cur = Graphs.getOppositeVertex(this.graph, e, cur)) != start);
        Collections.reverse(cycle);
        return new GraphWalk(this.graph, start, start, cycle, weight);
    }
}

