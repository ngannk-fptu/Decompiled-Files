/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.partition;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.PartitioningAlgorithm;

public class BipartitePartitioning<V, E>
implements PartitioningAlgorithm<V> {
    private Graph<V, E> graph;
    private boolean computed = false;
    private PartitioningAlgorithm.Partitioning<V> cachedPartitioning;

    public BipartitePartitioning(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "graph cannot be null");
    }

    public boolean isBipartite() {
        if (GraphTests.isEmpty(this.graph)) {
            return true;
        }
        try {
            if (Math.multiplyExact(4, this.graph.edgeSet().size()) > Math.multiplyExact(this.graph.vertexSet().size(), this.graph.vertexSet().size())) {
                return false;
            }
        }
        catch (ArithmeticException arithmeticException) {
            // empty catch block
        }
        return this.getPartitioning() != null;
    }

    @Override
    public PartitioningAlgorithm.Partitioning<V> getPartitioning() {
        if (this.computed) {
            return this.cachedPartitioning;
        }
        LinkedHashSet<V> unknown = new LinkedHashSet<V>(this.graph.vertexSet());
        LinkedHashSet<V> odd = new LinkedHashSet<V>();
        ArrayDeque<Object> queue = new ArrayDeque<Object>();
        while (!unknown.isEmpty()) {
            if (queue.isEmpty()) {
                queue.add(unknown.iterator().next());
            }
            Object v = queue.removeFirst();
            unknown.remove(v);
            for (E e : this.graph.edgesOf(v)) {
                V n = Graphs.getOppositeVertex(this.graph, e, v);
                if (unknown.contains(n)) {
                    queue.add(n);
                    if (odd.contains(v)) continue;
                    odd.add(n);
                    continue;
                }
                if (odd.contains(v) != odd.contains(n)) continue;
                this.computed = true;
                this.cachedPartitioning = null;
                return null;
            }
        }
        LinkedHashSet<V> even = new LinkedHashSet<V>(this.graph.vertexSet());
        even.removeAll(odd);
        this.computed = true;
        this.cachedPartitioning = new PartitioningAlgorithm.PartitioningImpl(Arrays.asList(even, odd));
        return this.cachedPartitioning;
    }

    @Override
    public boolean isValidPartitioning(PartitioningAlgorithm.Partitioning<V> partitioning) {
        Objects.requireNonNull(partitioning, "Partition cannot be null");
        if (partitioning.getNumberPartitions() != 2) {
            return false;
        }
        Set<V> firstPartition = partitioning.getPartition(0);
        Set<V> secondPartition = partitioning.getPartition(1);
        Objects.requireNonNull(firstPartition, "First partition class cannot be null");
        Objects.requireNonNull(secondPartition, "Second partition class cannot be null");
        if (this.graph.vertexSet().size() != firstPartition.size() + secondPartition.size()) {
            return false;
        }
        for (V v : this.graph.vertexSet()) {
            Set<V> otherPartition;
            if (firstPartition.contains(v)) {
                otherPartition = secondPartition;
            } else if (secondPartition.contains(v)) {
                otherPartition = firstPartition;
            } else {
                return false;
            }
            for (E e : this.graph.edgesOf(v)) {
                V other = Graphs.getOppositeVertex(this.graph, e, v);
                if (otherPartition.contains(other)) continue;
                return false;
            }
        }
        return true;
    }
}

