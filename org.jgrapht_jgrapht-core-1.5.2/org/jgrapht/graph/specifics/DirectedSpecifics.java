/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.specifics;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.graph.specifics.DirectedEdgeContainer;
import org.jgrapht.graph.specifics.Specifics;
import org.jgrapht.util.ArrayUnenforcedSet;

public class DirectedSpecifics<V, E>
implements Specifics<V, E>,
Serializable {
    private static final long serialVersionUID = 5964807709682219859L;
    protected Graph<V, E> graph;
    protected Map<V, DirectedEdgeContainer<V, E>> vertexMap;
    protected EdgeSetFactory<V, E> edgeSetFactory;

    public DirectedSpecifics(Graph<V, E> graph, Map<V, DirectedEdgeContainer<V, E>> vertexMap, EdgeSetFactory<V, E> edgeSetFactory) {
        this.graph = Objects.requireNonNull(graph);
        this.vertexMap = Objects.requireNonNull(vertexMap);
        this.edgeSetFactory = Objects.requireNonNull(edgeSetFactory);
    }

    @Override
    public boolean addVertex(V v) {
        DirectedEdgeContainer<V, E> ec = this.vertexMap.get(v);
        if (ec == null) {
            this.vertexMap.put((DirectedEdgeContainer<V, E>)v, (DirectedEdgeContainer<DirectedEdgeContainer<V, E>, E>)new DirectedEdgeContainer<V, E>(this.edgeSetFactory, v));
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
            DirectedEdgeContainer<V, E> ec = this.getEdgeContainer(sourceVertex);
            for (Object e : ec.outgoing) {
                if (!this.graph.getEdgeTarget(e).equals(targetVertex)) continue;
                edges.add(e);
            }
        }
        return edges;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        if (this.graph.containsVertex(sourceVertex) && this.graph.containsVertex(targetVertex)) {
            DirectedEdgeContainer<V, E> ec = this.getEdgeContainer(sourceVertex);
            for (Object e : ec.outgoing) {
                if (!this.graph.getEdgeTarget(e).equals(targetVertex)) continue;
                return e;
            }
        }
        return null;
    }

    @Override
    public boolean addEdgeToTouchingVertices(V sourceVertex, V targetVertex, E e) {
        this.getEdgeContainer(sourceVertex).addOutgoingEdge(e);
        this.getEdgeContainer(targetVertex).addIncomingEdge(e);
        return true;
    }

    @Override
    public boolean addEdgeToTouchingVerticesIfAbsent(V sourceVertex, V targetVertex, E e) {
        DirectedEdgeContainer<V, E> ec = this.getEdgeContainer(sourceVertex);
        for (Object outEdge : ec.outgoing) {
            if (!this.graph.getEdgeTarget(outEdge).equals(targetVertex)) continue;
            return false;
        }
        ec.addOutgoingEdge(e);
        this.getEdgeContainer(targetVertex).addIncomingEdge(e);
        return true;
    }

    @Override
    public E createEdgeToTouchingVerticesIfAbsent(V sourceVertex, V targetVertex, Supplier<E> edgeSupplier) {
        DirectedEdgeContainer<V, E> ec = this.getEdgeContainer(sourceVertex);
        for (Object e : ec.outgoing) {
            if (!this.graph.getEdgeTarget(e).equals(targetVertex)) continue;
            return null;
        }
        E e = edgeSupplier.get();
        ec.addOutgoingEdge(e);
        this.getEdgeContainer(targetVertex).addIncomingEdge(e);
        return e;
    }

    @Override
    public int degreeOf(V vertex) {
        return this.inDegreeOf(vertex) + this.outDegreeOf(vertex);
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        ArrayUnenforcedSet inAndOut = new ArrayUnenforcedSet(this.getEdgeContainer(vertex).incoming);
        if (this.graph.getType().isAllowingSelfLoops()) {
            for (Object e : this.getEdgeContainer(vertex).outgoing) {
                V target = this.graph.getEdgeTarget(e);
                if (vertex.equals(target)) continue;
                inAndOut.add(e);
            }
        } else {
            inAndOut.addAll(this.getEdgeContainer(vertex).outgoing);
        }
        return Collections.unmodifiableSet(inAndOut);
    }

    @Override
    public int inDegreeOf(V vertex) {
        return this.getEdgeContainer(vertex).incoming.size();
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return this.getEdgeContainer(vertex).getUnmodifiableIncomingEdges();
    }

    @Override
    public int outDegreeOf(V vertex) {
        return this.getEdgeContainer(vertex).outgoing.size();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return this.getEdgeContainer(vertex).getUnmodifiableOutgoingEdges();
    }

    @Override
    public void removeEdgeFromTouchingVertices(V sourceVertex, V targetVertex, E e) {
        this.getEdgeContainer(sourceVertex).removeOutgoingEdge(e);
        this.getEdgeContainer(targetVertex).removeIncomingEdge(e);
    }

    protected DirectedEdgeContainer<V, E> getEdgeContainer(V vertex) {
        DirectedEdgeContainer<V, E> ec = this.vertexMap.get(vertex);
        if (ec == null) {
            ec = new DirectedEdgeContainer<V, E>(this.edgeSetFactory, vertex);
            this.vertexMap.put((DirectedEdgeContainer<V, E>)vertex, (DirectedEdgeContainer<DirectedEdgeContainer<V, E>, E>)ec);
        }
        return ec;
    }
}

