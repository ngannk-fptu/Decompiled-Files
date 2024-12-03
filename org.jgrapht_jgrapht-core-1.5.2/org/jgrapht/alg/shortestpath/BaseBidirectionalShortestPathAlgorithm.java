/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.LinkedList;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.graph.GraphWalk;

public abstract class BaseBidirectionalShortestPathAlgorithm<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    public BaseBidirectionalShortestPathAlgorithm(Graph<V, E> graph) {
        super(graph);
    }

    protected GraphPath<V, E> createPath(BaseSearchFrontier<V, E> forwardFrontier, BaseSearchFrontier<V, E> backwardFrontier, double weight, V source, V commonVertex, V sink) {
        E e;
        LinkedList<E> edgeList = new LinkedList<E>();
        LinkedList<V> vertexList = new LinkedList<V>();
        vertexList.add(commonVertex);
        V v = commonVertex;
        while ((e = forwardFrontier.getTreeEdge(v)) != null) {
            edgeList.addFirst(e);
            v = Graphs.getOppositeVertex(forwardFrontier.graph, e, v);
            vertexList.addFirst(v);
        }
        v = commonVertex;
        while ((e = backwardFrontier.getTreeEdge(v)) != null) {
            edgeList.addLast(e);
            v = Graphs.getOppositeVertex(backwardFrontier.graph, e, v);
            vertexList.addLast(v);
        }
        return new GraphWalk(this.graph, source, sink, vertexList, edgeList, weight);
    }

    static abstract class BaseSearchFrontier<V, E> {
        final Graph<V, E> graph;

        BaseSearchFrontier(Graph<V, E> graph) {
            this.graph = graph;
        }

        abstract double getDistance(V var1);

        abstract E getTreeEdge(V var1);
    }
}

