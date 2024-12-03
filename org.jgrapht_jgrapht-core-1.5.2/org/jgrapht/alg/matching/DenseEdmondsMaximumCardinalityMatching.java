/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.GreedyMaximumCardinalityMatching;
import org.jgrapht.alg.util.FixedSizeIntegerQueue;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.UnionFind;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.util.CollectionUtil;

public class DenseEdmondsMaximumCardinalityMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private final MatchingAlgorithm<V, E> initializer;
    private List<V> vertices;
    private Map<V, Integer> vertexIndexMap;
    private SimpleMatching matching;
    private int matchedVertices;
    private Levels levels;
    private static final int NIL = -1;
    private FixedSizeIntegerQueue queue;
    private UnionFind<Integer> uf;
    private final Map<Integer, Pair<Integer, Integer>> bridges = new HashMap<Integer, Pair<Integer, Integer>>();
    private int[] path;
    private BitSet vAncestors;
    private BitSet wAncestors;

    public DenseEdmondsMaximumCardinalityMatching(Graph<V, E> graph) {
        this(graph, new GreedyMaximumCardinalityMatching<V, E>(graph, false));
    }

    public DenseEdmondsMaximumCardinalityMatching(Graph<V, E> graph, MatchingAlgorithm<V, E> initializer) {
        this.graph = GraphTests.requireUndirected(graph);
        this.initializer = initializer;
    }

    private void init() {
        this.vertices = new ArrayList<V>();
        this.vertices.addAll(this.graph.vertexSet());
        this.vertexIndexMap = new HashMap<V, Integer>();
        for (int i = 0; i < this.vertices.size(); ++i) {
            this.vertexIndexMap.put((Integer)this.vertices.get(i), i);
        }
        this.matching = new SimpleMatching(this.vertices.size());
        this.matchedVertices = 0;
        this.levels = new Levels(this.vertices.size());
        this.queue = new FixedSizeIntegerQueue(this.vertices.size());
        this.uf = new UnionFind<Integer>(new HashSet<Integer>(this.vertexIndexMap.values()));
        this.path = new int[this.vertices.size()];
        this.vAncestors = new BitSet(this.vertices.size());
        this.wAncestors = new BitSet(this.vertices.size());
    }

    private void warmStart(MatchingAlgorithm<V, E> initializer) {
        MatchingAlgorithm.Matching<V, E> initialSolution = initializer.getMatching();
        for (E e : initialSolution.getEdges()) {
            V u = this.graph.getEdgeSource(e);
            V v = this.graph.getEdgeTarget(e);
            this.matching.match(this.vertexIndexMap.get(u), this.vertexIndexMap.get(v));
        }
        this.matchedVertices = initialSolution.getEdges().size() * 2;
    }

    private boolean augment() {
        this.levels.reset();
        this.uf.reset();
        this.bridges.clear();
        this.queue.clear();
        ArrayDeque<Integer> exposed = new ArrayDeque<Integer>(this.matching.getExposed());
        while (!exposed.isEmpty()) {
            int root = (Integer)exposed.pop();
            this.levels.setEven(root, root);
            this.queue.enqueue(root);
            while (!this.queue.isEmpty()) {
                int v = this.queue.poll();
                for (V wOrig : Graphs.neighborListOf(this.graph, this.vertices.get(v))) {
                    int w = this.vertexIndexMap.get(wOrig);
                    if (this.levels.isEven(this.uf.find(w))) {
                        if (this.uf.inSameSet(v, w)) continue;
                        this.blossom(v, w);
                        continue;
                    }
                    if (!this.levels.isOddOrUnreached(w)) continue;
                    if (this.matching.isExposed(w)) {
                        this.augment(v);
                        this.augment(w);
                        this.matching.match(v, w);
                        return true;
                    }
                    this.levels.setOdd(w, v);
                    int u = this.matching.opposite(w);
                    this.levels.setEven(u, w);
                    this.queue.enqueue(u);
                }
            }
        }
        return false;
    }

    private void blossom(int v, int w) {
        int base = this.nearestCommonAncestor(v, w);
        this.blossomSupports(v, w, base);
        this.blossomSupports(w, v, base);
        this.uf.union(v, base);
        this.uf.union(w, base);
        this.levels.setEven(this.uf.find(base), this.levels.getEven(base));
    }

    private void blossomSupports(int v, int w, int base) {
        Pair<Integer, Integer> bridge = new Pair<Integer, Integer>(v, w);
        int u = v = this.uf.find(v).intValue();
        while (v != base) {
            this.uf.union(v, u);
            u = this.levels.getEven(v);
            this.bridges.put(u, bridge);
            this.queue.enqueue(u);
            this.uf.union(v, u);
            v = this.uf.find(this.levels.getOdd(u));
        }
    }

    private int nearestCommonAncestor(int v, int w) {
        this.vAncestors.clear();
        this.vAncestors.set(this.uf.find(v));
        this.wAncestors.clear();
        this.wAncestors.set(this.uf.find(w));
        do {
            v = this.parent(v);
            this.vAncestors.set(v);
            w = this.parent(w);
            this.wAncestors.set(w);
            if (!this.wAncestors.get(v)) continue;
            return v;
        } while (!this.vAncestors.get(w));
        return w;
    }

    private int parent(int v) {
        int parent = this.uf.find(this.levels.getEven(v = this.uf.find(v).intValue()));
        if (parent == v) {
            return v;
        }
        return this.uf.find(this.levels.getOdd(parent));
    }

    private void augment(int v) {
        int n = this.buildPath(this.path, 0, v, -1);
        for (int i = 2; i < n; i += 2) {
            this.matching.match(this.path[i], this.path[i - 1]);
        }
    }

    private int buildPath(int[] path, int i, int start, int end) {
        while (true) {
            if (this.levels.isOdd(start)) {
                Pair<Integer, Integer> bridge = this.bridges.get(start);
                int j = this.buildPath(path, i, bridge.getFirst(), start);
                this.reverse(path, i, j - 1);
                i = j;
                start = bridge.getSecond();
                continue;
            }
            path[i++] = start;
            if (this.matching.isExposed(start)) {
                return i;
            }
            path[i++] = this.matching.opposite(start);
            if (path[i - 1] == end) {
                return i;
            }
            start = this.levels.getOdd(path[i - 1]);
        }
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        this.init();
        if (this.initializer != null) {
            this.warmStart(this.initializer);
        }
        while (this.matchedVertices < this.graph.vertexSet().size() - 1 && this.augment()) {
            this.matchedVertices += 2;
        }
        LinkedHashSet<E> edges = new LinkedHashSet<E>();
        double cost = 0.0;
        for (int vx = 0; vx < this.vertices.size(); ++vx) {
            if (this.matching.isExposed(vx)) continue;
            V v = this.vertices.get(vx);
            V w = this.vertices.get(this.matching.opposite(vx));
            E edge = this.graph.getEdge(v, w);
            edges.add(edge);
            cost += 0.5 * this.graph.getEdgeWeight(edge);
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, edges, cost);
    }

    public boolean isMaximumMatching(MatchingAlgorithm.Matching<V, E> matching) {
        if (matching.getEdges().size() * 2 >= this.graph.vertexSet().size() - 1) {
            return true;
        }
        this.init();
        for (E e : matching.getEdges()) {
            V u = this.graph.getEdgeSource(e);
            V v2 = this.graph.getEdgeTarget(e);
            Integer ux = this.vertexIndexMap.get(u);
            Integer vx2 = this.vertexIndexMap.get(v2);
            this.matching.match(ux, vx2);
        }
        if (this.augment()) {
            return false;
        }
        Set oddVertices = this.vertexIndexMap.values().stream().filter(vx -> this.levels.isOdd((int)vx) && !this.bridges.containsKey(vx)).map(this.vertices::get).collect(Collectors.toSet());
        Set otherVertices = this.graph.vertexSet().stream().filter(v -> !oddVertices.contains(v)).collect(Collectors.toSet());
        AsSubgraph<V, E> subgraph = new AsSubgraph<V, E>(this.graph, otherVertices, null);
        List<Set<V>> connectedComponents = new ConnectivityInspector<V, E>(subgraph).connectedSets();
        long nrOddCardinalityComponents = connectedComponents.stream().filter(s -> s.size() % 2 == 1).count();
        return (double)matching.getEdges().size() == (double)((long)(this.graph.vertexSet().size() + oddVertices.size()) - nrOddCardinalityComponents) / 2.0;
    }

    private void reverse(int[] path, int i, int j) {
        while (i < j) {
            int tmp = path[i];
            path[i] = path[j];
            path[j] = tmp;
            ++i;
            --j;
        }
    }

    private static class SimpleMatching {
        private static final int UNMATCHED = -1;
        private final int[] match;
        private Set<Integer> exposed;

        private SimpleMatching(int n) {
            this.match = new int[n];
            this.exposed = CollectionUtil.newHashSetWithExpectedSize(n);
            Arrays.fill(this.match, -1);
            IntStream.range(0, n).forEach(this.exposed::add);
        }

        boolean isMatched(int v) {
            return this.match[v] != -1;
        }

        boolean isExposed(int v) {
            return this.match[v] == -1;
        }

        int opposite(int v) {
            assert (this.isMatched(v));
            return this.match[v];
        }

        void match(int u, int v) {
            this.match[u] = v;
            this.match[v] = u;
            this.exposed.remove(u);
            this.exposed.remove(v);
        }

        Set<Integer> getExposed() {
            return this.exposed;
        }
    }

    private static class Levels {
        private int[] even;
        private int[] odd;
        private List<Integer> dirty;

        public Levels(int n) {
            this.even = new int[n];
            this.odd = new int[n];
            this.dirty = new ArrayList<Integer>();
            Arrays.fill(this.even, -1);
            Arrays.fill(this.odd, -1);
        }

        public int getEven(int v) {
            return this.even[v];
        }

        public void setEven(int v, int value) {
            this.even[v] = value;
            if (value != -1) {
                this.dirty.add(v);
            }
        }

        public int getOdd(int v) {
            return this.odd[v];
        }

        public void setOdd(int v, int value) {
            this.odd[v] = value;
            if (value != -1) {
                this.dirty.add(v);
            }
        }

        public boolean isEven(int v) {
            return this.even[v] != -1;
        }

        public boolean isOddOrUnreached(int v) {
            return this.odd[v] == -1;
        }

        public boolean isOdd(int v) {
            return this.odd[v] != -1;
        }

        public void reset() {
            for (int v : this.dirty) {
                this.even[v] = -1;
                this.odd[v] = -1;
            }
            this.dirty.clear();
        }
    }
}

