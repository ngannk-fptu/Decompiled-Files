/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GusfieldEquivalentFlowTree<V, E>
implements MaximumFlowAlgorithm<V, E> {
    private final int n;
    private final MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm;
    private List<V> vertexList = new ArrayList<V>();
    private Map<V, Integer> indexMap = new HashMap<V, Integer>();
    private int[] p;
    private int[] neighbors;
    private double[][] flowMatrix = null;
    private V lastInvokedSource = null;
    private V lastInvokedTarget = null;

    public GusfieldEquivalentFlowTree(Graph<V, E> network) {
        this(network, 1.0E-9);
    }

    public GusfieldEquivalentFlowTree(Graph<V, E> network, double epsilon) {
        this(network, new PushRelabelMFImpl<V, E>(network, epsilon));
    }

    public GusfieldEquivalentFlowTree(Graph<V, E> network, MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm) {
        GraphTests.requireUndirected(network);
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

    private void calculateEquivalentFlowTree() {
        this.flowMatrix = new double[this.n][this.n];
        this.p = new int[this.n];
        this.neighbors = new int[this.n];
        for (int s = 1; s < this.n; ++s) {
            int i;
            int t;
            this.neighbors[s] = t = this.p[s];
            double flowValue = this.minimumSTCutAlgorithm.calculateMinCut(this.vertexList.get(s), this.vertexList.get(t));
            Set<V> sourcePartition = this.minimumSTCutAlgorithm.getSourcePartition();
            for (i = s; i < this.n; ++i) {
                if (!sourcePartition.contains(this.vertexList.get(i)) || this.p[i] != t) continue;
                this.p[i] = s;
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

    public SimpleWeightedGraph<V, DefaultWeightedEdge> getEquivalentFlowTree() {
        if (this.p == null) {
            this.calculateEquivalentFlowTree();
        }
        SimpleWeightedGraph<V, DefaultWeightedEdge> equivalentFlowTree = new SimpleWeightedGraph<V, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(equivalentFlowTree, this.vertexList);
        for (int i = 1; i < this.n; ++i) {
            DefaultWeightedEdge e = (DefaultWeightedEdge)equivalentFlowTree.addEdge(this.vertexList.get(i), this.vertexList.get(this.neighbors[i]));
            equivalentFlowTree.setEdgeWeight(e, this.flowMatrix[i][this.neighbors[i]]);
        }
        return equivalentFlowTree;
    }

    @Override
    public MaximumFlowAlgorithm.MaximumFlow<E> getMaximumFlow(V source, V sink) {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public double getMaximumFlowValue(V source, V sink) {
        assert (this.indexMap.containsKey(source) && this.indexMap.containsKey(sink));
        this.lastInvokedSource = source;
        this.lastInvokedTarget = sink;
        if (this.p == null) {
            this.calculateEquivalentFlowTree();
        }
        return this.flowMatrix[this.indexMap.get(source)][this.indexMap.get(sink)];
    }

    @Override
    public Map<E, Double> getFlowMap() {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public V getFlowDirection(E e) {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }
}

