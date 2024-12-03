/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GraphGenerator;

public class BarabasiAlbertGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final int m0;
    private final int m;
    private final int n;

    public BarabasiAlbertGraphGenerator(int m0, int m, int n) {
        this(m0, m, n, new Random());
    }

    public BarabasiAlbertGraphGenerator(int m0, int m, int n, long seed) {
        this(m0, m, n, new Random(seed));
    }

    public BarabasiAlbertGraphGenerator(int m0, int m, int n, Random rng) {
        if (m0 < 1) {
            throw new IllegalArgumentException("invalid initial nodes (" + m0 + " < 1)");
        }
        this.m0 = m0;
        if (m <= 0) {
            throw new IllegalArgumentException("invalid edges per node (" + m + " <= 0");
        }
        if (m > m0) {
            throw new IllegalArgumentException("invalid edges per node (" + m + " > " + m0 + ")");
        }
        this.m = m;
        if (n < m0) {
            throw new IllegalArgumentException("total number of nodes must be at least equal to the initial set");
        }
        this.n = n;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int i;
        HashSet oldNodes = new HashSet(target.vertexSet());
        HashSet newNodes = new HashSet();
        new CompleteGraphGenerator<V, E>(this.m0).generateGraph(target, resultMap);
        target.vertexSet().stream().filter(v -> !oldNodes.contains(v)).forEach(newNodes::add);
        ArrayList nodes = new ArrayList(this.n * this.m);
        nodes.addAll(newNodes);
        for (i = 0; i < this.m0 - 2; ++i) {
            nodes.addAll(newNodes);
        }
        for (i = this.m0; i < this.n; ++i) {
            V v2 = target.addVertex();
            ArrayList<Object> newEndpoints = new ArrayList<Object>();
            int added = 0;
            while (added < this.m) {
                Object u = nodes.get(this.rng.nextInt(nodes.size()));
                if (target.containsEdge(v2, u)) continue;
                target.addEdge(v2, u);
                ++added;
                newEndpoints.add(v2);
                if (i <= 1) continue;
                newEndpoints.add(u);
            }
            nodes.addAll(newEndpoints);
        }
    }
}

