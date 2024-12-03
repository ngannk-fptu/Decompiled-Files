/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.specifics;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.graph.specifics.DirectedEdgeContainer;
import org.jgrapht.graph.specifics.DirectedSpecifics;

public class FastLookupDirectedSpecifics<V, E>
extends DirectedSpecifics<V, E> {
    private static final long serialVersionUID = 4089085208843722263L;
    protected Map<Pair<V, V>, Set<E>> touchingVerticesToEdgeMap;

    public FastLookupDirectedSpecifics(Graph<V, E> graph, Map<V, DirectedEdgeContainer<V, E>> vertexMap, Map<Pair<V, V>, Set<E>> touchingVerticesToEdgeMap, EdgeSetFactory<V, E> edgeSetFactory) {
        super(graph, vertexMap, edgeSetFactory);
        this.touchingVerticesToEdgeMap = Objects.requireNonNull(touchingVerticesToEdgeMap);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        if (this.graph.containsVertex(sourceVertex) && this.graph.containsVertex(targetVertex)) {
            Set<E> edges = this.touchingVerticesToEdgeMap.get(new Pair<V, V>(sourceVertex, targetVertex));
            if (edges == null) {
                return Collections.emptySet();
            }
            Set<E> edgeSet = this.edgeSetFactory.createEdgeSet(sourceVertex);
            edgeSet.addAll(edges);
            return edgeSet;
        }
        return null;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        Set<E> edges = this.touchingVerticesToEdgeMap.get(new Pair<V, V>(sourceVertex, targetVertex));
        if (edges == null || edges.isEmpty()) {
            return null;
        }
        return edges.iterator().next();
    }

    @Override
    public boolean addEdgeToTouchingVertices(V sourceVertex, V targetVertex, E e) {
        if (!super.addEdgeToTouchingVertices(sourceVertex, targetVertex, e)) {
            return false;
        }
        this.addToIndex(sourceVertex, targetVertex, e);
        return true;
    }

    @Override
    public boolean addEdgeToTouchingVerticesIfAbsent(V sourceVertex, V targetVertex, E e) {
        E edge = this.getEdge(sourceVertex, targetVertex);
        if (edge != null) {
            return false;
        }
        return this.addEdgeToTouchingVertices(sourceVertex, targetVertex, e);
    }

    @Override
    public E createEdgeToTouchingVerticesIfAbsent(V sourceVertex, V targetVertex, Supplier<E> edgeSupplier) {
        E edge = this.getEdge(sourceVertex, targetVertex);
        if (edge != null) {
            return null;
        }
        E e = edgeSupplier.get();
        this.addEdgeToTouchingVertices(sourceVertex, targetVertex, e);
        return e;
    }

    @Override
    public void removeEdgeFromTouchingVertices(V sourceVertex, V targetVertex, E e) {
        super.removeEdgeFromTouchingVertices(sourceVertex, targetVertex, e);
        this.removeFromIndex(sourceVertex, targetVertex, e);
    }

    protected void addToIndex(V sourceVertex, V targetVertex, E e) {
        Pair<V, V> vertexPair = new Pair<V, V>(sourceVertex, targetVertex);
        Set<E> edgeSet = this.touchingVerticesToEdgeMap.get(vertexPair);
        if (edgeSet != null) {
            edgeSet.add(e);
        } else {
            edgeSet = this.edgeSetFactory.createEdgeSet(sourceVertex);
            edgeSet.add(e);
            this.touchingVerticesToEdgeMap.put(vertexPair, edgeSet);
        }
    }

    protected void removeFromIndex(V sourceVertex, V targetVertex, E e) {
        Pair<V, V> vertexPair = new Pair<V, V>(sourceVertex, targetVertex);
        Set<E> edgeSet = this.touchingVerticesToEdgeMap.get(vertexPair);
        if (edgeSet != null) {
            edgeSet.remove(e);
            if (edgeSet.isEmpty()) {
                this.touchingVerticesToEdgeMap.remove(vertexPair);
            }
        }
    }
}

