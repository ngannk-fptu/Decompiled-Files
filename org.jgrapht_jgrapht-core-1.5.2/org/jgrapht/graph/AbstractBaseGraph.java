/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphIterables;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultGraphIterables;
import org.jgrapht.graph.FastLookupGraphSpecificsStrategy;
import org.jgrapht.graph.GraphSpecificsStrategy;
import org.jgrapht.graph.IntrusiveEdgesSpecifics;
import org.jgrapht.graph.specifics.Specifics;
import org.jgrapht.util.TypeUtil;

public abstract class AbstractBaseGraph<V, E>
extends AbstractGraph<V, E>
implements Graph<V, E>,
Cloneable,
Serializable {
    private static final long serialVersionUID = -3582386521833998627L;
    private static final String LOOPS_NOT_ALLOWED = "loops not allowed";
    private static final String GRAPH_SPECIFICS_MUST_NOT_BE_NULL = "Graph specifics must not be null";
    private static final String INVALID_VERTEX_SUPPLIER_DOES_NOT_RETURN_UNIQUE_VERTICES_ON_EACH_CALL = "Invalid vertex supplier (does not return unique vertices on each call).";
    private static final String MIXED_GRAPH_NOT_SUPPORTED = "Mixed graph not supported";
    private static final String GRAPH_SPECIFICS_STRATEGY_REQUIRED = "Graph specifics strategy required";
    private static final String THE_GRAPH_CONTAINS_NO_VERTEX_SUPPLIER = "The graph contains no vertex supplier";
    private static final String THE_GRAPH_CONTAINS_NO_EDGE_SUPPLIER = "The graph contains no edge supplier";
    private transient Set<V> unmodifiableVertexSet = null;
    private Supplier<V> vertexSupplier;
    private Supplier<E> edgeSupplier;
    private GraphType type;
    private Specifics<V, E> specifics;
    private IntrusiveEdgesSpecifics<V, E> intrusiveEdgesSpecifics;
    private GraphSpecificsStrategy<V, E> graphSpecificsStrategy;
    private transient GraphIterables<V, E> graphIterables = null;

    protected AbstractBaseGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, GraphType type) {
        this(vertexSupplier, edgeSupplier, type, new FastLookupGraphSpecificsStrategy());
    }

    protected AbstractBaseGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, GraphType type, GraphSpecificsStrategy<V, E> graphSpecificsStrategy) {
        this.vertexSupplier = vertexSupplier;
        this.edgeSupplier = edgeSupplier;
        this.type = Objects.requireNonNull(type);
        if (type.isMixed()) {
            throw new IllegalArgumentException(MIXED_GRAPH_NOT_SUPPORTED);
        }
        this.graphSpecificsStrategy = Objects.requireNonNull(graphSpecificsStrategy, GRAPH_SPECIFICS_STRATEGY_REQUIRED);
        this.specifics = Objects.requireNonNull(graphSpecificsStrategy.getSpecificsFactory().apply(this, type), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);
        this.intrusiveEdgesSpecifics = Objects.requireNonNull(graphSpecificsStrategy.getIntrusiveEdgesSpecificsFactory().apply(type), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        return this.specifics.getAllEdges(sourceVertex, targetVertex);
    }

    @Override
    public Supplier<E> getEdgeSupplier() {
        return this.edgeSupplier;
    }

    public void setEdgeSupplier(Supplier<E> edgeSupplier) {
        this.edgeSupplier = edgeSupplier;
    }

    @Override
    public Supplier<V> getVertexSupplier() {
        return this.vertexSupplier;
    }

    public void setVertexSupplier(Supplier<V> vertexSupplier) {
        this.vertexSupplier = vertexSupplier;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        return this.specifics.getEdge(sourceVertex, targetVertex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        this.assertVertexExist(sourceVertex);
        this.assertVertexExist(targetVertex);
        if (!this.type.isAllowingSelfLoops() && sourceVertex.equals(targetVertex)) {
            throw new IllegalArgumentException(LOOPS_NOT_ALLOWED);
        }
        if (this.edgeSupplier == null) {
            throw new UnsupportedOperationException(THE_GRAPH_CONTAINS_NO_EDGE_SUPPLIER);
        }
        if (!this.type.isAllowingMultipleEdges()) {
            E e = this.specifics.createEdgeToTouchingVerticesIfAbsent(sourceVertex, targetVertex, this.edgeSupplier);
            if (e != null) {
                boolean edgeAdded = false;
                try {
                    edgeAdded = this.intrusiveEdgesSpecifics.add(e, sourceVertex, targetVertex);
                }
                finally {
                    if (!edgeAdded) {
                        this.specifics.removeEdgeFromTouchingVertices(sourceVertex, targetVertex, e);
                    }
                }
                if (edgeAdded) {
                    return e;
                }
            }
        } else {
            E e = this.edgeSupplier.get();
            if (this.intrusiveEdgesSpecifics.add(e, sourceVertex, targetVertex)) {
                this.specifics.addEdgeToTouchingVertices(sourceVertex, targetVertex, e);
                return e;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        this.assertVertexExist(sourceVertex);
        this.assertVertexExist(targetVertex);
        if (!this.type.isAllowingSelfLoops() && sourceVertex.equals(targetVertex)) {
            throw new IllegalArgumentException(LOOPS_NOT_ALLOWED);
        }
        if (!this.type.isAllowingMultipleEdges()) {
            if (!this.specifics.addEdgeToTouchingVerticesIfAbsent(sourceVertex, targetVertex, e)) {
                return false;
            }
            boolean edgeAdded = false;
            try {
                edgeAdded = this.intrusiveEdgesSpecifics.add(e, sourceVertex, targetVertex);
            }
            finally {
                if (!edgeAdded) {
                    this.specifics.removeEdgeFromTouchingVertices(sourceVertex, targetVertex, e);
                }
            }
            return edgeAdded;
        }
        if (this.intrusiveEdgesSpecifics.add(e, sourceVertex, targetVertex)) {
            this.specifics.addEdgeToTouchingVertices(sourceVertex, targetVertex, e);
            return true;
        }
        return false;
    }

    @Override
    public V addVertex() {
        if (this.vertexSupplier == null) {
            throw new UnsupportedOperationException(THE_GRAPH_CONTAINS_NO_VERTEX_SUPPLIER);
        }
        V v = this.vertexSupplier.get();
        if (!this.specifics.addVertex(v)) {
            throw new IllegalArgumentException(INVALID_VERTEX_SUPPLIER_DOES_NOT_RETURN_UNIQUE_VERTICES_ON_EACH_CALL);
        }
        return v;
    }

    @Override
    public boolean addVertex(V v) {
        if (v == null) {
            throw new NullPointerException();
        }
        if (this.containsVertex(v)) {
            return false;
        }
        this.specifics.addVertex(v);
        return true;
    }

    @Override
    public V getEdgeSource(E e) {
        return this.intrusiveEdgesSpecifics.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e) {
        return this.intrusiveEdgesSpecifics.getEdgeTarget(e);
    }

    public Object clone() {
        try {
            AbstractBaseGraph newGraph = (AbstractBaseGraph)TypeUtil.uncheckedCast(super.clone());
            newGraph.vertexSupplier = this.vertexSupplier;
            newGraph.edgeSupplier = this.edgeSupplier;
            newGraph.type = this.type;
            newGraph.unmodifiableVertexSet = null;
            newGraph.graphSpecificsStrategy = this.graphSpecificsStrategy;
            newGraph.specifics = newGraph.graphSpecificsStrategy.getSpecificsFactory().apply(newGraph, newGraph.type);
            newGraph.intrusiveEdgesSpecifics = newGraph.graphSpecificsStrategy.getIntrusiveEdgesSpecificsFactory().apply(newGraph.type);
            newGraph.graphIterables = null;
            Graphs.addGraph(newGraph, this);
            return newGraph;
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public boolean containsEdge(E e) {
        return this.intrusiveEdgesSpecifics.containsEdge(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return this.specifics.getVertexSet().contains(v);
    }

    @Override
    public int degreeOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.specifics.degreeOf(vertex);
    }

    @Override
    public Set<E> edgeSet() {
        return this.intrusiveEdgesSpecifics.getEdgeSet();
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.specifics.edgesOf(vertex);
    }

    @Override
    public int inDegreeOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.specifics.inDegreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.specifics.incomingEdgesOf(vertex);
    }

    @Override
    public int outDegreeOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.specifics.outDegreeOf(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        this.assertVertexExist(vertex);
        return this.specifics.outgoingEdgesOf(vertex);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        E e = this.getEdge(sourceVertex, targetVertex);
        if (e != null) {
            this.specifics.removeEdgeFromTouchingVertices(sourceVertex, targetVertex, e);
            this.intrusiveEdgesSpecifics.remove(e);
        }
        return e;
    }

    @Override
    public boolean removeEdge(E e) {
        if (this.containsEdge(e)) {
            V sourceVertex = this.getEdgeSource(e);
            V targetVertex = this.getEdgeTarget(e);
            this.specifics.removeEdgeFromTouchingVertices(sourceVertex, targetVertex, e);
            this.intrusiveEdgesSpecifics.remove(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeVertex(V v) {
        if (this.containsVertex(v)) {
            Set<E> touchingEdgesList = this.edgesOf(v);
            this.removeAllEdges(new ArrayList<E>(touchingEdgesList));
            this.specifics.getVertexSet().remove(v);
            return true;
        }
        return false;
    }

    @Override
    public Set<V> vertexSet() {
        if (this.unmodifiableVertexSet == null) {
            this.unmodifiableVertexSet = Collections.unmodifiableSet(this.specifics.getVertexSet());
        }
        return this.unmodifiableVertexSet;
    }

    @Override
    public double getEdgeWeight(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        return this.intrusiveEdgesSpecifics.getEdgeWeight(e);
    }

    @Override
    public void setEdgeWeight(E e, double weight) {
        if (e == null) {
            throw new NullPointerException();
        }
        this.intrusiveEdgesSpecifics.setEdgeWeight(e, weight);
    }

    @Override
    public GraphType getType() {
        return this.type;
    }

    @Override
    public GraphIterables<V, E> iterables() {
        if (this.graphIterables == null) {
            this.graphIterables = new DefaultGraphIterables(this);
        }
        return this.graphIterables;
    }
}

