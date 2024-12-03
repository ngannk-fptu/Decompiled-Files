/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.lca;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.LowestCommonAncestorAlgorithm;
import org.jgrapht.util.MathUtil;
import org.jgrapht.util.VertexToIntegerMapping;

public class BinaryLiftingLCAFinder<V, E>
implements LowestCommonAncestorAlgorithm<V> {
    private final Graph<V, E> graph;
    private final Set<V> roots;
    private final int maxLevel;
    private Map<V, Integer> vertexMap;
    private List<V> indexList;
    private int[][] ancestors;
    private int[] timeIn;
    private int[] timeOut;
    private int clock = 0;
    private int numberComponent;
    private int[] component;

    public BinaryLiftingLCAFinder(Graph<V, E> graph, V root) {
        this(graph, Collections.singleton(Objects.requireNonNull(root, "root cannot be null")));
    }

    public BinaryLiftingLCAFinder(Graph<V, E> graph, Set<V> roots) {
        this.graph = Objects.requireNonNull(graph, "graph cannot be null");
        this.roots = Objects.requireNonNull(roots, "roots cannot be null");
        this.maxLevel = MathUtil.log2(graph.vertexSet().size());
        if (this.roots.isEmpty()) {
            throw new IllegalArgumentException("roots cannot be empty");
        }
        if (!graph.vertexSet().containsAll(roots)) {
            throw new IllegalArgumentException("at least one root is not a valid vertex");
        }
        this.computeAncestorMatrix();
    }

    private void normalizeGraph() {
        VertexToIntegerMapping<V> vertexToIntegerMapping = Graphs.getVertexToIntegerMapping(this.graph);
        this.vertexMap = vertexToIntegerMapping.getVertexMap();
        this.indexList = vertexToIntegerMapping.getIndexList();
    }

    private void dfs(int u, int parent) {
        this.component[u] = this.numberComponent;
        this.timeIn[u] = ++this.clock;
        this.ancestors[0][u] = parent;
        for (int l = 1; l < this.maxLevel; ++l) {
            if (this.ancestors[l - 1][u] == -1) continue;
            this.ancestors[l][u] = this.ancestors[l - 1][this.ancestors[l - 1][u]];
        }
        V vertexU = this.indexList.get(u);
        for (E edge : this.graph.outgoingEdgesOf(vertexU)) {
            int v = this.vertexMap.get(Graphs.getOppositeVertex(this.graph, edge, vertexU));
            if (v == parent) continue;
            this.dfs(v, u);
        }
        this.timeOut[u] = ++this.clock;
    }

    private void computeAncestorMatrix() {
        this.ancestors = new int[this.maxLevel + 1][this.graph.vertexSet().size()];
        for (int l = 0; l < this.maxLevel; ++l) {
            Arrays.fill(this.ancestors[l], -1);
        }
        this.timeIn = new int[this.graph.vertexSet().size()];
        this.timeOut = new int[this.graph.vertexSet().size()];
        for (int i = 0; i < this.graph.vertexSet().size(); ++i) {
            this.timeIn[i] = this.timeOut[i] = -(i + 1);
        }
        this.numberComponent = 0;
        this.component = new int[this.graph.vertexSet().size()];
        this.normalizeGraph();
        for (V root : this.roots) {
            if (this.component[this.vertexMap.get(root)] == 0) {
                ++this.numberComponent;
                this.dfs(this.vertexMap.get(root), -1);
                continue;
            }
            throw new IllegalArgumentException("multiple roots in the same tree");
        }
    }

    private boolean isAncestor(int ancestor, int descendant) {
        return this.timeIn[ancestor] <= this.timeIn[descendant] && this.timeOut[descendant] <= this.timeOut[ancestor];
    }

    @Override
    public V getLCA(V a, V b) {
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
        if (this.isAncestor(indexA, indexB)) {
            return a;
        }
        if (this.isAncestor(indexB, indexA)) {
            return b;
        }
        for (int l = this.maxLevel - 1; l >= 0; --l) {
            if (this.ancestors[l][indexA] == -1 || this.isAncestor(this.ancestors[l][indexA], indexB)) continue;
            indexA = this.ancestors[l][indexA];
        }
        int lca = this.ancestors[0][indexA];
        if (lca == -1) {
            return null;
        }
        return this.indexList.get(lca);
    }

    @Override
    public Set<V> getLCASet(V a, V b) {
        throw new UnsupportedOperationException();
    }
}

