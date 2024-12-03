/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.MaskEdgeSet;
import org.jgrapht.graph.MaskVertexSet;

public class MaskSubgraph<V, E>
extends AbstractGraph<V, E>
implements Serializable {
    private static final long serialVersionUID = -7397441126669119179L;
    private static final String UNMODIFIABLE = "this graph is unmodifiable";
    protected final Graph<V, E> base;
    protected final GraphType baseType;
    protected final Set<E> edges;
    protected final Set<V> vertices;
    protected final Predicate<V> vertexMask;
    protected final Predicate<E> edgeMask;

    public MaskSubgraph(Graph<V, E> base, Predicate<V> vertexMask, Predicate<E> edgeMask) {
        this.base = Objects.requireNonNull(base, "Invalid graph provided");
        this.baseType = base.getType();
        this.vertexMask = Objects.requireNonNull(vertexMask, "Invalid vertex mask provided");
        this.edgeMask = Objects.requireNonNull(edgeMask, "Invalid edge mask provided");
        this.vertices = new MaskVertexSet<V>(base.vertexSet(), vertexMask);
        this.edges = new MaskEdgeSet<V, E>(base, base.edgeSet(), vertexMask, edgeMask);
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E edge) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public V addVertex() {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public boolean addVertex(V v) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public boolean containsEdge(E e) {
        return this.edgeSet().contains(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return this.vertexSet().contains(v);
    }

    @Override
    public Set<E> edgeSet() {
        return this.edges;
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return new MaskEdgeSet<V, E>(this.base, this.base.edgesOf(vertex), this.vertexMask, this.edgeMask);
    }

    @Override
    public int degreeOf(V vertex) {
        if (this.baseType.isDirected()) {
            return this.inDegreeOf(vertex) + this.outDegreeOf(vertex);
        }
        int degree = 0;
        for (E e : this.edgesOf(vertex)) {
            ++degree;
            if (!this.getEdgeSource(e).equals(this.getEdgeTarget(e))) continue;
            ++degree;
        }
        return degree;
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return new MaskEdgeSet<V, E>(this.base, this.base.incomingEdgesOf(vertex), this.vertexMask, this.edgeMask);
    }

    @Override
    public int inDegreeOf(V vertex) {
        if (this.baseType.isUndirected()) {
            return this.degreeOf(vertex);
        }
        return this.incomingEdgesOf(vertex).size();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return new MaskEdgeSet<V, E>(this.base, this.base.outgoingEdgesOf(vertex), this.vertexMask, this.edgeMask);
    }

    @Override
    public int outDegreeOf(V vertex) {
        if (this.baseType.isUndirected()) {
            return this.degreeOf(vertex);
        }
        return this.outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        if (this.containsVertex(sourceVertex) && this.containsVertex(targetVertex)) {
            return new MaskEdgeSet<V, E>(this.base, this.base.getAllEdges(sourceVertex, targetVertex), this.vertexMask, this.edgeMask);
        }
        return null;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        Set<E> edges = this.getAllEdges(sourceVertex, targetVertex);
        if (edges == null) {
            return null;
        }
        return edges.stream().findAny().orElse(null);
    }

    @Override
    public Supplier<V> getVertexSupplier() {
        return this.base.getVertexSupplier();
    }

    @Override
    public Supplier<E> getEdgeSupplier() {
        return this.base.getEdgeSupplier();
    }

    @Override
    public V getEdgeSource(E edge) {
        assert (this.edgeSet().contains(edge));
        return this.base.getEdgeSource(edge);
    }

    @Override
    public V getEdgeTarget(E edge) {
        assert (this.edgeSet().contains(edge));
        return this.base.getEdgeTarget(edge);
    }

    @Override
    public GraphType getType() {
        return this.baseType.asUnmodifiable();
    }

    @Override
    public double getEdgeWeight(E edge) {
        assert (this.edgeSet().contains(edge));
        return this.base.getEdgeWeight(edge);
    }

    @Override
    public void setEdgeWeight(E edge, double weight) {
        assert (this.edgeSet().contains(edge));
        this.base.setEdgeWeight(edge, weight);
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public boolean removeEdge(E e) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public boolean removeVertex(V v) {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    @Override
    public Set<V> vertexSet() {
        return this.vertices;
    }
}

