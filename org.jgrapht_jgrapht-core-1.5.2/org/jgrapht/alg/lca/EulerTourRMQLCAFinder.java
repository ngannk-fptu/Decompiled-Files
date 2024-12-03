/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.lca;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.LowestCommonAncestorAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.util.MathUtil;
import org.jgrapht.util.VertexToIntegerMapping;

public class EulerTourRMQLCAFinder<V, E>
implements LowestCommonAncestorAlgorithm<V> {
    private final Graph<V, E> graph;
    private final Set<V> roots;
    private final int maxLevel;
    private Map<V, Integer> vertexMap;
    private List<V> indexList;
    private int[] eulerTour;
    private int sizeTour;
    private int numberComponent;
    private int[] component;
    private int[] level;
    private int[] representative;
    private int[][] rmq;
    private int[] log2;

    public EulerTourRMQLCAFinder(Graph<V, E> graph, V root) {
        this(graph, Collections.singleton(Objects.requireNonNull(root, "root cannot be null")));
    }

    public EulerTourRMQLCAFinder(Graph<V, E> graph, Set<V> roots) {
        this.graph = Objects.requireNonNull(graph, "graph cannot be null");
        this.roots = Objects.requireNonNull(roots, "roots cannot be null");
        this.maxLevel = 1 + MathUtil.log2(graph.vertexSet().size());
        if (this.roots.isEmpty()) {
            throw new IllegalArgumentException("roots cannot be empty");
        }
        if (!graph.vertexSet().containsAll(roots)) {
            throw new IllegalArgumentException("at least one root is not a valid vertex");
        }
        this.computeAncestorsStructure();
    }

    private void normalizeGraph() {
        VertexToIntegerMapping<V> vertexToIntegerMapping = Graphs.getVertexToIntegerMapping(this.graph);
        this.vertexMap = vertexToIntegerMapping.getVertexMap();
        this.indexList = vertexToIntegerMapping.getIndexList();
    }

    private void dfsIterative(int u, int startLevel) {
        HashSet<Integer> explored = new HashSet<Integer>();
        ArrayDeque<Pair<Integer, Integer>> stack = new ArrayDeque<Pair<Integer, Integer>>();
        stack.push(Pair.of(u, startLevel));
        while (!stack.isEmpty()) {
            Pair pair = (Pair)stack.poll();
            u = (Integer)pair.getFirst();
            int lvl = (Integer)pair.getSecond();
            if (!explored.contains(u)) {
                explored.add(u);
                this.component[u] = this.numberComponent;
                this.eulerTour[this.sizeTour] = u;
                this.level[this.sizeTour] = lvl;
                ++this.sizeTour;
                V vertexU = this.indexList.get(u);
                for (E edge : this.graph.outgoingEdgesOf(vertexU)) {
                    int child = this.vertexMap.get(Graphs.getOppositeVertex(this.graph, edge, vertexU));
                    if (explored.contains(child)) continue;
                    stack.push(pair);
                    stack.push(Pair.of(child, lvl + 1));
                }
                continue;
            }
            this.eulerTour[this.sizeTour] = u;
            this.level[this.sizeTour] = lvl;
            ++this.sizeTour;
        }
    }

    private void computeRMQ() {
        int i;
        this.rmq = new int[this.maxLevel + 1][this.sizeTour];
        this.log2 = new int[this.sizeTour + 1];
        for (i = 0; i < this.sizeTour; ++i) {
            this.rmq[0][i] = i;
        }
        i = 1;
        while (1 << i <= this.sizeTour) {
            int j = 0;
            while (j + (1 << i) - 1 < this.sizeTour) {
                int p = 1 << i - 1;
                this.rmq[i][j] = this.level[this.rmq[i - 1][j]] < this.level[this.rmq[i - 1][j + p]] ? this.rmq[i - 1][j] : this.rmq[i - 1][j + p];
                ++j;
            }
            ++i;
        }
        for (i = 2; i <= this.sizeTour; ++i) {
            this.log2[i] = this.log2[i / 2] + 1;
        }
    }

    private void computeAncestorsStructure() {
        this.normalizeGraph();
        this.eulerTour = new int[2 * this.graph.vertexSet().size()];
        this.level = new int[2 * this.graph.vertexSet().size()];
        this.representative = new int[this.graph.vertexSet().size()];
        this.numberComponent = 0;
        this.component = new int[this.graph.vertexSet().size()];
        for (V root : this.roots) {
            int u = this.vertexMap.get(root);
            if (this.component[u] == 0) {
                ++this.numberComponent;
                this.dfsIterative(u, -1);
                continue;
            }
            throw new IllegalArgumentException("multiple roots in the same tree");
        }
        Arrays.fill(this.representative, -1);
        for (int i = 0; i < this.sizeTour; ++i) {
            if (this.representative[this.eulerTour[i]] != -1) continue;
            this.representative[this.eulerTour[i]] = i;
        }
        this.computeRMQ();
    }

    @Override
    public V getLCA(V a, V b) {
        int pwl;
        int l;
        int sol;
        int indexA = this.vertexMap.getOrDefault(a, -1);
        if (indexA == -1) {
            throw new IllegalArgumentException("invalid vertex: " + a);
        }
        int indexB = this.vertexMap.getOrDefault(b, -1);
        if (indexB == -1) {
            throw new IllegalArgumentException("invalid vertex: " + b);
        }
        if (a.equals(b)) {
            return a;
        }
        if (this.component[indexA] != this.component[indexB] || this.component[indexA] == 0) {
            return null;
        }
        if ((indexA = this.representative[indexA]) > (indexB = this.representative[indexB])) {
            int t = indexA;
            indexA = indexB;
            indexB = t;
        }
        if (this.level[sol = this.rmq[l = this.log2[indexB - indexA + 1]][indexA]] > this.level[this.rmq[l][indexB - (pwl = 1 << l) + 1]]) {
            sol = this.rmq[l][indexB - pwl + 1];
        }
        return this.indexList.get(this.eulerTour[sol]);
    }

    @Override
    public Set<V> getLCASet(V a, V b) {
        throw new UnsupportedOperationException();
    }
}

