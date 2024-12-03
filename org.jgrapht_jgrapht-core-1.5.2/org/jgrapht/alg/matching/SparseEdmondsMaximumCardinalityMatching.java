/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.GreedyMaximumCardinalityMatching;
import org.jgrapht.alg.util.FixedSizeIntegerQueue;
import org.jgrapht.util.CollectionUtil;

public class SparseEdmondsMaximumCardinalityMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private MatchingAlgorithm<V, E> initializer;
    private MatchingAlgorithm.Matching<V, E> result;
    private Map<V, Integer> oddSetCover;

    public SparseEdmondsMaximumCardinalityMatching(Graph<V, E> graph) {
        this(graph, new GreedyMaximumCardinalityMatching<V, E>(graph, false));
    }

    public SparseEdmondsMaximumCardinalityMatching(Graph<V, E> graph, MatchingAlgorithm<V, E> initializer) {
        this.graph = GraphTests.requireUndirected(graph);
        this.initializer = initializer;
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        if (this.result == null) {
            Algorithm<V, E> alg = new Algorithm<V, E>(this.graph, this.initializer);
            Set<E> matchingEdges = alg.computeMatching();
            int cardinality = matchingEdges.size();
            this.result = new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, matchingEdges, cardinality);
            this.oddSetCover = alg.computeOddSetCover();
        }
        return this.result;
    }

    public Map<V, Integer> getOddSetCover() {
        this.getMatching();
        return this.oddSetCover;
    }

    public static <V, E> boolean isOptimalMatching(Graph<V, E> graph, Set<E> matching, Map<V, Integer> oddSetCover) {
        HashSet<V> matched = new HashSet<V>();
        for (E e : matching) {
            V s = graph.getEdgeSource(e);
            if (!matched.add(s)) {
                return false;
            }
            V t = graph.getEdgeTarget(e);
            if (matched.add(t)) continue;
            return false;
        }
        int n = Math.max(2, graph.vertexSet().size());
        int kappa = 1;
        int[] count = new int[n];
        for (int i = 0; i < n; ++i) {
            count[i] = 0;
        }
        for (V v : graph.vertexSet()) {
            Integer osc = oddSetCover.get(v);
            if (osc < 0 || osc >= n) {
                return false;
            }
            int n2 = osc;
            count[n2] = count[n2] + 1;
            if (osc <= kappa) continue;
            kappa = osc;
        }
        int s = count[1];
        for (int i = 2; i <= kappa; ++i) {
            s += count[i] / 2;
        }
        if (s != matching.size()) {
            return false;
        }
        for (E e : graph.edgeSet()) {
            V v = graph.getEdgeSource(e);
            V w = graph.getEdgeTarget(e);
            int oscv = oddSetCover.get(v);
            int oscw = oddSetCover.get(w);
            if (v.equals(w) || oscv == 1 || oscw == 1 || oscv == oscw && oscv >= 2) continue;
            return false;
        }
        return true;
    }

    private static class Algorithm<V, E> {
        private static final int NULL = -1;
        private final Graph<V, E> graph;
        private MatchingAlgorithm<V, E> initializer;
        private int nodes;
        private Map<V, Integer> vertexIndexMap;
        private V[] vertexMap;
        private int[] mate;
        private Label[] label;
        private int[] pred;
        double strue;
        private double[] path1;
        private double[] path2;
        private int[] sourceBridge;
        private int[] targetBridge;
        private VertexPartition base;
        private FixedSizeIntegerQueue queue;
        private List<Integer> labeledNodes;

        public Algorithm(Graph<V, E> graph, MatchingAlgorithm<V, E> initializer) {
            this.graph = graph;
            this.initializer = initializer;
        }

        private void initialize() {
            this.nodes = this.graph.vertexSet().size();
            this.vertexIndexMap = CollectionUtil.newHashMapWithExpectedSize(this.nodes);
            this.vertexMap = new Object[this.nodes];
            int vIndex = 0;
            for (V vertex : this.graph.vertexSet()) {
                this.vertexIndexMap.put((Integer)vertex, vIndex);
                this.vertexMap[vIndex] = vertex;
                ++vIndex;
            }
            this.mate = new int[this.nodes];
            this.base = new VertexPartition(this.nodes);
            this.label = new Label[this.nodes];
            this.pred = new int[this.nodes];
            this.path1 = new double[this.nodes];
            this.path2 = new double[this.nodes];
            this.sourceBridge = new int[this.nodes];
            this.targetBridge = new int[this.nodes];
            for (int i = 0; i < this.nodes; ++i) {
                this.mate[i] = -1;
                this.label[i] = Label.EVEN;
                this.pred[i] = -1;
                this.path1[i] = 0.0;
                this.path2[i] = 0.0;
                this.sourceBridge[i] = -1;
                this.targetBridge[i] = -1;
            }
            this.strue = 0.0;
            this.queue = new FixedSizeIntegerQueue(this.nodes);
            this.labeledNodes = new ArrayList<Integer>();
        }

        private void runInitializer() {
            if (this.initializer == null) {
                return;
            }
            for (E e : this.initializer.getMatching()) {
                int vIndex;
                V u = this.graph.getEdgeSource(e);
                V v = this.graph.getEdgeTarget(e);
                int uIndex = this.vertexIndexMap.get(u);
                this.mate[uIndex] = vIndex = this.vertexIndexMap.get(v).intValue();
                this.label[uIndex] = Label.UNLABELED;
                this.mate[vIndex] = uIndex;
                this.label[vIndex] = Label.UNLABELED;
            }
        }

        private void findPath(Deque<Integer> p, int x, int y) {
            if (x == y) {
                p.add(x);
                return;
            }
            if (this.label[x] == Label.EVEN) {
                p.add(x);
                p.add(this.mate[x]);
                this.findPath(p, this.pred[this.mate[x]], y);
                return;
            }
            p.add(x);
            ArrayDeque<Integer> p2 = new ArrayDeque<Integer>();
            this.findPath(p2, this.sourceBridge[x], this.mate[x]);
            while (!p2.isEmpty()) {
                p.add((Integer)p2.removeLast());
            }
            this.findPath(p, this.targetBridge[x], y);
        }

        private void shrinkPath(int b, int v, int w) {
            int x = this.base.find(v);
            while (x != b) {
                this.base.union(x, b);
                x = this.mate[x];
                this.base.union(x, b);
                this.base.name(b);
                this.queue.enqueue(x);
                this.sourceBridge[x] = v;
                this.targetBridge[x] = w;
                x = this.base.find(this.pred[x]);
            }
        }

        public Set<E> computeMatching() {
            this.initialize();
            this.runInitializer();
            for (int i = 0; i < this.nodes; ++i) {
                if (this.mate[i] != -1) continue;
                this.queue.clear();
                this.queue.enqueue(i);
                this.labeledNodes.clear();
                this.labeledNodes.add(i);
                boolean breakThrough = false;
                block1: while (!breakThrough && !this.queue.isEmpty()) {
                    int v = this.queue.poll();
                    V vAsVertex = this.vertexMap[v];
                    for (E e : this.graph.edgesOf(vAsVertex)) {
                        V wAsVertex = Graphs.getOppositeVertex(this.graph, e, vAsVertex);
                        int w = this.vertexIndexMap.get(wAsVertex);
                        if (this.base.find(v) == this.base.find(w) || this.label[this.base.find(w)] == Label.ODD) continue;
                        if (this.label[w] == Label.UNLABELED) {
                            this.label[w] = Label.ODD;
                            this.labeledNodes.add(w);
                            this.pred[w] = v;
                            this.label[this.mate[w]] = Label.EVEN;
                            this.labeledNodes.add(this.mate[w]);
                            this.queue.enqueue(this.mate[w]);
                            continue;
                        }
                        int hv = this.base.find(v);
                        int hw = this.base.find(w);
                        this.strue += 1.0;
                        this.path1[hv] = this.strue;
                        this.path2[hw] = this.strue;
                        while (this.path1[hw] != this.strue && this.path2[hv] != this.strue && (this.mate[hv] != -1 || this.mate[hw] != -1)) {
                            if (this.mate[hv] != -1) {
                                hv = this.base.find(this.pred[this.mate[hv]]);
                                this.path1[hv] = this.strue;
                            }
                            if (this.mate[hw] == -1) continue;
                            hw = this.base.find(this.pred[this.mate[hw]]);
                            this.path2[hw] = this.strue;
                        }
                        if (this.path1[hw] == this.strue || this.path2[hv] == this.strue) {
                            int b = this.path1[hw] == this.strue ? hw : hv;
                            this.shrinkPath(b, v, w);
                            this.shrinkPath(b, w, v);
                            continue;
                        }
                        ArrayDeque<Integer> p = new ArrayDeque<Integer>();
                        this.findPath(p, v, hv);
                        p.addFirst(w);
                        while (!p.isEmpty()) {
                            int b;
                            int a = (Integer)p.pop();
                            this.mate[a] = b = ((Integer)p.pop()).intValue();
                            this.mate[b] = a;
                        }
                        this.labeledNodes.add(w);
                        for (Integer k : this.labeledNodes) {
                            this.label[k.intValue()] = Label.UNLABELED;
                        }
                        this.base.split(this.labeledNodes);
                        breakThrough = true;
                        continue block1;
                    }
                }
            }
            HashSet<E> matching = new HashSet<E>();
            for (E e : this.graph.edgeSet()) {
                int vIndex;
                int uIndex;
                V v;
                V u = this.graph.getEdgeSource(e);
                if (u.equals(v = this.graph.getEdgeTarget(e)) || (uIndex = this.vertexIndexMap.get(u).intValue()) == (vIndex = this.vertexIndexMap.get(v).intValue()) || this.mate[uIndex] != vIndex) continue;
                matching.add(e);
                this.mate[uIndex] = uIndex;
                this.mate[vIndex] = vIndex;
            }
            return matching;
        }

        public Map<V, Integer> computeOddSetCover() {
            int v;
            int[] osc = new int[this.nodes];
            Arrays.fill(osc, -1);
            int numberOfUnlabeled = 0;
            int arbUNode = -1;
            for (int v2 = 0; v2 < this.nodes; ++v2) {
                if (this.label[v2] != Label.UNLABELED) continue;
                ++numberOfUnlabeled;
                arbUNode = v2;
            }
            if (numberOfUnlabeled > 0) {
                osc[arbUNode] = 1;
                int lambda = numberOfUnlabeled == 2 ? 0 : 2;
                for (v = 0; v < this.nodes; ++v) {
                    if (this.label[v] != Label.UNLABELED || v == arbUNode) continue;
                    osc[v] = lambda;
                }
            }
            int kappa = numberOfUnlabeled <= 2 ? 2 : 3;
            for (v = 0; v < this.nodes; ++v) {
                if (this.base.find(v) == v || osc[this.base.find(v)] != -1) continue;
                osc[this.base.find((int)v)] = kappa++;
            }
            for (v = 0; v < this.nodes; ++v) {
                if (this.base.find(v) == v && osc[v] == -1) {
                    if (this.label[v] == Label.EVEN) {
                        osc[v] = 0;
                    }
                    if (this.label[v] == Label.ODD) {
                        osc[v] = 1;
                    }
                }
                if (this.base.find(v) == v) continue;
                osc[v] = osc[this.base.find(v)];
            }
            HashMap<V, Integer> oddSetCover = new HashMap<V, Integer>();
            for (int v3 = 0; v3 < this.nodes; ++v3) {
                oddSetCover.put(this.vertexMap[v3], osc[v3]);
            }
            return oddSetCover;
        }

        private static enum Label {
            EVEN,
            ODD,
            UNLABELED;

        }
    }

    private static class VertexPartition {
        private Item[] items;

        public VertexPartition(int n) {
            this.items = new Item[n];
            for (int i = 0; i < n; ++i) {
                this.items[i] = new Item(i);
            }
        }

        public int find(int e) {
            return this.findItem((int)e).rep;
        }

        public void union(int a, int b) {
            Item ib;
            assert (a >= 0 && a < this.items.length);
            assert (b >= 0 && b < this.items.length);
            Item ia = this.findItem(a);
            if (ia == (ib = this.findItem(b))) {
                return;
            }
            if (ia.rank > ib.rank) {
                ib.parent = ia;
            } else if (ia.rank < ib.rank) {
                ia.parent = ib;
            } else {
                ib.parent = ia;
                ++ia.rank;
            }
        }

        public void name(int e) {
            Item ie = this.findItem(e);
            ie.rep = e;
        }

        public void split(List<Integer> toSplit) {
            for (int i : toSplit) {
                Item item;
                item.parent = item = this.items[i];
                item.rep = i;
                item.rank = 0;
            }
        }

        private Item findItem(int e) {
            Item parent;
            assert (e >= 0 && e < this.items.length);
            Item current = this.items[e];
            while (!(parent = current.parent).equals(current)) {
                current = parent;
            }
            Item root = current;
            current = this.items[e];
            while (!current.equals(root)) {
                Item parent2 = current.parent;
                current.parent = root;
                current = parent2;
            }
            return root;
        }

        private static class Item {
            public int rep;
            public int rank;
            Item parent;

            public Item(int rep) {
                this.rep = rep;
                this.rank = 0;
                this.parent = this;
            }
        }
    }
}

