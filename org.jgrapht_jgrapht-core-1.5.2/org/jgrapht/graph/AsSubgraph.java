/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.graph.AbstractGraph;

public class AsSubgraph<V, E>
extends AbstractGraph<V, E>
implements Serializable {
    private static final long serialVersionUID = -1471811754881775298L;
    private static final String NO_SUCH_EDGE_IN_BASE = "no such edge in base graph";
    private static final String NO_SUCH_VERTEX_IN_BASE = "no such vertex in base graph";
    private static final String CANNOT_CREATE_NEW_VERTICES_FROM_SUBGRAPH = "Cannot create new vertices from subgraph";
    protected final Set<E> edgeSet = new LinkedHashSet();
    protected final Set<V> vertexSet = new LinkedHashSet<V>();
    protected final Graph<V, E> base;
    protected final GraphType baseType;
    protected final boolean isInduced;
    private transient Set<E> unmodifiableEdgeSet = null;
    private transient Set<V> unmodifiableVertexSet = null;

    public AsSubgraph(Graph<V, E> base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset) {
        this.base = GraphTests.requireDirectedOrUndirected(base);
        this.baseType = base.getType();
        boolean bl = this.isInduced = edgeSubset == null;
        if (base instanceof ListenableGraph) {
            ((ListenableGraph)base).addGraphListener(new BaseGraphListener());
        }
        this.initialize(vertexSubset, edgeSubset);
    }

    public AsSubgraph(Graph<V, E> base, Set<? extends V> vertexSubset) {
        this(base, vertexSubset, null);
    }

    public AsSubgraph(Graph<V, E> base) {
        this(base, null, null);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        if (this.containsVertex(sourceVertex) && this.containsVertex(targetVertex)) {
            return this.base.getAllEdges(sourceVertex, targetVertex).stream().filter(this.edgeSet::contains).collect(Collectors.toCollection(LinkedHashSet::new));
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
    public E addEdge(V sourceVertex, V targetVertex) {
        this.assertVertexExist(sourceVertex);
        this.assertVertexExist(targetVertex);
        if (!this.base.containsEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(NO_SUCH_EDGE_IN_BASE);
        }
        Set<E> edges = this.base.getAllEdges(sourceVertex, targetVertex);
        for (E e : edges) {
            if (this.containsEdge(e)) continue;
            this.edgeSet.add(e);
            return e;
        }
        return null;
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (!this.base.containsEdge(e)) {
            throw new IllegalArgumentException(NO_SUCH_EDGE_IN_BASE);
        }
        this.assertVertexExist(sourceVertex);
        this.assertVertexExist(targetVertex);
        assert (this.base.getEdgeSource(e) == sourceVertex);
        assert (this.base.getEdgeTarget(e) == targetVertex);
        return this.edgeSet.add(e);
    }

    @Override
    public V addVertex() {
        throw new UnsupportedOperationException(CANNOT_CREATE_NEW_VERTICES_FROM_SUBGRAPH);
    }

    @Override
    public boolean addVertex(V v) {
        if (v == null) {
            throw new NullPointerException();
        }
        if (!this.base.containsVertex(v)) {
            throw new IllegalArgumentException(NO_SUCH_VERTEX_IN_BASE);
        }
        return this.vertexSet.add(v);
    }

    @Override
    public boolean containsEdge(E e) {
        return this.edgeSet.contains(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return this.vertexSet.contains(v);
    }

    @Override
    public Set<E> edgeSet() {
        if (this.unmodifiableEdgeSet == null) {
            this.unmodifiableEdgeSet = Collections.unmodifiableSet(this.edgeSet);
        }
        return this.unmodifiableEdgeSet;
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.base.edgesOf(vertex).stream().filter(this.edgeSet::contains).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public int degreeOf(V vertex) {
        this.assertVertexExist(vertex);
        if (this.baseType.isUndirected()) {
            int degree = 0;
            Iterator it = this.base.edgesOf(vertex).stream().filter(this.edgeSet::contains).iterator();
            while (it.hasNext()) {
                Object e = it.next();
                ++degree;
                if (!this.getEdgeSource(e).equals(this.getEdgeTarget(e))) continue;
                ++degree;
            }
            return degree;
        }
        return this.inDegreeOf(vertex) + this.outDegreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.base.incomingEdgesOf(vertex).stream().filter(this.edgeSet::contains).collect(Collectors.toCollection(LinkedHashSet::new));
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
        return this.base.outgoingEdgesOf(vertex).stream().filter(this.edgeSet::contains).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public int outDegreeOf(V vertex) {
        if (this.baseType.isUndirected()) {
            return this.degreeOf(vertex);
        }
        return this.outgoingEdgesOf(vertex).size();
    }

    @Override
    public boolean removeEdge(E e) {
        return this.edgeSet.remove(e);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        E e = this.getEdge(sourceVertex, targetVertex);
        return (E)(this.edgeSet.remove(e) ? e : null);
    }

    @Override
    public boolean removeVertex(V v) {
        if (this.containsVertex(v) && this.base.containsVertex(v)) {
            this.removeAllEdges(this.edgesOf(v));
        }
        return this.vertexSet.remove(v);
    }

    @Override
    public Set<V> vertexSet() {
        if (this.unmodifiableVertexSet == null) {
            this.unmodifiableVertexSet = Collections.unmodifiableSet(this.vertexSet);
        }
        return this.unmodifiableVertexSet;
    }

    @Override
    public V getEdgeSource(E e) {
        return this.base.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e) {
        return this.base.getEdgeTarget(e);
    }

    @Override
    public GraphType getType() {
        return this.base.getType();
    }

    @Override
    public double getEdgeWeight(E e) {
        return this.base.getEdgeWeight(e);
    }

    @Override
    public void setEdgeWeight(E e, double weight) {
        this.base.setEdgeWeight(e, weight);
    }

    private void initialize(Set<? extends V> vertexFilter, Set<? extends E> edgeFilter) {
        if (vertexFilter == null && edgeFilter == null) {
            this.vertexSet.addAll(this.base.vertexSet());
            this.edgeSet.addAll(this.base.edgeSet());
            return;
        }
        if (vertexFilter == null) {
            this.vertexSet.addAll(this.base.vertexSet());
        } else if (vertexFilter.size() > this.base.vertexSet().size()) {
            this.base.vertexSet().stream().filter(vertexFilter::contains).forEach(this.vertexSet::add);
        } else {
            vertexFilter.stream().filter(v -> v != null && this.base.containsVertex(v)).forEach(this.vertexSet::add);
        }
        if (edgeFilter == null) {
            this.base.edgeSet().stream().filter(e -> this.vertexSet.contains(this.base.getEdgeSource(e)) && this.vertexSet.contains(this.base.getEdgeTarget(e))).forEach(this.edgeSet::add);
        } else if (edgeFilter.size() > this.base.edgeSet().size()) {
            this.base.edgeSet().stream().filter(e -> edgeFilter.contains(e) && this.vertexSet.contains(this.base.getEdgeSource(e)) && this.vertexSet.contains(this.base.getEdgeTarget(e))).forEach(this.edgeSet::add);
        } else {
            edgeFilter.stream().filter(e -> e != null && this.base.containsEdge(e) && this.vertexSet.contains(this.base.getEdgeSource(e)) && this.vertexSet.contains(this.base.getEdgeTarget(e))).forEach(this.edgeSet::add);
        }
    }

    private class BaseGraphListener
    implements GraphListener<V, E>,
    Serializable {
        private static final long serialVersionUID = 4343535244243546391L;

        private BaseGraphListener() {
        }

        @Override
        public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
            if (AsSubgraph.this.isInduced) {
                Object edge = e.getEdge();
                Object source = e.getEdgeSource();
                Object target = e.getEdgeTarget();
                if (AsSubgraph.this.containsVertex(source) && AsSubgraph.this.containsVertex(target)) {
                    AsSubgraph.this.addEdge(source, target, edge);
                }
            }
        }

        @Override
        public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
            Object edge = e.getEdge();
            AsSubgraph.this.removeEdge(edge);
        }

        @Override
        public void vertexAdded(GraphVertexChangeEvent<V> e) {
        }

        @Override
        public void vertexRemoved(GraphVertexChangeEvent<V> e) {
            Object vertex = e.getVertex();
            AsSubgraph.this.removeVertex(vertex);
        }
    }
}

