/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;

public class TreeSingleSourcePathsImpl<V, E>
implements ShortestPathAlgorithm.SingleSourcePaths<V, E>,
Serializable {
    private static final long serialVersionUID = -5914007312734512847L;
    protected Graph<V, E> g;
    protected V source;
    protected Map<V, Pair<Double, E>> map;

    public TreeSingleSourcePathsImpl(Graph<V, E> g, V source, Map<V, Pair<Double, E>> distanceAndPredecessorMap) {
        this.g = Objects.requireNonNull(g, "Graph is null");
        this.source = Objects.requireNonNull(source, "Source vertex is null");
        this.map = Objects.requireNonNull(distanceAndPredecessorMap, "Distance and predecessor map is null");
    }

    @Override
    public Graph<V, E> getGraph() {
        return this.g;
    }

    @Override
    public V getSourceVertex() {
        return this.source;
    }

    public Map<V, Pair<Double, E>> getDistanceAndPredecessorMap() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public double getWeight(V targetVertex) {
        Pair<Double, E> p = this.map.get(targetVertex);
        if (p == null) {
            if (this.source.equals(targetVertex)) {
                return 0.0;
            }
            return Double.POSITIVE_INFINITY;
        }
        return p.getFirst();
    }

    @Override
    public GraphPath<V, E> getPath(V targetVertex) {
        E e;
        if (this.source.equals(targetVertex)) {
            return GraphWalk.singletonWalk(this.g, this.source, 0.0);
        }
        LinkedList<E> edgeList = new LinkedList<E>();
        V cur = targetVertex;
        Pair<Double, E> p = this.map.get(cur);
        if (p == null || p.getFirst().equals(Double.POSITIVE_INFINITY)) {
            return null;
        }
        double weight = 0.0;
        while (p != null && !cur.equals(this.source) && (e = p.getSecond()) != null) {
            edgeList.addFirst(e);
            weight += this.g.getEdgeWeight(e);
            cur = Graphs.getOppositeVertex(this.g, e, cur);
            p = this.map.get(cur);
        }
        return new GraphWalk<V, E>(this.g, this.source, targetVertex, null, edgeList, weight);
    }
}

