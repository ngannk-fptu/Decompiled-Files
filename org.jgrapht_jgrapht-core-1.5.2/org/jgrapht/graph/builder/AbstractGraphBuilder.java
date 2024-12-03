/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.builder;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsUnmodifiableGraph;

public abstract class AbstractGraphBuilder<V, E, G extends Graph<V, E>, B extends AbstractGraphBuilder<V, E, G, B>> {
    protected final G graph;

    public AbstractGraphBuilder(G baseGraph) {
        this.graph = baseGraph;
    }

    protected abstract B self();

    public B addVertex(V vertex) {
        this.graph.addVertex(vertex);
        return this.self();
    }

    @SafeVarargs
    public final B addVertices(V ... vertices) {
        for (V vertex : vertices) {
            this.addVertex(vertex);
        }
        return this.self();
    }

    public B addEdge(V source, V target) {
        Graphs.addEdgeWithVertices(this.graph, source, target);
        return this.self();
    }

    public B addEdge(V source, V target, E edge) {
        this.addVertex(source);
        this.addVertex(target);
        this.graph.addEdge(source, target, edge);
        return this.self();
    }

    @SafeVarargs
    public final B addEdgeChain(V first, V second, V ... rest) {
        this.addEdge(first, second);
        V last = second;
        for (V vertex : rest) {
            this.addEdge(last, vertex);
            last = vertex;
        }
        return this.self();
    }

    public B addGraph(Graph<? extends V, ? extends E> sourceGraph) {
        Graphs.addGraph(this.graph, sourceGraph);
        return this.self();
    }

    public B removeVertex(V vertex) {
        this.graph.removeVertex(vertex);
        return this.self();
    }

    @SafeVarargs
    public final B removeVertices(V ... vertices) {
        for (V vertex : vertices) {
            this.removeVertex(vertex);
        }
        return this.self();
    }

    public B removeEdge(V source, V target) {
        this.graph.removeEdge(source, target);
        return this.self();
    }

    public B removeEdge(E edge) {
        this.graph.removeEdge(edge);
        return this.self();
    }

    public B addEdge(V source, V target, double weight) {
        Graphs.addEdgeWithVertices(this.graph, source, target, weight);
        return this.self();
    }

    public B addEdge(V source, V target, E edge, double weight) {
        this.addEdge(source, target, edge);
        this.graph.setEdgeWeight(edge, weight);
        return this.self();
    }

    public G build() {
        return this.graph;
    }

    public Graph<V, E> buildAsUnmodifiable() {
        return new AsUnmodifiableGraph(this.graph);
    }
}

