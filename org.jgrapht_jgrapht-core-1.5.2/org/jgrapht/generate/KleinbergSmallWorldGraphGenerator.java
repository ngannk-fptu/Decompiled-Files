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
import org.jgrapht.alg.util.AliasMethodSampler;
import org.jgrapht.generate.GraphGenerator;

public class KleinbergSmallWorldGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final int n;
    private final int p;
    private final int q;
    private final int r;

    public KleinbergSmallWorldGraphGenerator(int n, int p, int q, int r) {
        this(n, p, q, r, new Random());
    }

    public KleinbergSmallWorldGraphGenerator(int n, int p, int q, int r, long seed) {
        this(n, p, q, r, new Random(seed));
    }

    public KleinbergSmallWorldGraphGenerator(int n, int p, int q, int r, Random rng) {
        if (n < 1) {
            throw new IllegalArgumentException("parameter n must be positive");
        }
        this.n = n;
        if (p < 1) {
            throw new IllegalArgumentException("parameter p must be positive");
        }
        if (p > 2 * n - 2) {
            throw new IllegalArgumentException("lattice distance too large");
        }
        this.p = p;
        if (q < 0) {
            throw new IllegalArgumentException("parameter q must be non-negative");
        }
        this.q = q;
        if (r < 0) {
            throw new IllegalArgumentException("parameter r must be non-negative");
        }
        this.r = r;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        Object v;
        int i;
        if (this.n == 0) {
            return;
        }
        if (this.n == 1) {
            target.addVertex();
            return;
        }
        GraphTests.requireDirectedOrUndirected(target);
        boolean isDirected = target.getType().isDirected();
        ArrayList<V> nodes = new ArrayList<V>(this.n * this.n);
        for (i = 0; i < this.n * this.n; ++i) {
            nodes.add(target.addVertex());
        }
        for (i = 0; i < this.n; ++i) {
            for (int j = 0; j < this.n; ++j) {
                int vi = i * this.n + j;
                v = nodes.get(vi);
                for (int di = -this.p; di <= this.p; ++di) {
                    for (int dj = -this.p; dj <= this.p; ++dj) {
                        int t = (i + di) * this.n + (j + dj);
                        if (t < 0 || t == vi || t >= this.n * this.n || Math.abs(di) + Math.abs(dj) > this.p || !isDirected && t <= i * this.n + j) continue;
                        target.addEdge(v, nodes.get(t));
                    }
                }
            }
        }
        double[] p = new double[this.n * this.n];
        for (int i2 = 0; i2 < this.n; ++i2) {
            for (int j = 0; j < this.n; ++j) {
                v = nodes.get(i2 * this.n + j);
                double sum = 0.0;
                for (int oi = 0; oi < this.n; ++oi) {
                    for (int oj = 0; oj < this.n; ++oj) {
                        double weight;
                        if (oi == i2 && oj == j) continue;
                        p[oi * this.n + oj] = weight = Math.pow(Math.abs(i2 - oi) + Math.abs(j - oj), -this.r);
                        sum += weight;
                    }
                }
                p[i2 * this.n + j] = 0.0;
                int k = 0;
                while (k < this.n * this.n) {
                    int n = k++;
                    p[n] = p[n] / sum;
                }
                AliasMethodSampler sampler = new AliasMethodSampler(p, this.rng);
                for (int k2 = 0; k2 < this.q; ++k2) {
                    Object u = nodes.get(sampler.next());
                    if (u.equals(v) || target.containsEdge(v, u)) continue;
                    target.addEdge(v, u);
                }
            }
        }
    }
}

