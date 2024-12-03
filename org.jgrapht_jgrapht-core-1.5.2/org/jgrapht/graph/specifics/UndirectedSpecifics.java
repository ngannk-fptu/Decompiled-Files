/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.specifics;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.graph.specifics.Specifics;
import org.jgrapht.graph.specifics.UndirectedEdgeContainer;
import org.jgrapht.util.ArrayUnenforcedSet;

public class UndirectedSpecifics<V, E>
implements Specifics<V, E>,
Serializable {
    private static final long serialVersionUID = 4206026440450450992L;
    protected Graph<V, E> graph;
    protected Map<V, UndirectedEdgeContainer<V, E>> vertexMap;
    protected EdgeSetFactory<V, E> edgeSetFactory;

    public UndirectedSpecifics(Graph<V, E> graph, Map<V, UndirectedEdgeContainer<V, E>> vertexMap, EdgeSetFactory<V, E> edgeSetFactory) {
        this.graph = Objects.requireNonNull(graph);
        this.vertexMap = Objects.requireNonNull(vertexMap);
        this.edgeSetFactory = Objects.requireNonNull(edgeSetFactory);
    }

    @Override
    public boolean addVertex(V v) {
        UndirectedEdgeContainer<V, E> ec = this.vertexMap.get(v);
        if (ec == null) {
            this.vertexMap.put((UndirectedEdgeContainer<V, E>)v, (UndirectedEdgeContainer<UndirectedEdgeContainer<V, E>, E>)new UndirectedEdgeContainer<V, E>(this.edgeSetFactory, v));
            return true;
        }
        return false;
    }

    @Override
    public Set<V> getVertexSet() {
        return this.vertexMap.keySet();
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        ArrayUnenforcedSet edges = null;
        if (this.graph.containsVertex(sourceVertex) && this.graph.containsVertex(targetVertex)) {
            edges = new ArrayUnenforcedSet();
            for (Object e : this.getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal = this.isEqualsStraightOrInverted(sourceVertex, targetVertex, e);
                if (!equal) continue;
                edges.add(e);
            }
        }
        return edges;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        if (this.graph.containsVertex(sourceVertex) && this.graph.containsVertex(targetVertex)) {
            for (Object e : this.getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal = this.isEqualsStraightOrInverted(sourceVertex, targetVertex, e);
                if (!equal) continue;
                return e;
            }
        }
        return null;
    }

    private boolean isEqualsStraightOrInverted(Object sourceVertex, Object targetVertex, E e) {
        boolean equalStraight = sourceVertex.equals(this.graph.getEdgeSource(e)) && targetVertex.equals(this.graph.getEdgeTarget(e));
        boolean equalInverted = sourceVertex.equals(this.graph.getEdgeTarget(e)) && targetVertex.equals(this.graph.getEdgeSource(e));
        return equalStraight || equalInverted;
    }

    @Override
    public boolean addEdgeToTouchingVertices(V sourceVertex, V targetVertex, E e) {
        this.getEdgeContainer(sourceVertex).addEdge(e);
        if (!sourceVertex.equals(targetVertex)) {
            this.getEdgeContainer(targetVertex).addEdge(e);
        }
        return true;
    }

    @Override
    public boolean addEdgeToTouchingVerticesIfAbsent(V sourceVertex, V targetVertex, E e) {
        UndirectedEdgeContainer<V, E> ec = this.getEdgeContainer(sourceVertex);
        for (Object edge : ec.vertexEdges) {
            if (!this.isEqualsStraightOrInverted(sourceVertex, targetVertex, edge)) continue;
            return false;
        }
        ec.addEdge(e);
        this.getEdgeContainer(targetVertex).addEdge(e);
        return true;
    }

    @Override
    public E createEdgeToTouchingVerticesIfAbsent(V sourceVertex, V targetVertex, Supplier<E> edgeSupplier) {
        UndirectedEdgeContainer<V, E> ec = this.getEdgeContainer(sourceVertex);
        for (Object edge : ec.vertexEdges) {
            if (!this.isEqualsStraightOrInverted(sourceVertex, targetVertex, edge)) continue;
            return null;
        }
        E e = edgeSupplier.get();
        ec.addEdge(e);
        this.getEdgeContainer(targetVertex).addEdge(e);
        return e;
    }

    @Override
    public int degreeOf(V vertex) {
        if (this.graph.getType().isAllowingSelfLoops()) {
            int degree = 0;
            Set edges = this.getEdgeContainer(vertex).vertexEdges;
            for (Object e : edges) {
                if (this.graph.getEdgeSource(e).equals(this.graph.getEdgeTarget(e))) {
                    degree += 2;
                    continue;
                }
                ++degree;
            }
            return degree;
        }
        return this.getEdgeContainer(vertex).edgeCount();
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        return this.getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    @Override
    public int inDegreeOf(V vertex) {
        return this.degreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return this.getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    @Override
    public int outDegreeOf(V vertex) {
        return this.degreeOf(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return this.getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    @Override
    public void removeEdgeFromTouchingVertices(V sourceVertex, V targetVertex, E e) {
        this.getEdgeContainer(sourceVertex).removeEdge(e);
        if (!sourceVertex.equals(targetVertex)) {
            this.getEdgeContainer(targetVertex).removeEdge(e);
        }
    }

    protected UndirectedEdgeContainer<V, E> getEdgeContainer(V vertex) {
        UndirectedEdgeContainer<V, E> ec = this.vertexMap.get(vertex);
        if (ec == null) {
            ec = new UndirectedEdgeContainer<V, E>(this.edgeSetFactory, vertex);
            this.vertexMap.put((UndirectedEdgeContainer<V, E>)vertex, (UndirectedEdgeContainer<UndirectedEdgeContainer<V, E>, E>)ec);
        }
        return ec;
    }
}

