/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clustering;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.util.Pair;

public class LabelPropagationClustering<V, E>
implements ClusteringAlgorithm<V> {
    private Graph<V, E> graph;
    private int maxIterations;
    private Random rng;
    private ClusteringAlgorithm.Clustering<V> result;

    public LabelPropagationClustering(Graph<V, E> graph) {
        this(graph, 0, new Random());
    }

    public LabelPropagationClustering(Graph<V, E> graph, Random rng) {
        this(graph, 0, rng);
    }

    public LabelPropagationClustering(Graph<V, E> graph, int maxIterations) {
        this(graph, maxIterations, new Random());
    }

    public LabelPropagationClustering(Graph<V, E> graph, int maxIterations, Random rng) {
        this.graph = GraphTests.requireUndirected(graph);
        this.maxIterations = maxIterations;
        this.rng = Objects.requireNonNull(rng);
        if (maxIterations < 0) {
            throw new IllegalArgumentException("Max iterations cannot be negative");
        }
    }

    @Override
    public ClusteringAlgorithm.Clustering<V> getClustering() {
        if (this.result == null) {
            this.result = new ClusteringAlgorithm.ClusteringImpl<V>(new Implementation<V, E>(this.graph, this.rng, this.maxIterations).compute());
        }
        return this.result;
    }

    private static class Implementation<V, E> {
        private Graph<V, E> graph;
        private Random rng;
        private int maxIterations;
        private Map<V, String> labels;

        public Implementation(Graph<V, E> graph, Random rng, int maxIterations) {
            this.graph = graph;
            this.rng = rng;
            this.maxIterations = maxIterations;
            this.labels = new HashMap<V, String>();
            int i = 0;
            for (V v : graph.vertexSet()) {
                this.labels.put((String)v, String.valueOf(i++));
            }
        }

        public List<Set<V>> compute() {
            for (int currentIteration = 0; this.maxIterations <= 0 || currentIteration <= this.maxIterations; ++currentIteration) {
                boolean anyChange = false;
                ArrayList<V> allVertices = new ArrayList<V>(this.graph.vertexSet());
                Collections.shuffle(allVertices, this.rng);
                for (Object v : allVertices) {
                    if (!this.updateLabel(v)) continue;
                    anyChange = true;
                }
                if (!anyChange || this.shouldStop()) break;
            }
            return this.computeCommunities();
        }

        private boolean shouldStop() {
            for (V v : this.graph.vertexSet()) {
                Pair<Map<String, Integer>, Integer> labelCountsAndMaximum = this.getNeighborLabelCountsAndMaximum(v);
                Map<String, Integer> counts = labelCountsAndMaximum.getFirst();
                String vLabel = this.labels.get(v);
                int vLabelCount = counts.getOrDefault(vLabel, 0);
                int maxCount = labelCountsAndMaximum.getSecond();
                if (maxCount <= vLabelCount) continue;
                return false;
            }
            return true;
        }

        private Pair<Map<String, Integer>, Integer> getNeighborLabelCountsAndMaximum(V v) {
            HashMap<String, Integer> counts = new HashMap<String, Integer>();
            String vLabel = this.labels.get(v);
            int maxCount = 0;
            for (E e : this.graph.edgesOf(v)) {
                V u = Graphs.getOppositeVertex(this.graph, e, v);
                String uLabel = this.labels.get(u);
                int newCount = counts.getOrDefault(uLabel, 0) + 1;
                counts.put(uLabel, newCount);
                if (newCount <= maxCount || uLabel.equals(vLabel)) continue;
                maxCount = newCount;
            }
            return Pair.of(counts, maxCount);
        }

        private boolean updateLabel(V v) {
            if (this.graph.degreeOf(v) == 0) {
                return false;
            }
            Pair<Map<String, Integer>, Integer> labelCountsAndMaximum = this.getNeighborLabelCountsAndMaximum(v);
            Map<String, Integer> counts = labelCountsAndMaximum.getFirst();
            String oldLabel = this.labels.get(v);
            int vLabelCount = counts.getOrDefault(oldLabel, 0);
            int maxCount = Math.max(labelCountsAndMaximum.getSecond(), vLabelCount);
            ArrayList maxLabels = counts.entrySet().stream().filter(e -> (Integer)e.getValue() == maxCount).map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
            String newLabel = (String)maxLabels.get(this.rng.nextInt(maxLabels.size()));
            if (oldLabel.equals(newLabel)) {
                return false;
            }
            this.labels.put((String)v, newLabel);
            return true;
        }

        private List<Set<V>> computeCommunities() {
            HashMap<V, String> finalLabels = new HashMap<V, String>();
            int nextLabel = 0;
            for (V v : this.graph.vertexSet()) {
                if (finalLabels.containsKey(v)) continue;
                ArrayDeque<V> frontier = new ArrayDeque<V>();
                String currentLabel = String.valueOf(nextLabel++);
                finalLabels.put(v, currentLabel);
                frontier.addLast(v);
                while (!frontier.isEmpty()) {
                    Object u = frontier.removeFirst();
                    String uLabel = this.labels.get(u);
                    for (E e : this.graph.edgesOf(u)) {
                        V w = Graphs.getOppositeVertex(this.graph, e, u);
                        String wLabel = this.labels.get(w);
                        if (!wLabel.equals(uLabel) || finalLabels.containsKey(w)) continue;
                        finalLabels.put(w, currentLabel);
                        frontier.addLast(w);
                    }
                }
            }
            return this.convert(this.graph, finalLabels);
        }

        private List<Set<V>> convert(Graph<V, E> graph, Map<V, String> labels) {
            LinkedHashMap<String, LinkedHashSet<V>> clusterMap = new LinkedHashMap<String, LinkedHashSet<V>>();
            for (V v : graph.vertexSet()) {
                String rv = labels.get(v);
                if (rv == null) {
                    throw new IllegalArgumentException("Not all vertices have labels.");
                }
                LinkedHashSet<V> cluster = (LinkedHashSet<V>)clusterMap.get(rv);
                if (cluster == null) {
                    cluster = new LinkedHashSet<V>();
                    clusterMap.put(rv, cluster);
                }
                cluster.add(v);
            }
            return new ArrayList<Set<V>>(clusterMap.values());
        }
    }
}

