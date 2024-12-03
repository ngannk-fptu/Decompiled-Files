/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.flow.GusfieldGomoryHuCutTree;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class PadbergRaoOddMinimumCutset<V, E> {
    private final Graph<V, E> network;
    private Set<V> oddVertices;
    private final GusfieldGomoryHuCutTree<V, E> gusfieldGomoryHuCutTreeAlgorithm;
    private SimpleWeightedGraph<V, DefaultWeightedEdge> gomoryHuTree;
    private double minimumCutWeight = Double.MAX_VALUE;
    private Set<V> sourcePartitionMinimumCut;

    public PadbergRaoOddMinimumCutset(Graph<V, E> network) {
        this(network, 1.0E-9);
    }

    public PadbergRaoOddMinimumCutset(Graph<V, E> network, double epsilon) {
        this(network, new PushRelabelMFImpl<V, E>(network, epsilon));
    }

    public PadbergRaoOddMinimumCutset(Graph<V, E> network, MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm) {
        this.network = GraphTests.requireUndirected(network);
        this.gusfieldGomoryHuCutTreeAlgorithm = new GusfieldGomoryHuCutTree<V, E>(network, minimumSTCutAlgorithm);
    }

    public double calculateMinCut(Set<V> oddVertices, boolean useTreeCompression) {
        this.minimumCutWeight = Double.MAX_VALUE;
        this.oddVertices = oddVertices;
        if (oddVertices.size() % 2 == 1) {
            throw new IllegalArgumentException("There needs to be an even number of odd vertices");
        }
        assert (this.network.vertexSet().containsAll(oddVertices));
        assert (this.network.edgeSet().stream().noneMatch(e -> this.network.getEdgeWeight(e) < 0.0));
        this.gomoryHuTree = this.gusfieldGomoryHuCutTreeAlgorithm.getGomoryHuTree();
        if (useTreeCompression) {
            return this.calculateMinCutWithTreeCompression();
        }
        return this.calculateMinCutWithoutTreeCompression();
    }

    private double calculateMinCutWithoutTreeCompression() {
        LinkedHashSet edges = new LinkedHashSet(this.gomoryHuTree.edgeSet());
        for (DefaultWeightedEdge edge : edges) {
            Object source = this.gomoryHuTree.getEdgeSource(edge);
            Object target = this.gomoryHuTree.getEdgeTarget(edge);
            double edgeWeight = this.gomoryHuTree.getEdgeWeight(edge);
            if (edgeWeight >= this.minimumCutWeight) continue;
            this.gomoryHuTree.removeEdge(edge);
            Set<V> sourcePartition = new ConnectivityInspector(this.gomoryHuTree).connectedSetOf(source);
            if (PadbergRaoOddMinimumCutset.isOddVertexSet(sourcePartition, this.oddVertices)) {
                this.minimumCutWeight = edgeWeight;
                this.sourcePartitionMinimumCut = sourcePartition;
            }
            this.gomoryHuTree.addEdge(source, target, edge);
        }
        return this.minimumCutWeight;
    }

    private double calculateMinCutWithTreeCompression() {
        ArrayDeque<Set<V>> queue = new ArrayDeque<Set<V>>();
        queue.add(this.oddVertices);
        while (!queue.isEmpty()) {
            Set nextCluster = (Set)queue.poll();
            this.splitCluster(nextCluster, queue);
        }
        return this.minimumCutWeight;
    }

    private void splitCluster(Set<V> cluster, Queue<Set<V>> queue) {
        assert (cluster.size() >= 2);
        Iterator<V> iterator = cluster.iterator();
        V oddNode1 = iterator.next();
        V oddNode2 = iterator.next();
        double cutWeight = this.gusfieldGomoryHuCutTreeAlgorithm.calculateMinCut(oddNode1, oddNode2);
        Set<V> sourcePartition = null;
        if (cutWeight < this.minimumCutWeight && PadbergRaoOddMinimumCutset.isOddVertexSet(sourcePartition = this.gusfieldGomoryHuCutTreeAlgorithm.getSourcePartition(), this.oddVertices)) {
            this.minimumCutWeight = cutWeight;
            this.sourcePartitionMinimumCut = sourcePartition;
        }
        if (cluster.size() == 2) {
            return;
        }
        if (sourcePartition == null) {
            sourcePartition = this.gusfieldGomoryHuCutTreeAlgorithm.getSourcePartition();
        }
        Set<V> split1 = this.intersection(cluster, sourcePartition);
        HashSet<V> split2 = new HashSet<V>(cluster);
        split2.removeAll(split1);
        if (split1.size() > 1) {
            queue.add(split1);
        }
        if (split2.size() > 1) {
            queue.add(split2);
        }
    }

    private Set<V> intersection(Set<V> set1, Set<V> set2) {
        Set<V> b;
        Set<V> a;
        if (set1.size() <= set2.size()) {
            a = set1;
            b = set2;
        } else {
            a = set2;
            b = set1;
        }
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }

    public static <V> boolean isOddVertexSet(Set<V> vertices, Set<V> oddVertices) {
        if (vertices.size() < oddVertices.size()) {
            return vertices.stream().filter(oddVertices::contains).count() % 2L == 1L;
        }
        return oddVertices.stream().filter(vertices::contains).count() % 2L == 1L;
    }

    public Set<V> getSourcePartition() {
        return this.sourcePartitionMinimumCut;
    }

    public Set<V> getSinkPartition() {
        LinkedHashSet<V> sinkPartition = new LinkedHashSet<V>(this.network.vertexSet());
        sinkPartition.removeAll(this.sourcePartitionMinimumCut);
        return sinkPartition;
    }

    public Set<E> getCutEdges() {
        Predicate<Object> predicate = e -> this.sourcePartitionMinimumCut.contains(this.network.getEdgeSource(e)) ^ this.sourcePartitionMinimumCut.contains(this.network.getEdgeTarget(e));
        return this.network.edgeSet().stream().filter(predicate).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

