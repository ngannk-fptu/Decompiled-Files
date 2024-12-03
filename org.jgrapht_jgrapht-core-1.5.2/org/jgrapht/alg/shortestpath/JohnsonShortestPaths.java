/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraClosestFirstIterator;
import org.jgrapht.alg.shortestpath.NegativeCycleDetectedException;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.graph.AsGraphUnion;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.TypeUtil;

public class JohnsonShortestPaths<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    private double[][] distance;
    private E[][] pred;
    private Map<V, Integer> vertexIndices;
    private final Comparator<Double> comparator;

    public JohnsonShortestPaths(Graph<V, E> graph) {
        this(graph, 1.0E-9);
    }

    public JohnsonShortestPaths(Graph<V, E> graph, double epsilon) {
        super(graph);
        this.comparator = new ToleranceDoubleComparator(epsilon);
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        this.run();
        if (source.equals(sink)) {
            return GraphWalk.singletonWalk(this.graph, source, 0.0);
        }
        int vSource = this.vertexIndices.get(source);
        int vSink = this.vertexIndices.get(sink);
        V cur = sink;
        E e = this.pred[vSource][vSink];
        if (e == null) {
            return null;
        }
        LinkedList<E> edgeList = new LinkedList<E>();
        while (e != null) {
            edgeList.addFirst(e);
            cur = Graphs.getOppositeVertex(this.graph, e, cur);
            e = this.pred[vSource][this.vertexIndices.get(cur)];
        }
        return new GraphWalk(this.graph, source, sink, null, edgeList, this.distance[vSource][vSink]);
    }

    @Override
    public double getPathWeight(V source, V sink) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        this.run();
        return this.distance[this.vertexIndices.get(source)][this.vertexIndices.get(sink)];
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        this.run();
        return new JohnsonSingleSourcePaths(source);
    }

    private void run() {
        if (this.pred != null) {
            return;
        }
        GraphTests.requireDirectedOrUndirected(this.graph);
        Object detectedNegativeEdge = null;
        for (Object e : this.graph.edgeSet()) {
            if (this.comparator.compare(this.graph.getEdgeWeight(e), 0.0) >= 0) continue;
            detectedNegativeEdge = e;
            break;
        }
        if (detectedNegativeEdge != null) {
            if (this.graph.getType().isUndirected()) {
                Object source = this.graph.getEdgeSource(detectedNegativeEdge);
                double weight = this.graph.getEdgeWeight(detectedNegativeEdge);
                GraphWalk cycle = new GraphWalk(this.graph, source, source, Arrays.asList(detectedNegativeEdge, detectedNegativeEdge), 2.0 * weight);
                throw new NegativeCycleDetectedException("Graph contains a negative-weight cycle", cycle);
            }
            this.runWithNegativeEdgeWeights(this.graph);
        } else {
            this.runWithPositiveEdgeWeights(this.graph);
        }
    }

    private void runWithPositiveEdgeWeights(Graph<V, E> g) {
        this.vertexIndices = this.computeVertexIndices(g);
        int n = g.vertexSet().size();
        this.distance = new double[n][n];
        this.pred = (Object[][])TypeUtil.uncheckedCast(new Object[n][n]);
        for (V v : g.vertexSet()) {
            DijkstraClosestFirstIterator<V, E> it = new DijkstraClosestFirstIterator<V, E>(g, v, Double.POSITIVE_INFINITY);
            while (it.hasNext()) {
                it.next();
            }
            Map<Pair<Double, Object>, Pair<Double, E>> distanceAndPredecessorMap = it.getDistanceAndPredecessorMap();
            for (V u : g.vertexSet()) {
                Pair<Double, Object> pair = distanceAndPredecessorMap.getOrDefault(u, Pair.of(Double.POSITIVE_INFINITY, null));
                this.distance[this.vertexIndices.get(v).intValue()][this.vertexIndices.get(u).intValue()] = pair.getFirst();
                this.pred[this.vertexIndices.get(v).intValue()][this.vertexIndices.get(u).intValue()] = pair.getSecond();
            }
        }
    }

    private void runWithNegativeEdgeWeights(Graph<V, E> g) {
        Map<V, Double> vertexWeights = this.computeVertexWeights(g);
        HashMap<E, Double> newEdgeWeights = new HashMap<E, Double>();
        for (E e : g.edgeSet()) {
            V u = g.getEdgeSource(e);
            V v = g.getEdgeTarget(e);
            double weight = g.getEdgeWeight(e);
            newEdgeWeights.put(e, weight + vertexWeights.get(u) - vertexWeights.get(v));
        }
        AsWeightedGraph<V, E> newEdgeWeightsGraph = new AsWeightedGraph<V, E>(g, newEdgeWeights);
        this.vertexIndices = this.computeVertexIndices(g);
        int n = g.vertexSet().size();
        this.distance = new double[n][n];
        this.pred = (Object[][])TypeUtil.uncheckedCast(new Object[n][n]);
        for (V v : g.vertexSet()) {
            DijkstraClosestFirstIterator<V, E> it = new DijkstraClosestFirstIterator<V, E>(newEdgeWeightsGraph, v, Double.POSITIVE_INFINITY);
            while (it.hasNext()) {
                it.next();
            }
            Map<V, Pair<Double, E>> distanceAndPredecessorMap = it.getDistanceAndPredecessorMap();
            for (V u : g.vertexSet()) {
                Pair<Double, E> oldPair = distanceAndPredecessorMap.get(u);
                Pair<Double, Object> newPair = oldPair != null ? Pair.of(oldPair.getFirst() - vertexWeights.get(v) + vertexWeights.get(u), oldPair.getSecond()) : Pair.of(Double.POSITIVE_INFINITY, null);
                this.distance[this.vertexIndices.get(v).intValue()][this.vertexIndices.get(u).intValue()] = newPair.getFirst();
                this.pred[this.vertexIndices.get(v).intValue()][this.vertexIndices.get(u).intValue()] = newPair.getSecond();
            }
        }
    }

    private Map<V, Double> computeVertexWeights(Graph<V, E> g) {
        assert (g.getType().isDirected());
        Graph extraGraph = GraphTypeBuilder.directed().allowingMultipleEdges(true).allowingSelfLoops(true).edgeSupplier(this.graph.getEdgeSupplier()).vertexSupplier(this.graph.getVertexSupplier()).buildGraph();
        Object s = extraGraph.addVertex();
        if (s == null) {
            throw new IllegalArgumentException("Invalid vertex supplier (does not return unique vertices on each call).");
        }
        HashMap zeroWeightFunction = new HashMap();
        for (V v : g.vertexSet()) {
            extraGraph.addVertex(v);
            zeroWeightFunction.put(extraGraph.addEdge(s, v), 0.0);
        }
        AsGraphUnion unionGraph = new AsGraphUnion(new AsWeightedGraph(extraGraph, zeroWeightFunction), g);
        ShortestPathAlgorithm.SingleSourcePaths paths = new BellmanFordShortestPath(unionGraph).getPaths(s);
        HashMap<V, Double> weights = new HashMap<V, Double>();
        for (V v : g.vertexSet()) {
            weights.put(v, paths.getWeight(v));
        }
        return weights;
    }

    private Map<V, Integer> computeVertexIndices(Graph<V, E> g) {
        HashMap<V, Integer> numbering = new HashMap<V, Integer>();
        int num = 0;
        for (V v : g.vertexSet()) {
            numbering.put(v, num++);
        }
        return numbering;
    }

    class JohnsonSingleSourcePaths
    implements ShortestPathAlgorithm.SingleSourcePaths<V, E> {
        private V source;

        public JohnsonSingleSourcePaths(V source) {
            this.source = source;
        }

        @Override
        public Graph<V, E> getGraph() {
            return JohnsonShortestPaths.this.graph;
        }

        @Override
        public V getSourceVertex() {
            return this.source;
        }

        @Override
        public double getWeight(V sink) {
            return JohnsonShortestPaths.this.getPathWeight(this.source, sink);
        }

        @Override
        public GraphPath<V, E> getPath(V sink) {
            return JohnsonShortestPaths.this.getPath(this.source, sink);
        }
    }
}

