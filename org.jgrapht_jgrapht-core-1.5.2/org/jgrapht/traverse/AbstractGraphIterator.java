/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.traverse.GraphIterator;

public abstract class AbstractGraphIterator<V, E>
implements GraphIterator<V, E> {
    private final Set<TraversalListener<V, E>> traversalListeners = new LinkedHashSet<TraversalListener<V, E>>();
    protected int nListeners = 0;
    protected final FlyweightEdgeEvent<E> reusableEdgeEvent;
    protected final FlyweightVertexEvent<V> reusableVertexEvent;
    protected final Graph<V, E> graph;
    protected boolean crossComponentTraversal;
    protected boolean reuseEvents;

    public AbstractGraphIterator(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "graph must not be null");
        this.reusableEdgeEvent = new FlyweightEdgeEvent<Object>(this, null);
        this.reusableVertexEvent = new FlyweightVertexEvent<Object>(this, null);
        this.crossComponentTraversal = true;
        this.reuseEvents = false;
    }

    public Graph<V, E> getGraph() {
        return this.graph;
    }

    public void setCrossComponentTraversal(boolean crossComponentTraversal) {
        this.crossComponentTraversal = crossComponentTraversal;
    }

    @Override
    public boolean isCrossComponentTraversal() {
        return this.crossComponentTraversal;
    }

    @Override
    public void setReuseEvents(boolean reuseEvents) {
        this.reuseEvents = reuseEvents;
    }

    @Override
    public boolean isReuseEvents() {
        return this.reuseEvents;
    }

    @Override
    public void addTraversalListener(TraversalListener<V, E> l) {
        this.traversalListeners.add(l);
        this.nListeners = this.traversalListeners.size();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public void removeTraversalListener(TraversalListener<V, E> l) {
        this.traversalListeners.remove(l);
        this.nListeners = this.traversalListeners.size();
    }

    protected void fireConnectedComponentFinished(ConnectedComponentTraversalEvent e) {
        for (TraversalListener<V, E> l : this.traversalListeners) {
            l.connectedComponentFinished(e);
        }
    }

    protected void fireConnectedComponentStarted(ConnectedComponentTraversalEvent e) {
        for (TraversalListener<V, E> l : this.traversalListeners) {
            l.connectedComponentStarted(e);
        }
    }

    protected void fireEdgeTraversed(EdgeTraversalEvent<E> e) {
        for (TraversalListener<V, E> l : this.traversalListeners) {
            l.edgeTraversed(e);
        }
    }

    protected void fireVertexTraversed(VertexTraversalEvent<V> e) {
        for (TraversalListener<V, E> l : this.traversalListeners) {
            l.vertexTraversed(e);
        }
    }

    protected void fireVertexFinished(VertexTraversalEvent<V> e) {
        for (TraversalListener<V, E> l : this.traversalListeners) {
            l.vertexFinished(e);
        }
    }

    protected VertexTraversalEvent<V> createVertexTraversalEvent(V vertex) {
        if (this.reuseEvents) {
            this.reusableVertexEvent.setVertex(vertex);
            return this.reusableVertexEvent;
        }
        return new VertexTraversalEvent<V>(this, vertex);
    }

    protected EdgeTraversalEvent<E> createEdgeTraversalEvent(E edge) {
        if (this.isReuseEvents()) {
            this.reusableEdgeEvent.setEdge(edge);
            return this.reusableEdgeEvent;
        }
        return new EdgeTraversalEvent<E>(this, edge);
    }

    static class FlyweightEdgeEvent<E>
    extends EdgeTraversalEvent<E> {
        private static final long serialVersionUID = 4051327833765000755L;

        public FlyweightEdgeEvent(Object eventSource, E edge) {
            super(eventSource, edge);
        }

        protected void setEdge(E edge) {
            this.edge = edge;
        }
    }

    static class FlyweightVertexEvent<V>
    extends VertexTraversalEvent<V> {
        private static final long serialVersionUID = 3834024753848399924L;

        public FlyweightVertexEvent(Object eventSource, V vertex) {
            super(eventSource, vertex);
        }

        protected void setVertex(V vertex) {
            this.vertex = vertex;
        }
    }
}

