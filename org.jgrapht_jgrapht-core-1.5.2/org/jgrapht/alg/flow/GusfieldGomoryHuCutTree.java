/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GusfieldGomoryHuCutTree<V, E>
implements MaximumFlowAlgorithm<V, E>,
MinimumSTCutAlgorithm<V, E> {
    private final Graph<V, E> network;
    private final int n;
    private final MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm;
    private List<V> vertexList = new ArrayList<V>();
    private Map<V, Integer> indexMap = new HashMap<V, Integer>();
    private int[] p;
    private double[] fl;
    private double[][] flowMatrix = null;
    private V lastInvokedSource = null;
    private V lastInvokedTarget = null;
    private Set<V> sourcePartitionLastInvokedSource = null;
    private SimpleWeightedGraph<V, DefaultWeightedEdge> gomoryHuTree = null;

    public GusfieldGomoryHuCutTree(Graph<V, E> network) {
        this(network, 1.0E-9);
    }

    public GusfieldGomoryHuCutTree(Graph<V, E> network, double epsilon) {
        this(network, new PushRelabelMFImpl<V, E>(network, epsilon));
    }

    public GusfieldGomoryHuCutTree(Graph<V, E> network, MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm) {
        this.network = GraphTests.requireUndirected(network);
        this.n = network.vertexSet().size();
        if (this.n < 2) {
            throw new IllegalArgumentException("Graph must have at least 2 vertices");
        }
        this.minimumSTCutAlgorithm = minimumSTCutAlgorithm;
        this.vertexList.addAll(network.vertexSet());
        for (int i = 0; i < this.vertexList.size(); ++i) {
            this.indexMap.put((Integer)this.vertexList.get(i), i);
        }
    }

    private void calculateGomoryHuTree() {
        this.flowMatrix = new double[this.n][this.n];
        this.p = new int[this.n];
        this.fl = new double[this.n];
        for (int s = 1; s < this.n; ++s) {
            int i;
            int t = this.p[s];
            double flowValue = this.minimumSTCutAlgorithm.calculateMinCut(this.vertexList.get(s), this.vertexList.get(t));
            Set<V> sourcePartition = this.minimumSTCutAlgorithm.getSourcePartition();
            this.fl[s] = flowValue;
            for (i = 0; i < this.n; ++i) {
                if (i == s || !sourcePartition.contains(this.vertexList.get(i)) || this.p[i] != t) continue;
                this.p[i] = s;
            }
            if (sourcePartition.contains(this.vertexList.get(this.p[t]))) {
                this.p[s] = this.p[t];
                this.p[t] = s;
                this.fl[s] = this.fl[t];
                this.fl[t] = flowValue;
            }
            double d = flowValue;
            this.flowMatrix[t][s] = d;
            this.flowMatrix[s][t] = d;
            for (i = 0; i < s; ++i) {
                if (i == t) continue;
                double d2 = Math.min(this.flowMatrix[s][t], this.flowMatrix[t][i]);
                this.flowMatrix[i][s] = d2;
                this.flowMatrix[s][i] = d2;
            }
        }
    }

    public SimpleWeightedGraph<V, DefaultWeightedEdge> getGomoryHuTree() {
        if (this.p == null) {
            this.calculateGomoryHuTree();
        }
        SimpleWeightedGraph gomoryHuTree = new SimpleWeightedGraph(DefaultWeightedEdge.class);
        Graphs.addAllVertices(gomoryHuTree, this.vertexList);
        for (int i = 1; i < this.n; ++i) {
            Graphs.addEdge(gomoryHuTree, this.vertexList.get(i), this.vertexList.get(this.p[i]), this.fl[i]);
        }
        return gomoryHuTree;
    }

    @Override
    public MaximumFlowAlgorithm.MaximumFlow<E> getMaximumFlow(V source, V sink) {
        throw new UnsupportedOperationException("Flows calculated via Gomory-Hu trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public double getMaximumFlowValue(V source, V sink) {
        assert (this.indexMap.containsKey(source) && this.indexMap.containsKey(sink));
        this.lastInvokedSource = source;
        this.lastInvokedTarget = sink;
        this.sourcePartitionLastInvokedSource = null;
        this.gomoryHuTree = null;
        if (this.p == null) {
            this.calculateGomoryHuTree();
        }
        return this.flowMatrix[this.indexMap.get(source)][this.indexMap.get(sink)];
    }

    @Override
    public Map<E, Double> getFlowMap() {
        throw new UnsupportedOperationException("Flows calculated via Gomory-Hu trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public V getFlowDirection(E e) {
        throw new UnsupportedOperationException("Flows calculated via Gomory-Hu trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public double calculateMinCut(V source, V sink) {
        return this.getMaximumFlowValue(source, sink);
    }

    public double calculateMinCut() {
        if (this.gomoryHuTree == null) {
            this.gomoryHuTree = this.getGomoryHuTree();
        }
        DefaultWeightedEdge cheapestEdge = this.gomoryHuTree.edgeSet().stream().min(Comparator.comparing(this.gomoryHuTree::getEdgeWeight)).orElseThrow(() -> new RuntimeException("graph is empty?!"));
        this.lastInvokedSource = this.gomoryHuTree.getEdgeSource(cheapestEdge);
        this.lastInvokedTarget = this.gomoryHuTree.getEdgeTarget(cheapestEdge);
        this.sourcePartitionLastInvokedSource = null;
        return this.gomoryHuTree.getEdgeWeight(cheapestEdge);
    }

    @Override
    public double getCutCapacity() {
        return this.calculateMinCut(this.lastInvokedSource, this.lastInvokedTarget);
    }

    @Override
    public Set<V> getSourcePartition() {
        if (this.sourcePartitionLastInvokedSource != null) {
            return this.sourcePartitionLastInvokedSource;
        }
        if (this.gomoryHuTree == null) {
            this.gomoryHuTree = this.getGomoryHuTree();
        }
        Set<DefaultWeightedEdge> pathEdges = this.findPathBetween(this.gomoryHuTree, this.lastInvokedSource, this.lastInvokedTarget);
        DefaultWeightedEdge cheapestEdge = pathEdges.stream().min(Comparator.comparing(this.gomoryHuTree::getEdgeWeight)).orElseThrow(() -> new RuntimeException("path is empty?!"));
        Object source = this.gomoryHuTree.getEdgeSource(cheapestEdge);
        Object target = this.gomoryHuTree.getEdgeTarget(cheapestEdge);
        this.gomoryHuTree.removeEdge(cheapestEdge);
        this.sourcePartitionLastInvokedSource = new ConnectivityInspector<V, DefaultWeightedEdge>(this.gomoryHuTree).connectedSetOf(this.lastInvokedSource);
        this.gomoryHuTree.addEdge(source, target, cheapestEdge);
        return this.sourcePartitionLastInvokedSource;
    }

    private Set<DefaultWeightedEdge> findPathBetween(SimpleWeightedGraph<V, DefaultWeightedEdge> tree, V source, V target) {
        boolean[] visited = new boolean[this.vertexList.size()];
        HashMap predecessorMap = new HashMap();
        ArrayDeque<V> queue = new ArrayDeque<V>();
        queue.add(source);
        boolean found = false;
        while (!found && !queue.isEmpty()) {
            Object next = queue.poll();
            for (V v : Graphs.neighborListOf(tree, next)) {
                if (!visited[this.indexMap.get(v)]) {
                    predecessorMap.put(v, next);
                    queue.add(v);
                }
                if (v != target) continue;
                found = true;
                break;
            }
            visited[this.indexMap.get(next).intValue()] = true;
        }
        LinkedHashSet<DefaultWeightedEdge> edges = new LinkedHashSet<DefaultWeightedEdge>();
        V v = target;
        while (v != source) {
            Object pred = predecessorMap.get(v);
            edges.add((DefaultWeightedEdge)tree.getEdge(v, pred));
            v = pred;
        }
        return edges;
    }

    @Override
    public Set<V> getSinkPartition() {
        LinkedHashSet<V> sinkPartition = new LinkedHashSet<V>(this.network.vertexSet());
        sinkPartition.removeAll(this.getSourcePartition());
        return sinkPartition;
    }

    @Override
    public Set<E> getCutEdges() {
        LinkedHashSet<E> cutEdges = new LinkedHashSet<E>();
        Set<V> sourcePartion = this.getSourcePartition();
        for (E e : this.network.edgeSet()) {
            V source = this.network.getEdgeSource(e);
            V sink = this.network.getEdgeTarget(e);
            if (!(sourcePartion.contains(source) ^ sourcePartion.contains(sink))) continue;
            cutEdges.add(e);
        }
        return cutEdges;
    }
}

