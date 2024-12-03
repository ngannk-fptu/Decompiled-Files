/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

import org.jgrapht.Graph;
import org.jgrapht.util.LiveIterableWrapper;

public interface GraphIterables<V, E> {
    public Graph<V, E> getGraph();

    default public Iterable<E> edges() {
        return new LiveIterableWrapper(() -> this.getGraph().edgeSet());
    }

    default public long edgeCount() {
        return this.getGraph().edgeSet().size();
    }

    default public Iterable<V> vertices() {
        return new LiveIterableWrapper(() -> this.getGraph().vertexSet());
    }

    default public long vertexCount() {
        return this.getGraph().vertexSet().size();
    }

    default public Iterable<E> edgesOf(V vertex) {
        return new LiveIterableWrapper(() -> this.getGraph().edgesOf(vertex));
    }

    default public long degreeOf(V vertex) {
        return this.getGraph().degreeOf(vertex);
    }

    default public Iterable<E> incomingEdgesOf(V vertex) {
        return new LiveIterableWrapper(() -> this.getGraph().incomingEdgesOf(vertex));
    }

    default public long inDegreeOf(V vertex) {
        return this.getGraph().inDegreeOf(vertex);
    }

    default public Iterable<E> outgoingEdgesOf(V vertex) {
        return new LiveIterableWrapper(() -> this.getGraph().outgoingEdgesOf(vertex));
    }

    default public long outDegreeOf(V vertex) {
        return this.getGraph().outDegreeOf(vertex);
    }

    default public Iterable<E> allEdges(V sourceVertex, V targetVertex) {
        return new LiveIterableWrapper(() -> this.getGraph().getAllEdges(sourceVertex, targetVertex));
    }
}

