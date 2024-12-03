/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clustering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;

public class UndirectedModularityMeasurer<V, E> {
    private static final String INVALID_PARTITION_OF_VERTICES = "Invalid partition of vertices";
    private final Graph<V, E> graph;
    private double m;
    private Map<V, Double> degrees;

    public UndirectedModularityMeasurer(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
        this.degrees = new HashMap<V, Double>();
        this.precomputeDegrees(graph);
    }

    public double modularity(List<Set<V>> partitions) {
        int totalPartitions = partitions.size();
        HashMap<V, Integer> vertexPartition = new HashMap<V, Integer>();
        double[] weightedDegreeInPartition = new double[totalPartitions];
        int curPartition = 0;
        for (Set<V> set : partitions) {
            weightedDegreeInPartition[curPartition] = 0.0;
            for (V v : set) {
                vertexPartition.put(v, curPartition);
                Double d = this.degrees.get(v);
                if (d == null) {
                    throw new IllegalArgumentException(INVALID_PARTITION_OF_VERTICES);
                }
                int n = curPartition;
                weightedDegreeInPartition[n] = weightedDegreeInPartition[n] + d;
            }
            ++curPartition;
        }
        double[] edgeWeightInPartition = new double[totalPartitions];
        for (E e : this.graph.edgeSet()) {
            V v;
            v = this.graph.getEdgeSource(e);
            V u = this.graph.getEdgeTarget(e);
            Integer pv = (Integer)vertexPartition.get(v);
            if (pv == null) {
                throw new IllegalArgumentException(INVALID_PARTITION_OF_VERTICES);
            }
            Integer pu = (Integer)vertexPartition.get(u);
            if (pu == null) {
                throw new IllegalArgumentException(INVALID_PARTITION_OF_VERTICES);
            }
            if (pv.intValue() != pu.intValue()) continue;
            int n = pv;
            edgeWeightInPartition[n] = edgeWeightInPartition[n] + this.graph.getEdgeWeight(e);
        }
        double d = 0.0;
        for (int p = 0; p < totalPartitions; ++p) {
            double expectedEdgeWeightInPartition = weightedDegreeInPartition[p] * weightedDegreeInPartition[p] / (2.0 * this.m);
            d += 2.0 * edgeWeightInPartition[p] - expectedEdgeWeightInPartition;
        }
        return d /= 2.0 * this.m;
    }

    private void precomputeDegrees(Graph<V, E> graph) {
        if (graph.getType().isWeighted()) {
            this.m = graph.edgeSet().stream().collect(Collectors.summingDouble(graph::getEdgeWeight));
            for (V v : graph.vertexSet()) {
                double sum = 0.0;
                for (E e : graph.outgoingEdgesOf(v)) {
                    V u = Graphs.getOppositeVertex(graph, e, v);
                    if (u.equals(v)) {
                        sum += 2.0 * graph.getEdgeWeight(e);
                        continue;
                    }
                    sum += graph.getEdgeWeight(e);
                }
                this.degrees.put((Double)v, sum);
            }
        } else {
            this.m = graph.edgeSet().size();
            for (V v : graph.vertexSet()) {
                this.degrees.put((Double)v, Double.valueOf(graph.degreeOf(v)));
            }
        }
    }
}

