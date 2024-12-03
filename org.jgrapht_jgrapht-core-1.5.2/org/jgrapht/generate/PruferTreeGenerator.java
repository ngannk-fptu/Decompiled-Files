/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.generate.GraphGenerator;

public class PruferTreeGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final int n;
    private final Random rng;
    private final int[] inputPruferSeq;

    public PruferTreeGenerator(int[] pruferSequence) {
        if (Objects.isNull(pruferSequence)) {
            throw new IllegalArgumentException("pruferSequence cannot be null");
        }
        this.n = pruferSequence.length + 2;
        this.rng = null;
        this.inputPruferSeq = (int[])pruferSequence.clone();
        if (this.n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        for (int i = 0; i < this.n - 2; ++i) {
            if (pruferSequence[i] >= 0 && pruferSequence[i] < this.n) continue;
            throw new IllegalArgumentException("invalid pruferSequence");
        }
    }

    public PruferTreeGenerator(int n) {
        this(n, new Random());
    }

    public PruferTreeGenerator(int n, long seed) {
        this(n, new Random(seed));
    }

    public PruferTreeGenerator(int n, Random rng) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        this.n = n;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
        this.inputPruferSeq = null;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int[] pruferSeq;
        GraphTests.requireUndirected(target);
        if (!target.vertexSet().isEmpty()) {
            throw new IllegalArgumentException("target graph is not empty");
        }
        ArrayList<V> vertexList = new ArrayList<V>(this.n);
        for (int i = 0; i < this.n; ++i) {
            vertexList.add(target.addVertex());
        }
        if (this.n == 1) {
            return;
        }
        int[] degree = new int[this.n];
        Arrays.fill(degree, 1);
        if (this.inputPruferSeq == null) {
            pruferSeq = new int[this.n - 2];
            for (int i = 0; i < this.n - 2; ++i) {
                pruferSeq[i] = this.rng.nextInt(this.n);
                int n = pruferSeq[i];
                degree[n] = degree[n] + 1;
            }
        } else {
            pruferSeq = this.inputPruferSeq;
        }
        int index = -1;
        for (int k = 0; k < this.n; ++k) {
            if (degree[k] != 1) continue;
            index = k;
            break;
        }
        assert (index != -1);
        int x = index;
        HashSet<V> orphans = new HashSet<V>(target.vertexSet());
        block3: for (int i = 0; i < this.n - 2; ++i) {
            int y = pruferSeq[i];
            orphans.remove(vertexList.get(x));
            target.addEdge(vertexList.get(x), vertexList.get(y));
            int n = y;
            degree[n] = degree[n] - 1;
            if (y < index && degree[y] == 1) {
                x = y;
                continue;
            }
            for (int k = index + 1; k < this.n; ++k) {
                if (degree[k] != 1) continue;
                index = x = k;
                continue block3;
            }
        }
        assert (orphans.size() == 2);
        Iterator iterator = orphans.iterator();
        Object u = iterator.next();
        Object v = iterator.next();
        target.addEdge(u, v);
    }
}

