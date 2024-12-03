/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.util.VertexDegreeComparator;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.CollectionUtil;
import org.jgrapht.util.TypeUtil;

public class FloydWarshallShortestPaths<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    private final List<V> vertices;
    private final List<Integer> degrees;
    private final Map<V, Integer> vertexIndices;
    private final int minDegreeOne;
    private final int minDegreeTwo;
    private double[][] d = null;
    private Object[][] backtrace = null;
    private Object[][] lastHopMatrix = null;

    public FloydWarshallShortestPaths(Graph<V, E> graph) {
        super(graph);
        this.vertices = new ArrayList<V>(graph.vertexSet());
        Collections.sort(this.vertices, VertexDegreeComparator.of(graph));
        this.degrees = new ArrayList<Integer>();
        this.vertexIndices = CollectionUtil.newHashMapWithExpectedSize(this.vertices.size());
        int i = 0;
        int minDegreeOne = this.vertices.size();
        int minDegreeTwo = this.vertices.size();
        for (V vertex : this.vertices) {
            this.vertexIndices.put((Integer)vertex, i);
            int degree = graph.degreeOf(vertex);
            this.degrees.add(degree);
            if (degree > 1) {
                if (i < minDegreeOne) {
                    minDegreeOne = i;
                }
                if (i < minDegreeTwo) {
                    minDegreeTwo = i;
                }
            } else if (i < minDegreeOne && degree == 1) {
                minDegreeOne = i;
            }
            ++i;
        }
        this.minDegreeOne = minDegreeOne;
        this.minDegreeTwo = minDegreeTwo;
    }

    public int getShortestPathsCount() {
        this.lazyCalculateMatrix();
        int n = this.vertices.size();
        int nShortestPaths = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i == j || !Double.isFinite(this.d[i][j])) continue;
                ++nShortestPaths;
            }
        }
        return nShortestPaths;
    }

    @Override
    public GraphPath<V, E> getPath(V a, V b) {
        if (!this.graph.containsVertex(a)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(b)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        this.lazyCalculateMatrix();
        int vA = this.vertexIndices.get(a);
        int vB = this.vertexIndices.get(b);
        if (this.backtrace[vA][vB] == null) {
            return this.createEmptyPath(a, b);
        }
        ArrayList edges = new ArrayList();
        V u = a;
        while (!u.equals(b)) {
            int vU = this.vertexIndices.get(u);
            Object e = TypeUtil.uncheckedCast(this.backtrace[vU][vB]);
            edges.add(e);
            u = Graphs.getOppositeVertex(this.graph, e, u);
        }
        return new GraphWalk(this.graph, a, b, null, edges, this.d[vA][vB]);
    }

    @Override
    public double getPathWeight(V source, V sink) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        this.lazyCalculateMatrix();
        return this.d[this.vertexIndices.get(source)][this.vertexIndices.get(sink)];
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        return new FloydWarshallSingleSourcePaths(source);
    }

    public V getFirstHop(V a, V b) {
        this.lazyCalculateMatrix();
        int vA = this.vertexIndices.get(a);
        int vB = this.vertexIndices.get(b);
        if (this.backtrace[vA][vB] == null) {
            return null;
        }
        Object e = TypeUtil.uncheckedCast(this.backtrace[vA][vB]);
        return Graphs.getOppositeVertex(this.graph, e, a);
    }

    public V getLastHop(V a, V b) {
        this.lazyCalculateMatrix();
        int vA = this.vertexIndices.get(a);
        int vB = this.vertexIndices.get(b);
        if (this.backtrace[vA][vB] == null) {
            return null;
        }
        this.populateLastHopMatrix();
        Object e = TypeUtil.uncheckedCast(this.lastHopMatrix[vA][vB]);
        return Graphs.getOppositeVertex(this.graph, e, b);
    }

    private void lazyCalculateMatrix() {
        int i;
        if (this.d != null) {
            return;
        }
        int n = this.vertices.size();
        this.backtrace = new Object[n][n];
        this.d = new double[n][n];
        for (i = 0; i < n; ++i) {
            Arrays.fill(this.d[i], Double.POSITIVE_INFINITY);
        }
        for (i = 0; i < n; ++i) {
            this.d[i][i] = 0.0;
        }
        if (this.graph.getType().isUndirected()) {
            for (Object edge : this.graph.edgeSet()) {
                Object target;
                Object source = this.graph.getEdgeSource(edge);
                if (source.equals(target = this.graph.getEdgeTarget(edge))) continue;
                int v1 = this.vertexIndices.get(source);
                int v2 = this.vertexIndices.get(target);
                double edgeWeight = this.graph.getEdgeWeight(edge);
                if (Double.compare(edgeWeight, this.d[v1][v2]) >= 0) continue;
                double d = edgeWeight;
                this.d[v2][v1] = d;
                this.d[v1][v2] = d;
                this.backtrace[v1][v2] = edge;
                this.backtrace[v2][v1] = edge;
            }
        } else {
            for (Object v1 : this.graph.vertexSet()) {
                int i1 = this.vertexIndices.get(v1);
                for (Object e : this.graph.outgoingEdgesOf(v1)) {
                    Object v2 = Graphs.getOppositeVertex(this.graph, e, v1);
                    if (v1.equals(v2)) continue;
                    int i2 = this.vertexIndices.get(v2);
                    double edgeWeight = this.graph.getEdgeWeight(e);
                    if (Double.compare(edgeWeight, this.d[i1][i2]) >= 0) continue;
                    this.d[i1][i2] = edgeWeight;
                    this.backtrace[i1][i2] = e;
                }
            }
        }
        for (int k = this.minDegreeTwo; k < n; ++k) {
            for (int i2 = this.minDegreeOne; i2 < n; ++i2) {
                if (i2 == k) continue;
                for (int j = this.minDegreeOne; j < n; ++j) {
                    double sumIKKJ;
                    if (i2 == j || j == k || Double.compare(sumIKKJ = this.d[i2][k] + this.d[k][j], this.d[i2][j]) >= 0) continue;
                    this.d[i2][j] = sumIKKJ;
                    this.backtrace[i2][j] = this.backtrace[i2][k];
                }
            }
        }
    }

    private void populateLastHopMatrix() {
        this.lazyCalculateMatrix();
        if (this.lastHopMatrix != null) {
            return;
        }
        int n = this.vertices.size();
        this.lastHopMatrix = new Object[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i == j || this.lastHopMatrix[i][j] != null || this.backtrace[i][j] == null) continue;
                V u = this.vertices.get(i);
                V b = this.vertices.get(j);
                while (!u.equals(b)) {
                    int vU = this.vertexIndices.get(u);
                    Object e = TypeUtil.uncheckedCast(this.backtrace[vU][j]);
                    V other = Graphs.getOppositeVertex(this.graph, e, u);
                    this.lastHopMatrix[i][this.vertexIndices.get(other).intValue()] = e;
                    u = other;
                }
            }
        }
    }

    class FloydWarshallSingleSourcePaths
    implements ShortestPathAlgorithm.SingleSourcePaths<V, E> {
        private final V source;

        public FloydWarshallSingleSourcePaths(V source) {
            this.source = source;
        }

        @Override
        public Graph<V, E> getGraph() {
            return FloydWarshallShortestPaths.this.graph;
        }

        @Override
        public V getSourceVertex() {
            return this.source;
        }

        @Override
        public double getWeight(V sink) {
            return FloydWarshallShortestPaths.this.getPathWeight(this.source, sink);
        }

        @Override
        public GraphPath<V, E> getPath(V sink) {
            return FloydWarshallShortestPaths.this.getPath(this.source, sink);
        }
    }
}

