/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.util.CollectionUtil;

public class WattsStrogatzGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private static final boolean DEFAULT_ADD_INSTEAD_OF_REWIRE = false;
    private final Random rng;
    private final int n;
    private final int k;
    private final double p;
    private final boolean addInsteadOfRewire;

    public WattsStrogatzGraphGenerator(int n, int k, double p) {
        this(n, k, p, false, new Random());
    }

    public WattsStrogatzGraphGenerator(int n, int k, double p, long seed) {
        this(n, k, p, false, new Random(seed));
    }

    public WattsStrogatzGraphGenerator(int n, int k, double p, boolean addInsteadOfRewire, Random rng) {
        if (n < 3) {
            throw new IllegalArgumentException("number of vertices must be at least 3");
        }
        this.n = n;
        if (k < 1) {
            throw new IllegalArgumentException("number of k-nearest neighbors must be positive");
        }
        if (k % 2 == 1) {
            throw new IllegalArgumentException("number of k-nearest neighbors must be even");
        }
        if (k > n - 2 + n % 2) {
            throw new IllegalArgumentException("invalid k-nearest neighbors");
        }
        this.k = k;
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("invalid probability");
        }
        this.p = p;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
        this.addInsteadOfRewire = addInsteadOfRewire;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int i;
        if (this.n == 0) {
            return;
        }
        if (this.n == 1) {
            target.addVertex();
            return;
        }
        ArrayList<V> ring = new ArrayList<V>(this.n);
        LinkedHashMap adj = CollectionUtil.newLinkedHashMapWithExpectedSize(this.n);
        for (i = 0; i < this.n; ++i) {
            V v = target.addVertex();
            ring.add(v);
            adj.put(v, new ArrayList(this.k));
        }
        for (i = 0; i < this.n; ++i) {
            Object vi = ring.get(i);
            List viAdj = (List)adj.get(vi);
            for (int j = 1; j <= this.k / 2; ++j) {
                viAdj.add(target.addEdge(vi, ring.get((i + j) % this.n)));
            }
        }
        for (int r = 0; r < this.k / 2; ++r) {
            for (int i2 = 0; i2 < this.n; ++i2) {
                if (!(this.rng.nextDouble() < this.p)) continue;
                Object v = ring.get(i2);
                Object e = ((List)adj.get(v)).get(r);
                Object other = ring.get(this.rng.nextInt(this.n));
                if (other.equals(v) || target.containsEdge(v, other)) continue;
                if (!this.addInsteadOfRewire) {
                    target.removeEdge(e);
                }
                target.addEdge(v, other);
            }
        }
    }
}

