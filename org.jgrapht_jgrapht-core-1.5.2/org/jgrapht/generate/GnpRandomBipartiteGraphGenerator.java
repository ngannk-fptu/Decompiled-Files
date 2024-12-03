/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class GnpRandomBipartiteGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final int n1;
    private final int n2;
    private final double p;
    private List<V> partitionA;
    private List<V> partitionB;

    public GnpRandomBipartiteGraphGenerator(int n1, int n2, double p) {
        this(n1, n2, p, new Random());
    }

    public GnpRandomBipartiteGraphGenerator(int n1, int n2, double p, long seed) {
        this(n1, n2, p, new Random(seed));
    }

    public GnpRandomBipartiteGraphGenerator(int n1, int n2, double p, Random rng) {
        if (n1 < 0) {
            throw new IllegalArgumentException("number of vertices must be non-negative");
        }
        this.n1 = n1;
        if (n2 < 0) {
            throw new IllegalArgumentException("number of vertices must be non-negative");
        }
        this.n2 = n2;
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("not valid probability of edge existence");
        }
        this.p = p;
        this.rng = Objects.requireNonNull(rng);
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int i;
        if (this.n1 + this.n2 == 0) {
            return;
        }
        int previousVertexSetSize = target.vertexSet().size();
        this.partitionA = new ArrayList<V>(this.n1);
        for (i = 0; i < this.n1; ++i) {
            this.partitionA.add(target.addVertex());
        }
        this.partitionB = new ArrayList<V>(this.n2);
        for (i = 0; i < this.n2; ++i) {
            this.partitionB.add(target.addVertex());
        }
        if (target.vertexSet().size() != previousVertexSetSize + this.n1 + this.n2) {
            throw new IllegalArgumentException("Vertex factory did not produce " + (this.n1 + this.n2) + " distinct vertices.");
        }
        boolean isDirected = target.getType().isDirected();
        for (int i2 = 0; i2 < this.n1; ++i2) {
            V s = this.partitionA.get(i2);
            for (int j = 0; j < this.n2; ++j) {
                V t = this.partitionB.get(j);
                if (this.rng.nextDouble() < this.p) {
                    target.addEdge(s, t);
                }
                if (!isDirected || !(this.rng.nextDouble() < this.p)) continue;
                target.addEdge(t, s);
            }
        }
    }

    public Set<V> getFirstPartition() {
        if (this.partitionA.size() <= this.partitionB.size()) {
            return new LinkedHashSet<V>(this.partitionA);
        }
        return new LinkedHashSet<V>(this.partitionB);
    }

    public Set<V> getSecondPartition() {
        if (this.partitionB.size() >= this.partitionA.size()) {
            return new LinkedHashSet<V>(this.partitionB);
        }
        return new LinkedHashSet<V>(this.partitionA);
    }
}

