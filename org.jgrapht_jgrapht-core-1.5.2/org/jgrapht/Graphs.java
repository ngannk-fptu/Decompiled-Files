/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.util.VertexToIntegerMapping;

public abstract class Graphs {
    public static <V, E> E addEdge(Graph<V, E> g, V sourceVertex, V targetVertex, double weight) {
        Supplier<E> edgeSupplier = g.getEdgeSupplier();
        if (edgeSupplier == null) {
            throw new UnsupportedOperationException("Graph contains no edge supplier");
        }
        E e = edgeSupplier.get();
        if (g.addEdge(sourceVertex, targetVertex, e)) {
            g.setEdgeWeight(e, weight);
            return e;
        }
        return null;
    }

    public static <V, E> E addEdgeWithVertices(Graph<V, E> g, V sourceVertex, V targetVertex) {
        g.addVertex(sourceVertex);
        g.addVertex(targetVertex);
        return g.addEdge(sourceVertex, targetVertex);
    }

    public static <V, E> boolean addEdgeWithVertices(Graph<V, E> targetGraph, Graph<V, E> sourceGraph, E edge) {
        V sourceVertex = sourceGraph.getEdgeSource(edge);
        V targetVertex = sourceGraph.getEdgeTarget(edge);
        targetGraph.addVertex(sourceVertex);
        targetGraph.addVertex(targetVertex);
        return targetGraph.addEdge(sourceVertex, targetVertex, edge);
    }

    public static <V, E> E addEdgeWithVertices(Graph<V, E> g, V sourceVertex, V targetVertex, double weight) {
        g.addVertex(sourceVertex);
        g.addVertex(targetVertex);
        return Graphs.addEdge(g, sourceVertex, targetVertex, weight);
    }

    public static <V, E> boolean addGraph(Graph<? super V, ? super E> destination, Graph<V, E> source) {
        boolean modified = Graphs.addAllVertices(destination, source.vertexSet());
        return modified |= Graphs.addAllEdges(destination, source, source.edgeSet());
    }

    public static <V, E> void addGraphReversed(Graph<? super V, ? super E> destination, Graph<V, E> source) {
        if (!source.getType().isDirected() || !destination.getType().isDirected()) {
            throw new IllegalArgumentException("graph must be directed");
        }
        Graphs.addAllVertices(destination, source.vertexSet());
        for (E edge : source.edgeSet()) {
            destination.addEdge(source.getEdgeTarget(edge), source.getEdgeSource(edge));
        }
    }

    public static <V, E> boolean addAllEdges(Graph<? super V, ? super E> destination, Graph<V, E> source, Collection<? extends E> edges) {
        boolean modified = false;
        for (E e : edges) {
            V s = source.getEdgeSource(e);
            V t = source.getEdgeTarget(e);
            destination.addVertex(s);
            destination.addVertex(t);
            modified |= destination.addEdge(s, t, e);
        }
        return modified;
    }

    public static <V, E> boolean addAllVertices(Graph<? super V, ? super E> destination, Collection<? extends V> vertices) {
        boolean modified = false;
        for (V v : vertices) {
            modified |= destination.addVertex(v);
        }
        return modified;
    }

    public static <V, E> List<V> neighborListOf(Graph<V, E> g, V vertex) {
        ArrayList<V> neighbors = new ArrayList<V>();
        for (E e : g.iterables().edgesOf(vertex)) {
            neighbors.add(Graphs.getOppositeVertex(g, e, vertex));
        }
        return neighbors;
    }

    public static <V, E> Set<V> neighborSetOf(Graph<V, E> g, V vertex) {
        LinkedHashSet<V> neighbors = new LinkedHashSet<V>();
        for (E e : g.iterables().edgesOf(vertex)) {
            neighbors.add(Graphs.getOppositeVertex(g, e, vertex));
        }
        return neighbors;
    }

    public static <V, E> List<V> predecessorListOf(Graph<V, E> g, V vertex) {
        ArrayList<V> predecessors = new ArrayList<V>();
        for (E e : g.iterables().incomingEdgesOf(vertex)) {
            predecessors.add(Graphs.getOppositeVertex(g, e, vertex));
        }
        return predecessors;
    }

    public static <V, E> List<V> successorListOf(Graph<V, E> g, V vertex) {
        ArrayList<V> successors = new ArrayList<V>();
        for (E e : g.iterables().outgoingEdgesOf(vertex)) {
            successors.add(Graphs.getOppositeVertex(g, e, vertex));
        }
        return successors;
    }

    public static <V, E> Graph<V, E> undirectedGraph(Graph<V, E> g) {
        if (g.getType().isDirected()) {
            return new AsUndirectedGraph<V, E>(g);
        }
        if (g.getType().isUndirected()) {
            return g;
        }
        throw new IllegalArgumentException("graph must be either directed or undirected");
    }

    public static <V, E> boolean testIncidence(Graph<V, E> g, E e, V v) {
        return g.getEdgeSource(e).equals(v) || g.getEdgeTarget(e).equals(v);
    }

    public static <V, E> V getOppositeVertex(Graph<V, E> g, E e, V v) {
        V source = g.getEdgeSource(e);
        V target = g.getEdgeTarget(e);
        if (v.equals(source)) {
            return target;
        }
        if (v.equals(target)) {
            return source;
        }
        throw new IllegalArgumentException("no such vertex: " + v.toString());
    }

    public static <V, E> boolean removeVertexAndPreserveConnectivity(Graph<V, E> graph, V vertex) {
        if (!graph.containsVertex(vertex)) {
            return false;
        }
        if (Graphs.vertexHasPredecessors(graph, vertex)) {
            List<V> predecessors = Graphs.predecessorListOf(graph, vertex);
            List<V> successors = Graphs.successorListOf(graph, vertex);
            for (V predecessor : predecessors) {
                Graphs.addOutgoingEdges(graph, predecessor, successors);
            }
        }
        graph.removeVertex(vertex);
        return true;
    }

    public static <V, E> boolean removeVerticesAndPreserveConnectivity(Graph<V, E> graph, Predicate<V> predicate) {
        ArrayList<V> verticesToRemove = new ArrayList<V>();
        for (V node : graph.vertexSet()) {
            if (!predicate.test(node)) continue;
            verticesToRemove.add(node);
        }
        return Graphs.removeVertexAndPreserveConnectivity(graph, verticesToRemove);
    }

    public static <V, E> boolean removeVertexAndPreserveConnectivity(Graph<V, E> graph, Iterable<V> vertices) {
        boolean atLeastOneVertexHasBeenRemoved = false;
        for (V vertex : vertices) {
            if (!Graphs.removeVertexAndPreserveConnectivity(graph, vertex)) continue;
            atLeastOneVertexHasBeenRemoved = true;
        }
        return atLeastOneVertexHasBeenRemoved;
    }

    public static <V, E> void addOutgoingEdges(Graph<V, E> graph, V source, Iterable<V> targets) {
        if (!graph.containsVertex(source)) {
            graph.addVertex(source);
        }
        for (V target : targets) {
            if (!graph.containsVertex(target)) {
                graph.addVertex(target);
            }
            graph.addEdge(source, target);
        }
    }

    public static <V, E> void addIncomingEdges(Graph<V, E> graph, V target, Iterable<V> sources) {
        if (!graph.containsVertex(target)) {
            graph.addVertex(target);
        }
        for (V source : sources) {
            if (!graph.containsVertex(source)) {
                graph.addVertex(source);
            }
            graph.addEdge(source, target);
        }
    }

    public static <V, E> boolean vertexHasSuccessors(Graph<V, E> graph, V vertex) {
        return !graph.outgoingEdgesOf(vertex).isEmpty();
    }

    public static <V, E> boolean vertexHasPredecessors(Graph<V, E> graph, V vertex) {
        return !graph.incomingEdgesOf(vertex).isEmpty();
    }

    public static <V, E> VertexToIntegerMapping<V> getVertexToIntegerMapping(Graph<V, E> graph) {
        return new VertexToIntegerMapping<V>(Objects.requireNonNull(graph).vertexSet());
    }
}

