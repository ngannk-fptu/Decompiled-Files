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

public class GnmRandomBipartiteGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final int n1;
    private final int n2;
    private final int m;
    private List<V> partitionA;
    private List<V> partitionB;

    public GnmRandomBipartiteGraphGenerator(int n1, int n2, int m) {
        this(n1, n2, m, new Random());
    }

    public GnmRandomBipartiteGraphGenerator(int n1, int n2, int m, long seed) {
        this(n1, n2, m, new Random(seed));
    }

    public GnmRandomBipartiteGraphGenerator(int n1, int n2, int m, Random rng) {
        if (n1 < 0) {
            throw new IllegalArgumentException("number of vertices must be non-negative");
        }
        this.n1 = n1;
        if (n2 < 0) {
            throw new IllegalArgumentException("number of vertices must be non-negative");
        }
        this.n2 = n2;
        if (m < 0) {
            throw new IllegalArgumentException("number of edges must be non-negative");
        }
        this.m = m;
        this.rng = Objects.requireNonNull(rng);
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int maxAllowedEdges;
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
        try {
            maxAllowedEdges = isDirected ? Math.multiplyExact(2, Math.multiplyExact(this.n1, this.n2)) : Math.multiplyExact(this.n1, this.n2);
        }
        catch (ArithmeticException e) {
            maxAllowedEdges = Integer.MAX_VALUE;
        }
        if (this.m > maxAllowedEdges) {
            throw new IllegalArgumentException("number of edges not valid for bipartite graph with " + this.n1 + " and " + this.n2 + " vertices");
        }
        int edgesCounter = 0;
        while (edgesCounter < this.m) {
            V s = this.partitionA.get(this.rng.nextInt(this.n1));
            V t = this.partitionB.get(this.rng.nextInt(this.n2));
            if (isDirected && this.rng.nextBoolean()) {
                V tmp = s;
                s = t;
                t = tmp;
            }
            if (target.containsEdge(s, t)) continue;
            try {
                E resultEdge = target.addEdge(s, t);
                if (resultEdge == null) continue;
                ++edgesCounter;
            }
            catch (IllegalArgumentException illegalArgumentException) {}
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

