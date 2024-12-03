/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.GraphDelegator;
import org.jgrapht.util.TypeUtil;

public class DefaultListenableGraph<V, E>
extends GraphDelegator<V, E>
implements ListenableGraph<V, E>,
Cloneable {
    private static final long serialVersionUID = -1156773351121025002L;
    private List<GraphListener<V, E>> graphListeners = new ArrayList<GraphListener<V, E>>();
    private List<VertexSetListener<V>> vertexSetListeners = new ArrayList<VertexSetListener<V>>();
    private FlyweightEdgeEvent<V, E> reuseableEdgeEvent;
    private FlyweightVertexEvent<V> reuseableVertexEvent;
    private boolean reuseEvents;

    public DefaultListenableGraph(Graph<V, E> g) {
        this(g, false);
    }

    public DefaultListenableGraph(Graph<V, E> g, boolean reuseEvents) {
        super(g);
        this.reuseEvents = reuseEvents;
        this.reuseableEdgeEvent = new FlyweightEdgeEvent(this, -1, null);
        this.reuseableVertexEvent = new FlyweightVertexEvent<Object>((Object)this, -1, null);
        if (g instanceof ListenableGraph) {
            throw new IllegalArgumentException("base graph cannot be listenable");
        }
    }

    public void setReuseEvents(boolean reuseEvents) {
        this.reuseEvents = reuseEvents;
    }

    public boolean isReuseEvents() {
        return this.reuseEvents;
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        Object e = super.addEdge(sourceVertex, targetVertex);
        if (e != null) {
            this.fireEdgeAdded(e, sourceVertex, targetVertex, 1.0);
        }
        return e;
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        boolean added = super.addEdge(sourceVertex, targetVertex, e);
        if (added) {
            this.fireEdgeAdded(e, sourceVertex, targetVertex, 1.0);
        }
        return added;
    }

    @Override
    public void addGraphListener(GraphListener<V, E> l) {
        DefaultListenableGraph.addToListenerList(this.graphListeners, l);
    }

    @Override
    public V addVertex() {
        Object v = super.addVertex();
        if (v != null) {
            this.fireVertexAdded(v);
        }
        return v;
    }

    @Override
    public boolean addVertex(V v) {
        boolean modified = super.addVertex(v);
        if (modified) {
            this.fireVertexAdded(v);
        }
        return modified;
    }

    @Override
    public void addVertexSetListener(VertexSetListener<V> l) {
        DefaultListenableGraph.addToListenerList(this.vertexSetListeners, l);
    }

    public Object clone() {
        try {
            DefaultListenableGraph g = (DefaultListenableGraph)TypeUtil.uncheckedCast(super.clone());
            g.graphListeners = new ArrayList<GraphListener<V, E>>();
            g.vertexSetListeners = new ArrayList<VertexSetListener<V>>();
            return g;
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("internal error");
        }
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        Object e = super.getEdge(sourceVertex, targetVertex);
        if (e != null) {
            double weight = super.getEdgeWeight(e);
            if (super.removeEdge(e)) {
                this.fireEdgeRemoved(e, sourceVertex, targetVertex, weight);
            }
        }
        return e;
    }

    @Override
    public boolean removeEdge(E e) {
        Object sourceVertex = this.getEdgeSource(e);
        Object targetVertex = this.getEdgeTarget(e);
        double weight = this.getEdgeWeight(e);
        boolean modified = super.removeEdge(e);
        if (modified) {
            this.fireEdgeRemoved(e, sourceVertex, targetVertex, weight);
        }
        return modified;
    }

    @Override
    public void removeGraphListener(GraphListener<V, E> l) {
        this.graphListeners.remove(l);
    }

    @Override
    public boolean removeVertex(V v) {
        if (this.containsVertex(v)) {
            Set touchingEdgesList = this.edgesOf(v);
            this.removeAllEdges(new ArrayList(touchingEdgesList));
            super.removeVertex(v);
            this.fireVertexRemoved(v);
            return true;
        }
        return false;
    }

    @Override
    public void setEdgeWeight(E e, double weight) {
        super.setEdgeWeight(e, weight);
        Object sourceVertex = this.getEdgeSource(e);
        Object targetVertex = this.getEdgeTarget(e);
        this.fireEdgeWeightUpdated(e, sourceVertex, targetVertex, weight);
    }

    @Override
    public void removeVertexSetListener(VertexSetListener<V> l) {
        this.vertexSetListeners.remove(l);
    }

    protected void fireEdgeAdded(E edge, V source, V target, double weight) {
        GraphEdgeChangeEvent<V, E> e = this.createGraphEdgeChangeEvent(23, edge, source, target, weight);
        for (GraphListener<V, E> l : this.graphListeners) {
            l.edgeAdded(e);
        }
    }

    protected void fireEdgeRemoved(E edge, V source, V target, double weight) {
        GraphEdgeChangeEvent<V, E> e = this.createGraphEdgeChangeEvent(24, edge, source, target, weight);
        for (GraphListener<V, E> l : this.graphListeners) {
            l.edgeRemoved(e);
        }
    }

    protected void fireEdgeWeightUpdated(E edge, V source, V target, double weight) {
        GraphEdgeChangeEvent<V, E> e = this.createGraphEdgeChangeEvent(25, edge, source, target, weight);
        for (GraphListener<V, E> l : this.graphListeners) {
            l.edgeWeightUpdated(e);
        }
    }

    protected void fireVertexAdded(V vertex) {
        GraphVertexChangeEvent<V> e = this.createGraphVertexChangeEvent(13, vertex);
        for (VertexSetListener<V> vertexSetListener : this.vertexSetListeners) {
            vertexSetListener.vertexAdded(e);
        }
        for (GraphListener graphListener : this.graphListeners) {
            graphListener.vertexAdded(e);
        }
    }

    protected void fireVertexRemoved(V vertex) {
        GraphVertexChangeEvent<V> e = this.createGraphVertexChangeEvent(14, vertex);
        for (VertexSetListener<V> vertexSetListener : this.vertexSetListeners) {
            vertexSetListener.vertexRemoved(e);
        }
        for (GraphListener graphListener : this.graphListeners) {
            graphListener.vertexRemoved(e);
        }
    }

    private static <L extends EventListener> void addToListenerList(List<L> list, L l) {
        if (!list.contains(l)) {
            list.add(l);
        }
    }

    private GraphEdgeChangeEvent<V, E> createGraphEdgeChangeEvent(int eventType, E edge, V source, V target, double weight) {
        if (this.reuseEvents) {
            this.reuseableEdgeEvent.setType(eventType);
            this.reuseableEdgeEvent.setEdge(edge);
            this.reuseableEdgeEvent.setEdgeSource(source);
            this.reuseableEdgeEvent.setEdgeTarget(target);
            this.reuseableEdgeEvent.setEdgeWeight(weight);
            return this.reuseableEdgeEvent;
        }
        return new GraphEdgeChangeEvent<V, E>(this, eventType, edge, source, target, weight);
    }

    private GraphVertexChangeEvent<V> createGraphVertexChangeEvent(int eventType, V vertex) {
        if (this.reuseEvents) {
            this.reuseableVertexEvent.setType(eventType);
            this.reuseableVertexEvent.setVertex(vertex);
            return this.reuseableVertexEvent;
        }
        return new GraphVertexChangeEvent<V>(this, eventType, vertex);
    }

    private static class FlyweightEdgeEvent<VV, EE>
    extends GraphEdgeChangeEvent<VV, EE> {
        private static final long serialVersionUID = 3907207152526636089L;

        public FlyweightEdgeEvent(Object eventSource, int type, EE e) {
            super(eventSource, type, e, null, null);
        }

        protected void setEdge(EE e) {
            this.edge = e;
        }

        protected void setEdgeSource(VV v) {
            this.edgeSource = v;
        }

        protected void setEdgeTarget(VV v) {
            this.edgeTarget = v;
        }

        protected void setEdgeWeight(double weight) {
            this.edgeWeight = weight;
        }

        protected void setType(int type) {
            this.type = type;
        }
    }

    private static class FlyweightVertexEvent<VV>
    extends GraphVertexChangeEvent<VV> {
        private static final long serialVersionUID = 3257848787857585716L;

        public FlyweightVertexEvent(Object eventSource, int type, VV vertex) {
            super(eventSource, type, vertex);
        }

        protected void setType(int type) {
            this.type = type;
        }

        protected void setVertex(VV vertex) {
            this.vertex = vertex;
        }
    }
}

