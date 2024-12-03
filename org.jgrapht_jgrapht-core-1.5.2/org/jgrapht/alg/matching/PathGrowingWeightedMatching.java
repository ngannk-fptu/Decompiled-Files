/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

public class PathGrowingWeightedMatching<V, E>
implements MatchingAlgorithm<V, E> {
    public static final boolean DEFAULT_USE_HEURISTICS = true;
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;
    private final boolean useHeuristics;

    public PathGrowingWeightedMatching(Graph<V, E> graph) {
        this(graph, true, 1.0E-9);
    }

    public PathGrowingWeightedMatching(Graph<V, E> graph, boolean useHeuristics) {
        this(graph, useHeuristics, 1.0E-9);
    }

    public PathGrowingWeightedMatching(Graph<V, E> graph, boolean useHeuristics, double epsilon) {
        if (graph == null) {
            throw new IllegalArgumentException("Input graph cannot be null");
        }
        this.graph = graph;
        this.comparator = new ToleranceDoubleComparator(epsilon);
        this.useHeuristics = useHeuristics;
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        if (this.useHeuristics) {
            return this.runWithHeuristics();
        }
        return this.run();
    }

    private Set<V> initVisibleVertices() {
        HashSet<V> visibleVertex = new HashSet<V>();
        for (E e : this.graph.edgeSet()) {
            V t;
            V s = this.graph.getEdgeSource(e);
            if (s.equals(t = this.graph.getEdgeTarget(e))) continue;
            visibleVertex.add(s);
            visibleVertex.add(t);
        }
        return visibleVertex;
    }

    private MatchingAlgorithm.Matching<V, E> run() {
        Set<V> visibleVertex = this.initVisibleVertices();
        HashSet<Object> m1 = new HashSet<Object>();
        HashSet<Object> m2 = new HashSet<Object>();
        double m1Weight = 0.0;
        double m2Weight = 0.0;
        int i = 1;
        while (!visibleVertex.isEmpty()) {
            Object x = visibleVertex.stream().findAny().get();
            while (x != null) {
                double maxWeight = 0.0;
                Object maxWeightedEdge = null;
                Object maxWeightedNeighbor = null;
                for (E e : this.graph.edgesOf(x)) {
                    double curWeight;
                    V other = Graphs.getOppositeVertex(this.graph, e, x);
                    if (!visibleVertex.contains(other) || other.equals(x) || this.comparator.compare(curWeight = this.graph.getEdgeWeight(e), 0.0) <= 0 || maxWeightedEdge != null && this.comparator.compare(curWeight, maxWeight) <= 0) continue;
                    maxWeight = curWeight;
                    maxWeightedEdge = e;
                    maxWeightedNeighbor = other;
                }
                if (maxWeightedEdge != null) {
                    switch (i) {
                        case 1: {
                            m1.add(maxWeightedEdge);
                            m1Weight += maxWeight;
                            break;
                        }
                        case 2: {
                            m2.add(maxWeightedEdge);
                            m2Weight += maxWeight;
                            break;
                        }
                        default: {
                            throw new RuntimeException("Failed to figure out matching, seems to be a bug");
                        }
                    }
                    i = 3 - i;
                }
                visibleVertex.remove(x);
                x = maxWeightedNeighbor;
            }
        }
        if (this.comparator.compare(m1Weight, m2Weight) > 0) {
            return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, m1, m1Weight);
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, m2, m2Weight);
    }

    private MatchingAlgorithm.Matching<V, E> runWithHeuristics() {
        Set<V> visibleVertex = this.initVisibleVertices();
        DynamicProgrammingPathSolver pathSolver = new DynamicProgrammingPathSolver();
        HashSet<E> matching = new HashSet<E>();
        double matchingWeight = 0.0;
        HashSet<V> matchedVertices = new HashSet<V>();
        while (!visibleVertex.isEmpty()) {
            Object x = visibleVertex.stream().findAny().get();
            LinkedList<Object> path = new LinkedList<Object>();
            while (x != null) {
                double maxWeight = 0.0;
                Object maxWeightedEdge = null;
                Object maxWeightedNeighbor = null;
                for (E e : this.graph.edgesOf(x)) {
                    double curWeight;
                    V other = Graphs.getOppositeVertex(this.graph, e, x);
                    if (!visibleVertex.contains(other) || other.equals(x) || this.comparator.compare(curWeight = this.graph.getEdgeWeight(e), 0.0) <= 0 || maxWeightedEdge != null && this.comparator.compare(curWeight, maxWeight) <= 0) continue;
                    maxWeight = curWeight;
                    maxWeightedEdge = e;
                    maxWeightedNeighbor = other;
                }
                if (maxWeightedEdge != null) {
                    path.add(maxWeightedEdge);
                }
                visibleVertex.remove(x);
                x = maxWeightedNeighbor;
            }
            Pair pathMatching = pathSolver.getMaximumWeightMatching(this.graph, path);
            matchingWeight += pathMatching.getFirst().doubleValue();
            for (Object e : pathMatching.getSecond()) {
                V s = this.graph.getEdgeSource(e);
                V t = this.graph.getEdgeTarget(e);
                if (!matchedVertices.add(s)) {
                    throw new RuntimeException("Set is not a valid matching, please submit a bug report");
                }
                if (!matchedVertices.add(t)) {
                    throw new RuntimeException("Set is not a valid matching, please submit a bug report");
                }
                matching.add(e);
            }
        }
        for (E e : this.graph.edgeSet()) {
            V t;
            V s;
            double edgeWeight = this.graph.getEdgeWeight(e);
            if (this.comparator.compare(edgeWeight, 0.0) <= 0 || matchedVertices.contains(s = this.graph.getEdgeSource(e)) || matchedVertices.contains(t = this.graph.getEdgeTarget(e))) continue;
            matching.add(e);
            matchingWeight += edgeWeight;
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, matching, matchingWeight);
    }

    class DynamicProgrammingPathSolver {
        private static final int WORK_ARRAY_INITIAL_SIZE = 256;
        private double[] a = new double[256];

        DynamicProgrammingPathSolver() {
        }

        public Pair<Double, Set<E>> getMaximumWeightMatching(Graph<V, E> g, LinkedList<E> path) {
            int pathLength = path.size();
            switch (pathLength) {
                case 0: {
                    return Pair.of(0.0, Collections.emptySet());
                }
                case 1: {
                    Object e = path.getFirst();
                    double eWeight = g.getEdgeWeight(e);
                    if (PathGrowingWeightedMatching.this.comparator.compare(eWeight, 0.0) > 0) {
                        return Pair.of(eWeight, Collections.singleton(e));
                    }
                    return Pair.of(0.0, Collections.emptySet());
                }
            }
            if (this.a.length < pathLength + 1) {
                this.a = new double[pathLength + 1];
            }
            Iterator it = path.iterator();
            Object e = it.next();
            double eWeight = g.getEdgeWeight(e);
            this.a[0] = 0.0;
            this.a[1] = PathGrowingWeightedMatching.this.comparator.compare(eWeight, 0.0) > 0 ? eWeight : 0.0;
            for (int i = 2; i <= pathLength; ++i) {
                e = it.next();
                eWeight = g.getEdgeWeight(e);
                this.a[i] = PathGrowingWeightedMatching.this.comparator.compare(this.a[i - 1], this.a[i - 2] + eWeight) > 0 ? this.a[i - 1] : this.a[i - 2] + eWeight;
            }
            HashSet matching = new HashSet();
            it = path.descendingIterator();
            for (int i = pathLength; i >= 1; --i) {
                e = it.next();
                if (PathGrowingWeightedMatching.this.comparator.compare(this.a[i], this.a[i - 1]) <= 0) continue;
                matching.add(e);
                if (i > 1) {
                    e = it.next();
                }
                --i;
            }
            return Pair.of(this.a[pathLength], matching);
        }
    }
}

