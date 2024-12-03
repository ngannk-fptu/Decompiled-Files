/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.util.VertexToIntegerMapping;

public class HeldKarpTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    private double memo(int previousNode, int state, double[][] c, double[][] w) {
        if (c[previousNode][state] != Double.MIN_VALUE) {
            return c[previousNode][state];
        }
        double totalCost = Double.MAX_VALUE;
        if (state == (1 << w.length) - 1) {
            if (w[previousNode][0] != Double.MAX_VALUE) {
                totalCost = w[previousNode][0];
            }
        } else {
            for (int i = 0; i < w.length; ++i) {
                if ((state >> i & 1) != 0 || w[previousNode][i] == Double.MAX_VALUE) continue;
                totalCost = Math.min(totalCost, w[previousNode][i] + this.memo(i, state ^ 1 << i, c, w));
            }
        }
        double d = totalCost;
        c[previousNode][state] = d;
        return d;
    }

    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.requireNotEmpty(graph);
        int n = graph.vertexSet().size();
        if (n == 1) {
            return this.getSingletonTour(graph);
        }
        if (n > 31) {
            throw new IllegalArgumentException("The internal representation of the dynamic programming state space cannot represent graphs containing more than 31 vertices. The runtime complexity of this implementation, O(2^|V| x |V|^2),  makes it unsuitable for graphs with more than 31 vertices.");
        }
        VertexToIntegerMapping<V> vertexToIntegerMapping = Graphs.getVertexToIntegerMapping(graph);
        double[][] w = this.computeMinimumWeights(vertexToIntegerMapping.getVertexMap(), graph);
        double[][] c = new double[n][1 << n];
        HeldKarpTSP.fill(c, Double.MIN_VALUE);
        double tourWeight = this.memo(0, 1, c, w);
        if (tourWeight == Double.MAX_VALUE) {
            return null;
        }
        List<V> vertexList = this.reconstructTour(vertexToIntegerMapping.getIndexList(), w, c);
        return this.vertexListToTour(vertexList, graph);
    }

    private double[][] computeMinimumWeights(Map<V, Integer> vertexMap, Graph<V, E> graph) {
        int n = vertexMap.size();
        double[][] w = new double[n][n];
        HeldKarpTSP.fill(w, Double.MAX_VALUE);
        for (E e : graph.edgeSet()) {
            V source = graph.getEdgeSource(e);
            V target = graph.getEdgeTarget(e);
            int u = vertexMap.get(source);
            int v = vertexMap.get(target);
            w[u][v] = Math.min(w[u][v], graph.getEdgeWeight(e));
            if (!graph.getType().isUndirected()) continue;
            w[v][u] = Math.min(w[v][u], graph.getEdgeWeight(e));
        }
        return w;
    }

    private static void fill(double[][] array, double value) {
        for (double[] element : array) {
            Arrays.fill(element, value);
        }
    }

    private List<V> reconstructTour(List<V> indexList, double[][] w, double[][] c) {
        int n = indexList.size();
        ArrayList<V> vertexList = new ArrayList<V>(n);
        int lastNode = 0;
        int lastState = 1;
        vertexList.add(indexList.get(lastNode));
        for (int step = 1; step < n; ++step) {
            int nextNode = -1;
            for (int node = 1; node < n; ++node) {
                if ((lastState & 1 << node) != 0 || w[lastNode][node] == Double.MAX_VALUE || c[node][lastState ^ 1 << node] == Double.MIN_VALUE || Double.compare(c[node][lastState ^ 1 << node] + w[lastNode][node], c[lastNode][lastState]) != 0) continue;
                nextNode = node;
                break;
            }
            assert (nextNode != -1);
            vertexList.add(indexList.get(nextNode));
            lastState ^= 1 << nextNode;
            lastNode = nextNode;
        }
        return vertexList;
    }
}

