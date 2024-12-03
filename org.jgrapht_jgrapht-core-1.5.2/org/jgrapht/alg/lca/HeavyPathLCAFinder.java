/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.lca;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.decomposition.HeavyPathDecomposition;
import org.jgrapht.alg.interfaces.LowestCommonAncestorAlgorithm;

public class HeavyPathLCAFinder<V, E>
implements LowestCommonAncestorAlgorithm<V> {
    private final Graph<V, E> graph;
    private final Set<V> roots;
    private int[] parent;
    private int[] depth;
    private int[] path;
    private int[] positionInPath;
    private int[] component;
    private int[] firstNodeInPath;
    private Map<V, Integer> vertexMap;
    private List<V> indexList;

    public HeavyPathLCAFinder(Graph<V, E> graph, V root) {
        this(graph, Collections.singleton(Objects.requireNonNull(root, "root cannot be null")));
    }

    public HeavyPathLCAFinder(Graph<V, E> graph, Set<V> roots) {
        this.graph = Objects.requireNonNull(graph, "graph cannot be null");
        this.roots = Objects.requireNonNull(roots, "roots cannot be null");
        if (this.roots.isEmpty()) {
            throw new IllegalArgumentException("roots cannot be empty");
        }
        if (!graph.vertexSet().containsAll(roots)) {
            throw new IllegalArgumentException("at least one root is not a valid vertex");
        }
        this.computeHeavyPathDecomposition();
    }

    private void computeHeavyPathDecomposition() {
        HeavyPathDecomposition<V, E> heavyPath = new HeavyPathDecomposition<V, E>(this.graph, this.roots);
        HeavyPathDecomposition.InternalState state = heavyPath.getInternalState();
        this.vertexMap = state.getVertexMap();
        this.indexList = state.getIndexList();
        this.parent = state.getParentArray();
        this.depth = state.getDepthArray();
        this.component = state.getComponentArray();
        this.firstNodeInPath = state.getFirstNodeInPathArray();
        this.path = state.getPathArray();
        this.positionInPath = state.getPositionInPathArray();
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
        int componentA = this.component[indexA];
        int componentB = this.component[indexB];
        if (componentA != componentB || componentA == -1) {
            return null;
        }
        int pathA = this.path[indexA];
        int pathB = this.path[indexB];
        while (pathA != pathB) {
            int firstNodePathA = this.firstNodeInPath[pathA];
            int firstNodePathB = this.firstNodeInPath[pathB];
            if (this.depth[firstNodePathA] < this.depth[firstNodePathB]) {
                indexB = this.parent[firstNodePathB];
                pathB = this.path[indexB];
                continue;
            }
            indexA = this.parent[firstNodePathA];
            pathA = this.path[indexA];
        }
        return this.positionInPath[indexA] < this.positionInPath[indexB] ? this.indexList.get(indexA) : this.indexList.get(indexB);
    }

    @Override
    public Set<V> getLCASet(V a, V b) {
        throw new UnsupportedOperationException();
    }
}

