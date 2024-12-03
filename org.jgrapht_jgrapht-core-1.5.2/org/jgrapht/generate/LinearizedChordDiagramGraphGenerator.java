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

public class LinearizedChordDiagramGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final int m;
    private final int n;

    public LinearizedChordDiagramGraphGenerator(int n, int m) {
        this(n, m, new Random());
    }

    public LinearizedChordDiagramGraphGenerator(int n, int m, long seed) {
        this(n, m, new Random(seed));
    }

    public LinearizedChordDiagramGraphGenerator(int n, int m, Random rng) {
        if (n <= 0) {
            throw new IllegalArgumentException("invalid number of nodes: must be positive");
        }
        this.n = n;
        if (m <= 0) {
            throw new IllegalArgumentException("invalid edges per node (" + m + " <= 0");
        }
        this.m = m;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        ArrayList<Object> nodes = new ArrayList<Object>(2 * this.n * this.m);
        for (int t = 0; t < this.n; ++t) {
            V vt = target.addVertex();
            for (int j = 0; j < this.m; ++j) {
                nodes.add(vt);
                Object vs = nodes.get(this.rng.nextInt(nodes.size()));
                if (target.addEdge(vt, vs) == null) {
                    throw new IllegalArgumentException("Graph does not permit parallel-edges.");
                }
                nodes.add(vs);
            }
        }
    }
}

