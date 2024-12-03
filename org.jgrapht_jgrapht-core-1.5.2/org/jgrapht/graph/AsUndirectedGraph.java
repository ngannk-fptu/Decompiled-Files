/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.graph.GraphDelegator;
import org.jgrapht.util.ArrayUnenforcedSet;

public class AsUndirectedGraph<V, E>
extends GraphDelegator<V, E>
implements Serializable,
Graph<V, E> {
    private static final long serialVersionUID = 325983813283133557L;
    private static final String NO_EDGE_ADD = "this graph does not support edge addition";

    public AsUndirectedGraph(Graph<V, E> g) {
        super(g);
        GraphTests.requireDirected(g);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        Set forwardList = super.getAllEdges(sourceVertex, targetVertex);
        if (sourceVertex.equals(targetVertex)) {
            return forwardList;
        }
        Set reverseList = super.getAllEdges(targetVertex, sourceVertex);
        ArrayUnenforcedSet list = new ArrayUnenforcedSet(forwardList.size() + reverseList.size());
        list.addAll(forwardList);
        list.addAll(reverseList);
        return list;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        Object edge = super.getEdge(sourceVertex, targetVertex);
        if (edge != null) {
            return edge;
        }
        return super.getEdge(targetVertex, sourceVertex);
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException(NO_EDGE_ADD);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        throw new UnsupportedOperationException(NO_EDGE_ADD);
    }

    @Override
    public int degreeOf(V vertex) {
        return super.degreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return super.edgesOf(vertex);
    }

    @Override
    public int inDegreeOf(V vertex) {
        return super.degreeOf(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return super.edgesOf(vertex);
    }

    @Override
    public int outDegreeOf(V vertex) {
        return super.degreeOf(vertex);
    }

    @Override
    public GraphType getType() {
        return super.getType().asUndirected();
    }

    @Override
    public String toString() {
        return super.toStringFromSets(this.vertexSet(), this.edgeSet(), false);
    }
}

