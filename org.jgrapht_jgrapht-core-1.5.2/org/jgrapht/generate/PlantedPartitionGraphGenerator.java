/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.util.CollectionUtil;

public class PlantedPartitionGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private static final boolean DEFAULT_ALLOW_SELFLOOPS = false;
    private final int l;
    private final int k;
    private final double p;
    private final double q;
    private final Random rng;
    private final boolean selfLoops;
    private boolean fired;
    private List<Set<V>> communities;

    public PlantedPartitionGraphGenerator(int l, int k, double p, double q) {
        this(l, k, p, q, new Random(), false);
    }

    public PlantedPartitionGraphGenerator(int l, int k, double p, double q, boolean selfLoops) {
        this(l, k, p, q, new Random(), selfLoops);
    }

    public PlantedPartitionGraphGenerator(int l, int k, double p, double q, long seed) {
        this(l, k, p, q, new Random(seed), false);
    }

    public PlantedPartitionGraphGenerator(int l, int k, double p, double q, long seed, boolean selfLoops) {
        this(l, k, p, q, new Random(seed), selfLoops);
    }

    public PlantedPartitionGraphGenerator(int l, int k, double p, double q, Random rng, boolean selfLoops) {
        if (l < 0) {
            throw new IllegalArgumentException("number of groups must be non-negative");
        }
        if (k < 0) {
            throw new IllegalArgumentException("number of nodes in each group must be non-negative");
        }
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("invalid probability p");
        }
        if (q < 0.0 || q > 1.0) {
            throw new IllegalArgumentException("invalid probability q");
        }
        this.l = l;
        this.k = k;
        this.p = p;
        this.q = q;
        this.rng = rng;
        this.selfLoops = selfLoops;
        this.fired = false;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int j;
        int i;
        if (this.fired) {
            throw new IllegalStateException("generateGraph() can be only called once");
        }
        this.fired = true;
        this.communities = new ArrayList<Set<V>>(this.l);
        for (int i2 = 0; i2 < this.l; ++i2) {
            this.communities.add(CollectionUtil.newLinkedHashSetWithExpectedSize(this.k));
        }
        if (this.l == 0 || this.k == 0) {
            return;
        }
        int n = this.k * this.l;
        ArrayList<V> vertices = new ArrayList<V>(n);
        for (i = 0; i < n; ++i) {
            V vertex = target.addVertex();
            vertices.add(vertex);
            int lv = i / this.k;
            this.communities.get(lv).add(vertex);
        }
        if (this.selfLoops) {
            if (target.getType().isAllowingSelfLoops()) {
                for (Object v : vertices) {
                    if (!(this.rng.nextDouble() < this.p)) continue;
                    target.addEdge(v, v);
                }
            } else {
                throw new IllegalArgumentException("target graph must allow self-loops");
            }
        }
        if (target.getType().isUndirected()) {
            for (i = 0; i < n; ++i) {
                int li = i / this.k;
                for (j = i + 1; j < n; ++j) {
                    int lj = j / this.k;
                    if (li == lj) {
                        if (!(this.rng.nextDouble() < this.p)) continue;
                        target.addEdge(vertices.get(i), vertices.get(j));
                        continue;
                    }
                    if (!(this.rng.nextDouble() < this.q)) continue;
                    target.addEdge(vertices.get(i), vertices.get(j));
                }
            }
        } else {
            for (i = 0; i < n; ++i) {
                int li = i / this.k;
                for (j = i + 1; j < n; ++j) {
                    int lj = j / this.k;
                    if (li == lj) {
                        if (this.rng.nextDouble() < this.p) {
                            target.addEdge(vertices.get(i), vertices.get(j));
                        }
                        if (!(this.rng.nextDouble() < this.p)) continue;
                        target.addEdge(vertices.get(j), vertices.get(i));
                        continue;
                    }
                    if (this.rng.nextDouble() < this.q) {
                        target.addEdge(vertices.get(i), vertices.get(j));
                    }
                    if (!(this.rng.nextDouble() < this.q)) continue;
                    target.addEdge(vertices.get(j), vertices.get(i));
                }
            }
        }
    }

    public List<Set<V>> getCommunities() {
        if (this.communities == null) {
            throw new IllegalStateException("must generate graph before getting community structure");
        }
        return this.communities;
    }
}

