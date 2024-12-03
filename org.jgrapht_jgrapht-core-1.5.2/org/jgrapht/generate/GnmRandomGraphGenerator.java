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

public class GnmRandomGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private static final boolean DEFAULT_ALLOW_LOOPS = false;
    private static final boolean DEFAULT_ALLOW_MULTIPLE_EDGES = false;
    private final Random rng;
    private final int n;
    private final int m;
    private final boolean loops;
    private final boolean multipleEdges;

    public GnmRandomGraphGenerator(int n, int m) {
        this(n, m, new Random(), false, false);
    }

    public GnmRandomGraphGenerator(int n, int m, long seed) {
        this(n, m, new Random(seed), false, false);
    }

    public GnmRandomGraphGenerator(int n, int m, long seed, boolean loops, boolean multipleEdges) {
        this(n, m, new Random(seed), loops, multipleEdges);
    }

    public GnmRandomGraphGenerator(int n, int m, Random rng, boolean loops, boolean multipleEdges) {
        if (n < 0) {
            throw new IllegalArgumentException("number of vertices must be non-negative");
        }
        this.n = n;
        if (m < 0) {
            throw new IllegalArgumentException("number of edges must be non-negative");
        }
        this.m = m;
        this.rng = Objects.requireNonNull(rng);
        this.loops = loops;
        this.multipleEdges = multipleEdges;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.n == 0) {
            return;
        }
        if (this.loops && !target.getType().isAllowingSelfLoops()) {
            throw new IllegalArgumentException("Provided graph does not support self-loops");
        }
        if (this.multipleEdges && !target.getType().isAllowingMultipleEdges()) {
            throw new IllegalArgumentException("Provided graph does not support multiple edges between the same vertices");
        }
        if (this.m > GnmRandomGraphGenerator.computeMaximumAllowedEdges(this.n, target.getType().isDirected(), this.loops, this.multipleEdges)) {
            throw new IllegalArgumentException("number of edges is not valid for the graph type \n-> invalid number of edges=" + this.m + " for: graph type=" + target.getType() + ", number of vertices=" + this.n);
        }
        ArrayList<V> vertices = new ArrayList<V>(this.n);
        int previousVertexSetSize = target.vertexSet().size();
        for (int i = 0; i < this.n; ++i) {
            vertices.add(target.addVertex());
        }
        if (target.vertexSet().size() != previousVertexSetSize + this.n) {
            throw new IllegalArgumentException("Vertex factory did not produce " + this.n + " distinct vertices.");
        }
        int edgesCounter = 0;
        while (edgesCounter < this.m) {
            int sIndex = this.rng.nextInt(this.n);
            int tIndex = this.rng.nextInt(this.n);
            Object s = null;
            Object t = null;
            boolean addEdge = false;
            if (sIndex == tIndex) {
                if (this.loops) {
                    addEdge = true;
                }
            } else if (this.multipleEdges) {
                addEdge = true;
            } else {
                s = vertices.get(sIndex);
                if (!target.containsEdge(s, t = (Object)vertices.get(tIndex))) {
                    addEdge = true;
                }
            }
            if (!addEdge) continue;
            try {
                E resultEdge;
                if (s == null) {
                    s = vertices.get(sIndex);
                    t = vertices.get(tIndex);
                }
                if ((resultEdge = target.addEdge(s, t)) == null) continue;
                ++edgesCounter;
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
    }

    static int computeMaximumAllowedEdges(int n, boolean isDirected, boolean createLoops, boolean createMultipleEdges) {
        int maxAllowedEdges;
        if (n == 0) {
            return 0;
        }
        try {
            maxAllowedEdges = isDirected ? Math.multiplyExact(n, n - 1) : (n % 2 == 0 ? Math.multiplyExact(n / 2, n - 1) : Math.multiplyExact(n, (n - 1) / 2));
            if (createLoops) {
                if (createMultipleEdges) {
                    return Integer.MAX_VALUE;
                }
                maxAllowedEdges = isDirected ? Math.addExact(maxAllowedEdges, Math.multiplyExact(2, n)) : Math.addExact(maxAllowedEdges, n);
            } else if (createMultipleEdges && n > 1) {
                return Integer.MAX_VALUE;
            }
        }
        catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
        return maxAllowedEdges;
    }
}

