/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.decomposition;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.TreeToPathDecompositionAlgorithm;
import org.jgrapht.util.VertexToIntegerMapping;

public class HeavyPathDecomposition<V, E>
implements TreeToPathDecompositionAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private final Set<V> roots;
    private Map<V, Integer> vertexMap;
    private List<V> indexList;
    private int[] sizeSubtree;
    private int[] parent;
    private int[] depth;
    private int[] component;
    private int[] path;
    private int[] lengthPath;
    private int[] positionInPath;
    private int[] firstNodeInPath;
    private int numberOfPaths;
    private List<List<V>> paths;
    private Set<E> heavyEdges;
    private Set<E> lightEdges;

    public HeavyPathDecomposition(Graph<V, E> tree, V root) {
        this(tree, Collections.singleton(Objects.requireNonNull(root, "root cannot be null")));
    }

    public HeavyPathDecomposition(Graph<V, E> forest, Set<V> roots) {
        this.graph = Objects.requireNonNull(forest, "input tree/forrest cannot be null");
        this.roots = Objects.requireNonNull(roots, "set of roots cannot be null");
        this.decompose();
    }

    private void allocateArrays() {
        int n = this.graph.vertexSet().size();
        this.sizeSubtree = new int[n];
        this.parent = new int[n];
        this.depth = new int[n];
        this.component = new int[n];
        this.path = new int[n];
        this.lengthPath = new int[n];
        this.positionInPath = new int[n];
        this.heavyEdges = new HashSet();
        this.lightEdges = new HashSet();
    }

    private void normalizeGraph() {
        VertexToIntegerMapping<V> vertexToIntegerMapping = Graphs.getVertexToIntegerMapping(this.graph);
        this.vertexMap = vertexToIntegerMapping.getVertexMap();
        this.indexList = vertexToIntegerMapping.getIndexList();
    }

    private void dfsIterative(int u, int c) {
        HashSet<Integer> explored = new HashSet<Integer>();
        ArrayDeque<Integer> stack = new ArrayDeque<Integer>();
        stack.push(u);
        while (!stack.isEmpty()) {
            u = (Integer)stack.poll();
            if (!explored.contains(u)) {
                explored.add(u);
                stack.push(u);
                this.component[u] = c;
                this.sizeSubtree[u] = 1;
                V vertexU = this.indexList.get(u);
                for (E edge : this.graph.edgesOf(vertexU)) {
                    int child = this.vertexMap.get(Graphs.getOppositeVertex(this.graph, edge, vertexU));
                    if (explored.contains(child)) continue;
                    this.parent[child] = u;
                    this.depth[child] = this.depth[u] + 1;
                    stack.push(child);
                }
                continue;
            }
            int pathChild = -1;
            Object pathEdge = null;
            V vertexU = this.indexList.get(u);
            for (E edge : this.graph.edgesOf(vertexU)) {
                int child = this.vertexMap.get(Graphs.getOppositeVertex(this.graph, edge, vertexU));
                if (child == this.parent[u]) continue;
                int n = u;
                this.sizeSubtree[n] = this.sizeSubtree[n] + this.sizeSubtree[child];
                if (pathChild == -1 || this.sizeSubtree[pathChild] < this.sizeSubtree[child]) {
                    pathChild = child;
                    pathEdge = edge;
                }
                this.lightEdges.add(edge);
            }
            if (pathChild == -1) {
                ++this.numberOfPaths;
            } else {
                this.path[u] = this.path[pathChild];
                if (2 * this.sizeSubtree[pathChild] > this.sizeSubtree[u]) {
                    this.heavyEdges.add(pathEdge);
                    this.lightEdges.remove(pathEdge);
                }
            }
            int n = this.path[u];
            this.lengthPath[n] = this.lengthPath[n] + 1;
        }
    }

    private void decompose() {
        int i;
        if (this.path != null) {
            return;
        }
        this.normalizeGraph();
        this.allocateArrays();
        Arrays.fill(this.parent, -1);
        Arrays.fill(this.path, -1);
        Arrays.fill(this.depth, -1);
        Arrays.fill(this.component, -1);
        Arrays.fill(this.positionInPath, -1);
        int numberComponent = 0;
        for (V root : this.roots) {
            Integer u = this.vertexMap.get(root);
            if (u == null) {
                throw new IllegalArgumentException("root: " + root + " not contained in graph");
            }
            if (this.component[u] == -1) {
                this.dfsIterative(u, numberComponent++);
                continue;
            }
            throw new IllegalArgumentException("multiple roots in the same tree");
        }
        this.firstNodeInPath = new int[this.numberOfPaths];
        for (int i2 = 0; i2 < this.graph.vertexSet().size(); ++i2) {
            if (this.path[i2] == -1) continue;
            this.positionInPath[i2] = this.lengthPath[this.path[i2]] - this.positionInPath[i2] - 1;
            if (this.positionInPath[i2] != 0) continue;
            this.firstNodeInPath[this.path[i2]] = i2;
        }
        ArrayList<List<Object>> paths = new ArrayList<List<Object>>(this.numberOfPaths);
        for (i = 0; i < this.numberOfPaths; ++i) {
            ArrayList<Object> path = new ArrayList<Object>(this.lengthPath[i]);
            for (int j = 0; j < this.lengthPath[i]; ++j) {
                path.add(null);
            }
            paths.add(path);
        }
        for (i = 0; i < this.graph.vertexSet().size(); ++i) {
            if (this.path[i] == -1) continue;
            ((List)paths.get(this.path[i])).set(this.positionInPath[i], this.indexList.get(i));
        }
        for (i = 0; i < this.numberOfPaths; ++i) {
            paths.set(i, Collections.unmodifiableList((List)paths.get(i)));
        }
        this.paths = Collections.unmodifiableList(paths);
        this.heavyEdges = Collections.unmodifiableSet(this.heavyEdges);
    }

    public Set<E> getHeavyEdges() {
        return this.heavyEdges;
    }

    public Set<E> getLightEdges() {
        return this.lightEdges;
    }

    @Override
    public TreeToPathDecompositionAlgorithm.PathDecomposition<V, E> getPathDecomposition() {
        return new TreeToPathDecompositionAlgorithm.PathDecompositionImpl<V, E>(this.graph, this.getHeavyEdges(), this.paths);
    }

    public InternalState getInternalState() {
        return new InternalState();
    }

    public class InternalState {
        public V getParent(V v) {
            int index = HeavyPathDecomposition.this.vertexMap.getOrDefault(v, -1);
            if (index == -1 || HeavyPathDecomposition.this.parent[index] == -1) {
                return null;
            }
            return HeavyPathDecomposition.this.indexList.get(HeavyPathDecomposition.this.parent[index]);
        }

        public int getDepth(V v) {
            int index = HeavyPathDecomposition.this.vertexMap.getOrDefault(v, -1);
            if (index == -1) {
                return -1;
            }
            return HeavyPathDecomposition.this.depth[index];
        }

        public int getSizeSubtree(V v) {
            int index = HeavyPathDecomposition.this.vertexMap.getOrDefault(v, -1);
            if (index == -1) {
                return 0;
            }
            return HeavyPathDecomposition.this.sizeSubtree[index];
        }

        public int getComponent(V v) {
            int index = HeavyPathDecomposition.this.vertexMap.getOrDefault(v, -1);
            if (index == -1) {
                return -1;
            }
            return HeavyPathDecomposition.this.component[index];
        }

        public Map<V, Integer> getVertexMap() {
            return Collections.unmodifiableMap(HeavyPathDecomposition.this.vertexMap);
        }

        public List<V> getIndexList() {
            return Collections.unmodifiableList(HeavyPathDecomposition.this.indexList);
        }

        public int[] getDepthArray() {
            return HeavyPathDecomposition.this.depth;
        }

        public int[] getSizeSubtreeArray() {
            return HeavyPathDecomposition.this.sizeSubtree;
        }

        public int[] getComponentArray() {
            return HeavyPathDecomposition.this.component;
        }

        public int[] getPathArray() {
            return HeavyPathDecomposition.this.path;
        }

        public int[] getPositionInPathArray() {
            return HeavyPathDecomposition.this.positionInPath;
        }

        public int[] getFirstNodeInPathArray() {
            return HeavyPathDecomposition.this.firstNodeInPath;
        }

        public int[] getParentArray() {
            return HeavyPathDecomposition.this.parent;
        }
    }
}

