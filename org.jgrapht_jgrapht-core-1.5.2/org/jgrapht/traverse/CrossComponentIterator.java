/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.traverse.AbstractGraphIterator;

public abstract class CrossComponentIterator<V, E, D>
extends AbstractGraphIterator<V, E> {
    private static final int CCS_BEFORE_COMPONENT = 1;
    private static final int CCS_WITHIN_COMPONENT = 2;
    private static final int CCS_AFTER_COMPONENT = 3;
    private final ConnectedComponentTraversalEvent ccFinishedEvent = new ConnectedComponentTraversalEvent(this, 32);
    private final ConnectedComponentTraversalEvent ccStartedEvent = new ConnectedComponentTraversalEvent(this, 31);
    private Map<V, D> seen = new HashMap<V, D>();
    private Iterator<V> entireGraphVertexIterator = null;
    private Iterator<V> startVertexIterator = null;
    private V startVertex;
    private int state = 1;

    public CrossComponentIterator(Graph<V, E> g) {
        this((Graph<Object, E>)g, null);
    }

    public CrossComponentIterator(Graph<V, E> g, V startVertex) {
        this(g, (Iterable<V>)(startVertex == null ? null : Collections.singletonList(startVertex)));
    }

    public CrossComponentIterator(Graph<V, E> g, Iterable<V> startVertices) {
        super(g);
        Iterator<V> it;
        if (startVertices == null) {
            this.crossComponentTraversal = true;
        } else {
            this.crossComponentTraversal = false;
            this.startVertexIterator = startVertices.iterator();
        }
        Iterator<V> iterator = it = this.crossComponentTraversal ? this.getEntireGraphVertexIterator() : this.startVertexIterator;
        if (it.hasNext()) {
            this.startVertex = it.next();
            if (!this.graph.containsVertex(this.startVertex)) {
                throw new IllegalArgumentException("graph must contain the start vertex");
            }
        } else {
            this.startVertex = null;
        }
    }

    @Override
    public boolean hasNext() {
        if (this.startVertex != null) {
            this.encounterStartVertex();
        }
        if (this.isConnectedComponentExhausted()) {
            Iterator<V> it;
            if (this.state == 2) {
                this.state = 3;
                if (this.nListeners != 0) {
                    this.fireConnectedComponentFinished(this.ccFinishedEvent);
                }
            }
            Iterator<V> iterator = it = this.isCrossComponentTraversal() ? this.getEntireGraphVertexIterator() : this.startVertexIterator;
            while (it != null && it.hasNext()) {
                V v = it.next();
                if (!this.graph.containsVertex(v)) {
                    throw new IllegalArgumentException("graph must contain the start vertex");
                }
                if (this.isSeenVertex(v)) continue;
                this.encounterVertex(v, null);
                this.state = 1;
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public V next() {
        if (this.startVertex != null) {
            this.encounterStartVertex();
        }
        if (this.hasNext()) {
            if (this.state == 1) {
                this.state = 2;
                if (this.nListeners != 0) {
                    this.fireConnectedComponentStarted(this.ccStartedEvent);
                }
            }
            V nextVertex = this.provideNextVertex();
            if (this.nListeners != 0) {
                this.fireVertexTraversed(this.createVertexTraversalEvent(nextVertex));
            }
            this.addUnseenChildrenOf(nextVertex);
            return nextVertex;
        }
        throw new NoSuchElementException();
    }

    protected Iterator<V> getEntireGraphVertexIterator() {
        if (this.entireGraphVertexIterator == null) {
            assert (this.isCrossComponentTraversal());
            this.entireGraphVertexIterator = this.graph.vertexSet().iterator();
        }
        return this.entireGraphVertexIterator;
    }

    protected abstract boolean isConnectedComponentExhausted();

    protected abstract void encounterVertex(V var1, E var2);

    protected abstract V provideNextVertex();

    protected D getSeenData(V vertex) {
        return this.seen.get(vertex);
    }

    protected boolean isSeenVertex(V vertex) {
        return this.seen.containsKey(vertex);
    }

    protected abstract void encounterVertexAgain(V var1, E var2);

    protected D putSeenData(V vertex, D data) {
        return this.seen.put(vertex, data);
    }

    protected void finishVertex(V vertex) {
        if (this.nListeners != 0) {
            this.fireVertexFinished(this.createVertexTraversalEvent(vertex));
        }
    }

    protected Set<E> selectOutgoingEdges(V vertex) {
        return this.graph.outgoingEdgesOf(vertex);
    }

    private void addUnseenChildrenOf(V vertex) {
        for (E edge : this.selectOutgoingEdges(vertex)) {
            V oppositeV;
            if (this.nListeners != 0) {
                this.fireEdgeTraversed(this.createEdgeTraversalEvent(edge));
            }
            if (this.isSeenVertex(oppositeV = Graphs.getOppositeVertex(this.graph, edge, vertex))) {
                this.encounterVertexAgain(oppositeV, edge);
                continue;
            }
            this.encounterVertex(oppositeV, edge);
        }
    }

    private void encounterStartVertex() {
        this.encounterVertex(this.startVertex, null);
        this.startVertex = null;
    }
}

