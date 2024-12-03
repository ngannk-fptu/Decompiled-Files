/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class GnpRandomGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private static final boolean DEFAULT_ALLOW_LOOPS = false;
    private final Random rng;
    private final int n;
    private final double p;
    private final boolean createLoops;

    public GnpRandomGraphGenerator(int n, double p) {
        this(n, p, new Random(), false);
    }

    public GnpRandomGraphGenerator(int n, double p, long seed) {
        this(n, p, new Random(seed), false);
    }

    public GnpRandomGraphGenerator(int n, double p, long seed, boolean createLoops) {
        this(n, p, new Random(seed), createLoops);
    }

    public GnpRandomGraphGenerator(int n, double p, Random rng, boolean createLoops) {
        if (n < 0) {
            throw new IllegalArgumentException("number of vertices must be non-negative");
        }
        this.n = n;
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("not valid probability of edge existence");
        }
        this.p = p;
        this.rng = Objects.requireNonNull(rng);
        this.createLoops = createLoops;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.n == 0) {
            return;
        }
        if (this.createLoops && !target.getType().isAllowingSelfLoops()) {
            throw new IllegalArgumentException("Provided graph does not support self-loops");
        }
        int previousVertexSetSize = target.vertexSet().size();
        ArrayList<V> vertices = new ArrayList<V>(this.n);
        for (int i = 0; i < this.n; ++i) {
            vertices.add(target.addVertex());
        }
        if (target.vertexSet().size() != previousVertexSetSize + this.n) {
            throw new IllegalArgumentException("Vertex factory did not produce " + this.n + " distinct vertices.");
        }
        boolean isDirected = target.getType().isDirected();
        for (int i = 0; i < this.n; ++i) {
            for (int j = i; j < this.n; ++j) {
                if (i == j && !this.createLoops) continue;
                Object s = null;
                Object t = null;
                if (this.rng.nextDouble() < this.p) {
                    s = vertices.get(i);
                    t = vertices.get(j);
                    target.addEdge(s, t);
                }
                if (!isDirected || !(this.rng.nextDouble() < this.p)) continue;
                if (s == null) {
                    s = vertices.get(i);
                    t = vertices.get(j);
                }
                target.addEdge(t, s);
            }
        }
    }
}

