/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.util.CollectionUtil;

public class CompleteBipartiteGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final int sizeA;
    private final int sizeB;
    private final Set<V> partitionA;
    private final Set<V> partitionB;

    public CompleteBipartiteGraphGenerator(int partitionA, int partitionB) {
        if (partitionA < 0 || partitionB < 0) {
            throw new IllegalArgumentException("partition sizes must be non-negative");
        }
        this.sizeA = partitionA;
        this.sizeB = partitionB;
        this.partitionA = CollectionUtil.newLinkedHashSetWithExpectedSize(this.sizeA);
        this.partitionB = CollectionUtil.newLinkedHashSetWithExpectedSize(this.sizeB);
    }

    public CompleteBipartiteGraphGenerator(Set<V> partitionA, Set<V> partitionB) {
        if (partitionA.isEmpty() || partitionB.isEmpty()) {
            throw new IllegalArgumentException("partitions must be non-empty");
        }
        this.sizeA = 0;
        this.sizeB = 0;
        this.partitionA = partitionA;
        this.partitionB = partitionB;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int i;
        for (i = 0; i < this.sizeA; ++i) {
            this.partitionA.add(target.addVertex());
        }
        for (i = 0; i < this.sizeB; ++i) {
            this.partitionB.add(target.addVertex());
        }
        for (V u : this.partitionA) {
            for (V v : this.partitionB) {
                target.addEdge(u, v);
            }
        }
    }
}

