/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.ArrayDeque;
import java.util.Deque;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.CrossComponentIterator;

public class BreadthFirstIterator<V, E>
extends CrossComponentIterator<V, E, SearchNodeData<E>> {
    private Deque<V> queue = new ArrayDeque<V>();

    public BreadthFirstIterator(Graph<V, E> g) {
        this((Graph<Object, E>)g, null);
    }

    public BreadthFirstIterator(Graph<V, E> g, V startVertex) {
        super(g, startVertex);
    }

    public BreadthFirstIterator(Graph<V, E> g, Iterable<V> startVertices) {
        super(g, startVertices);
    }

    @Override
    protected boolean isConnectedComponentExhausted() {
        return this.queue.isEmpty();
    }

    @Override
    protected void encounterVertex(V vertex, E edge) {
        int depth = edge == null ? 0 : ((SearchNodeData)this.getSeenData(Graphs.getOppositeVertex(this.graph, edge, vertex))).depth + 1;
        this.putSeenData(vertex, new SearchNodeData<E>(edge, depth));
        this.queue.add(vertex);
    }

    @Override
    protected void encounterVertexAgain(V vertex, E edge) {
    }

    public V getParent(V v) {
        assert (this.getSeenData(v) != null);
        Object edge = ((SearchNodeData)this.getSeenData(v)).edge;
        if (edge == null) {
            return null;
        }
        return Graphs.getOppositeVertex(this.graph, edge, v);
    }

    public E getSpanningTreeEdge(V v) {
        assert (this.getSeenData(v) != null);
        return ((SearchNodeData)this.getSeenData(v)).edge;
    }

    public int getDepth(V v) {
        assert (this.getSeenData(v) != null);
        return ((SearchNodeData)this.getSeenData(v)).depth;
    }

    @Override
    protected V provideNextVertex() {
        return this.queue.removeFirst();
    }

    protected static class SearchNodeData<E> {
        private final E edge;
        private final int depth;

        public SearchNodeData(E edge, int depth) {
            this.edge = edge;
            this.depth = depth;
        }

        public E getEdge() {
            return this.edge;
        }

        public int getDepth() {
            return this.depth;
        }
    }
}

