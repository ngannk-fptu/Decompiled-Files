/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractGraph;

public class GraphDelegator<V, E>
extends AbstractGraph<V, E>
implements Graph<V, E>,
Serializable {
    private static final long serialVersionUID = -215068279981825448L;
    private final Graph<V, E> delegate;
    private final Supplier<V> vertexSupplier;
    private final Supplier<E> edgeSupplier;

    public GraphDelegator(Graph<V, E> graph) {
        this(graph, null, null);
    }

    public GraphDelegator(Graph<V, E> graph, Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        this.delegate = Objects.requireNonNull(graph, "graph must not be null");
        this.vertexSupplier = vertexSupplier;
        this.edgeSupplier = edgeSupplier;
    }

    @Override
    public Supplier<V> getVertexSupplier() {
        if (this.vertexSupplier != null) {
            return this.vertexSupplier;
        }
        return this.delegate.getVertexSupplier();
    }

    @Override
    public Supplier<E> getEdgeSupplier() {
        if (this.edgeSupplier != null) {
            return this.edgeSupplier;
        }
        return this.delegate.getEdgeSupplier();
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        return this.delegate.getAllEdges(sourceVertex, targetVertex);
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        return this.delegate.getEdge(sourceVertex, targetVertex);
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        if (this.edgeSupplier != null) {
            E e = this.edgeSupplier.get();
            return (E)(this.addEdge(sourceVertex, targetVertex, e) ? e : null);
        }
        return this.delegate.addEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        return this.delegate.addEdge(sourceVertex, targetVertex, e);
    }

    @Override
    public V addVertex() {
        if (this.vertexSupplier != null) {
            V v = this.vertexSupplier.get();
            return (V)(this.addVertex(v) ? v : null);
        }
        return this.delegate.addVertex();
    }

    @Override
    public boolean addVertex(V v) {
        return this.delegate.addVertex(v);
    }

    @Override
    public boolean containsEdge(E e) {
        return this.delegate.containsEdge(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return this.delegate.containsVertex(v);
    }

    @Override
    public int degreeOf(V vertex) {
        return this.delegate.degreeOf(vertex);
    }

    @Override
    public Set<E> edgeSet() {
        return this.delegate.edgeSet();
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        return this.delegate.edgesOf(vertex);
    }

    @Override
    public int inDegreeOf(V vertex) {
        return this.delegate.inDegreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return this.delegate.incomingEdgesOf(vertex);
    }

    @Override
    public int outDegreeOf(V vertex) {
        return this.delegate.outDegreeOf(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return this.delegate.outgoingEdgesOf(vertex);
    }

    @Override
    public boolean removeEdge(E e) {
        return this.delegate.removeEdge(e);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        return this.delegate.removeEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean removeVertex(V v) {
        return this.delegate.removeVertex(v);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public Set<V> vertexSet() {
        return this.delegate.vertexSet();
    }

    @Override
    public V getEdgeSource(E e) {
        return this.delegate.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e) {
        return this.delegate.getEdgeTarget(e);
    }

    @Override
    public double getEdgeWeight(E e) {
        return this.delegate.getEdgeWeight(e);
    }

    @Override
    public void setEdgeWeight(E e, double weight) {
        this.delegate.setEdgeWeight(e, weight);
    }

    @Override
    public GraphType getType() {
        return this.delegate.getType();
    }

    protected Graph<V, E> getDelegate() {
        return this.delegate;
    }
}

