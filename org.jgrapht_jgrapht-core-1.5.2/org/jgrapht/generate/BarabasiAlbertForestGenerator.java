/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.generate.GraphGenerator;

public class BarabasiAlbertForestGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final int t;
    private final int n;

    public BarabasiAlbertForestGenerator(int t, int n) {
        this(t, n, new Random());
    }

    public BarabasiAlbertForestGenerator(int t, int n, long seed) {
        this(t, n, new Random(seed));
    }

    public BarabasiAlbertForestGenerator(int t, int n, Random rng) {
        if (t < 1) {
            throw new IllegalArgumentException("invalid number of trees (" + t + " < 1)");
        }
        this.t = t;
        if (n < t) {
            throw new IllegalArgumentException("total number of nodes must be at least equal to the number of trees");
        }
        this.n = n;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int i;
        GraphTests.requireUndirected(target);
        if (!target.vertexSet().isEmpty()) {
            throw new IllegalArgumentException("target graph is not empty");
        }
        ArrayList<Object> nodes = new ArrayList<Object>();
        for (i = 0; i < this.t; ++i) {
            nodes.add(target.addVertex());
        }
        for (i = this.t; i < this.n; ++i) {
            V v = target.addVertex();
            Object u = nodes.get(this.rng.nextInt(nodes.size()));
            assert (!target.containsEdge(v, u));
            target.addEdge(v, u);
            nodes.add(v);
            if (i <= 1) continue;
            nodes.add(u);
        }
    }
}

