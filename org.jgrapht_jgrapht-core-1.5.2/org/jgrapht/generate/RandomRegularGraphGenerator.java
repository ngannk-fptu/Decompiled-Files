/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.EmptyGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.util.CollectionUtil;

public class RandomRegularGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final int n;
    private final int d;
    private final Random rng;

    public RandomRegularGraphGenerator(int n, int d) {
        this(n, d, new Random());
    }

    public RandomRegularGraphGenerator(int n, int d, long seed) {
        this(n, d, new Random(seed));
    }

    public RandomRegularGraphGenerator(int n, int d, Random rng) {
        if (n < 0) {
            throw new IllegalArgumentException("number of nodes must be non-negative");
        }
        if (d < 0) {
            throw new IllegalArgumentException("degree of nodes must be non-negative");
        }
        if (d > n) {
            throw new IllegalArgumentException("degree of nodes must be smaller than or equal to number of nodes");
        }
        if (n * d % 2 != 0) {
            throw new IllegalArgumentException("value 'n * d' must be even");
        }
        this.n = n;
        this.d = d;
        this.rng = rng;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (!target.getType().isUndirected()) {
            throw new IllegalArgumentException("target graph must be undirected");
        }
        if (target.getType().isSimple()) {
            if (this.n == 0 || this.d == 0) {
                new EmptyGraphGenerator<V, E>(this.n).generateGraph(target);
            } else {
                if (this.d == this.n) {
                    throw new IllegalArgumentException("target graph must be simple if 'n == d'");
                }
                if (this.d == this.n - 1) {
                    new CompleteGraphGenerator<V, E>(this.n).generateGraph(target);
                } else {
                    this.generateSimpleRegularGraph(target);
                }
            }
        } else {
            this.generateNonSimpleRegularGraph(target);
        }
    }

    private boolean suitable(Set<Map.Entry<Integer, Integer>> edges, Map<Integer, Integer> potentialEdges) {
        if (potentialEdges.isEmpty()) {
            return true;
        }
        Object[] keys = potentialEdges.keySet().toArray(new Integer[0]);
        Arrays.sort(keys);
        for (int i = 0; i < keys.length; ++i) {
            int s2 = (Integer)keys[i];
            for (int j = 0; j < i; ++j) {
                int s1 = (Integer)keys[j];
                AbstractMap.SimpleImmutableEntry<Integer, Integer> e = new AbstractMap.SimpleImmutableEntry<Integer, Integer>(s1, s2);
                if (edges.contains(e)) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    private void generateSimpleRegularGraph(Graph<V, E> target) {
        ArrayList vertices = new ArrayList(this.n);
        for (int i = 0; i < this.n; ++i) {
            vertices.add(target.addVertex());
        }
        HashSet<Map.Entry<Integer, Integer>> edges = CollectionUtil.newHashSetWithExpectedSize(this.n * this.d);
        block1: do {
            void var5_7;
            ArrayList<Integer> stubs = new ArrayList<Integer>(this.n * this.d);
            boolean bl = false;
            while (var5_7 < this.n * this.d) {
                stubs.add((int)(var5_7 % this.n));
                ++var5_7;
            }
            while (!stubs.isEmpty()) {
                HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
                Collections.shuffle(stubs, this.rng);
                for (int i = 0; i < stubs.size() - 1; i += 2) {
                    int s2;
                    int s1 = (Integer)stubs.get(i);
                    if (s1 > (s2 = ((Integer)stubs.get(i + 1)).intValue())) {
                        int temp = s1;
                        s1 = s2;
                        s2 = temp;
                    }
                    AbstractMap.SimpleImmutableEntry<Integer, Integer> edge = new AbstractMap.SimpleImmutableEntry<Integer, Integer>(s1, s2);
                    if (s1 != s2 && !edges.contains(edge)) {
                        edges.add(edge);
                        continue;
                    }
                    hashMap.put(s1, hashMap.getOrDefault(s1, 0) + 1);
                    hashMap.put(s2, hashMap.getOrDefault(s2, 0) + 1);
                }
                if (!this.suitable(edges, hashMap)) {
                    edges.clear();
                    continue block1;
                }
                stubs.clear();
                for (Map.Entry e : hashMap.entrySet()) {
                    int node = (Integer)e.getKey();
                    int potential = (Integer)e.getValue();
                    for (int i = 0; i < potential; ++i) {
                        stubs.add(node);
                    }
                }
            }
        } while (edges.isEmpty());
        for (Map.Entry entry : edges) {
            target.addEdge(vertices.get((Integer)entry.getKey()), vertices.get((Integer)entry.getValue()));
        }
    }

    private void generateNonSimpleRegularGraph(Graph<V, E> target) {
        int i;
        ArrayList<V> vertices = new ArrayList<V>(this.n * this.d);
        for (i = 0; i < this.n; ++i) {
            V vertex = target.addVertex();
            for (int j = 0; j < this.d; ++j) {
                vertices.add(vertex);
            }
        }
        Collections.shuffle(vertices, this.rng);
        for (i = 0; i < this.n * this.d / 2; ++i) {
            Object u = vertices.get(2 * i);
            Object v = vertices.get(2 * i + 1);
            target.addEdge(u, v);
        }
    }
}

