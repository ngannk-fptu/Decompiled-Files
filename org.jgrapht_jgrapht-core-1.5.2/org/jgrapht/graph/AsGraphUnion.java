/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.util.UnmodifiableUnionSet;
import org.jgrapht.util.WeightCombiner;

public class AsGraphUnion<V, E>
extends AbstractGraph<V, E>
implements Serializable {
    private static final long serialVersionUID = -3848082143382987713L;
    private static final String READ_ONLY = "union of graphs is read-only";
    private final Graph<V, E> g1;
    private final GraphType type1;
    private final Graph<V, E> g2;
    private final GraphType type2;
    private final GraphType type;
    private final WeightCombiner operator;

    public AsGraphUnion(Graph<V, E> g1, Graph<V, E> g2, WeightCombiner operator) {
        this.g1 = GraphTests.requireDirectedOrUndirected(g1);
        this.type1 = g1.getType();
        this.g2 = GraphTests.requireDirectedOrUndirected(g2);
        this.type2 = g2.getType();
        if (g1 == g2) {
            throw new IllegalArgumentException("g1 is equal to g2");
        }
        this.operator = Objects.requireNonNull(operator, "Weight combiner cannot be null");
        DefaultGraphType.Builder builder = new DefaultGraphType.Builder();
        builder = this.type1.isDirected() && this.type2.isDirected() ? builder.directed() : (this.type1.isUndirected() && this.type2.isUndirected() ? builder.undirected() : builder.mixed());
        this.type = builder.allowSelfLoops(this.type1.isAllowingSelfLoops() || this.type2.isAllowingSelfLoops()).allowMultipleEdges(true).weighted(true).modifiable(false).build();
    }

    public AsGraphUnion(Graph<V, E> g1, Graph<V, E> g2) {
        this(g1, g2, WeightCombiner.SUM);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        boolean inG2;
        boolean inG1 = this.g1.containsVertex(sourceVertex) && this.g1.containsVertex(targetVertex);
        boolean bl = inG2 = this.g2.containsVertex(sourceVertex) && this.g2.containsVertex(targetVertex);
        if (inG1 && inG2) {
            return new UnmodifiableUnionSet<E>(this.g1.getAllEdges(sourceVertex, targetVertex), this.g2.getAllEdges(sourceVertex, targetVertex));
        }
        if (inG1) {
            return Collections.unmodifiableSet(this.g1.getAllEdges(sourceVertex, targetVertex));
        }
        if (inG2) {
            return Collections.unmodifiableSet(this.g2.getAllEdges(sourceVertex, targetVertex));
        }
        return Collections.emptySet();
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        E res = null;
        if (this.g1.containsVertex(sourceVertex) && this.g1.containsVertex(targetVertex)) {
            res = this.g1.getEdge(sourceVertex, targetVertex);
        }
        if (res == null && this.g2.containsVertex(sourceVertex) && this.g2.containsVertex(targetVertex)) {
            res = this.g2.getEdge(sourceVertex, targetVertex);
        }
        return res;
    }

    @Override
    public Supplier<V> getVertexSupplier() {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public Supplier<E> getEdgeSupplier() {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public V addVertex() {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public boolean addVertex(V v) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public boolean containsEdge(E e) {
        return this.g1.containsEdge(e) || this.g2.containsEdge(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return this.g1.containsVertex(v) || this.g2.containsVertex(v);
    }

    @Override
    public Set<E> edgeSet() {
        return new UnmodifiableUnionSet<E>(this.g1.edgeSet(), this.g2.edgeSet());
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        boolean inG1 = this.g1.containsVertex(vertex);
        boolean inG2 = this.g2.containsVertex(vertex);
        if (inG1 && inG2) {
            return new UnmodifiableUnionSet<E>(this.g1.edgesOf(vertex), this.g2.edgesOf(vertex));
        }
        if (inG1) {
            return Collections.unmodifiableSet(this.g1.edgesOf(vertex));
        }
        if (inG2) {
            return Collections.unmodifiableSet(this.g2.edgesOf(vertex));
        }
        throw new IllegalArgumentException("no such vertex in graph: " + vertex.toString());
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        boolean inG1 = this.g1.containsVertex(vertex);
        boolean inG2 = this.g2.containsVertex(vertex);
        if (inG1 && inG2) {
            return new UnmodifiableUnionSet<E>(this.g1.incomingEdgesOf(vertex), this.g2.incomingEdgesOf(vertex));
        }
        if (inG1) {
            return Collections.unmodifiableSet(this.g1.incomingEdgesOf(vertex));
        }
        if (inG2) {
            return Collections.unmodifiableSet(this.g2.incomingEdgesOf(vertex));
        }
        throw new IllegalArgumentException("no such vertex in graph: " + vertex.toString());
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        boolean inG1 = this.g1.containsVertex(vertex);
        boolean inG2 = this.g2.containsVertex(vertex);
        if (inG1 && inG2) {
            return new UnmodifiableUnionSet<E>(this.g1.outgoingEdgesOf(vertex), this.g2.outgoingEdgesOf(vertex));
        }
        if (inG1) {
            return Collections.unmodifiableSet(this.g1.outgoingEdgesOf(vertex));
        }
        if (inG2) {
            return Collections.unmodifiableSet(this.g2.outgoingEdgesOf(vertex));
        }
        throw new IllegalArgumentException("no such vertex in graph: " + vertex.toString());
    }

    @Override
    public int degreeOf(V vertex) {
        if (this.type.isMixed()) {
            int d = 0;
            if (this.g1.containsVertex(vertex)) {
                d += this.g1.degreeOf(vertex);
            }
            if (this.g2.containsVertex(vertex)) {
                d += this.g2.degreeOf(vertex);
            }
            return d;
        }
        if (this.type.isUndirected()) {
            int degree = 0;
            for (E e : this.edgesOf(vertex)) {
                ++degree;
                if (!this.getEdgeSource(e).equals(this.getEdgeTarget(e))) continue;
                ++degree;
            }
            return degree;
        }
        return this.incomingEdgesOf(vertex).size() + this.outgoingEdgesOf(vertex).size();
    }

    @Override
    public int inDegreeOf(V vertex) {
        if (this.type.isMixed()) {
            int d = 0;
            if (this.g1.containsVertex(vertex)) {
                d += this.g1.inDegreeOf(vertex);
            }
            if (this.g2.containsVertex(vertex)) {
                d += this.g2.inDegreeOf(vertex);
            }
            return d;
        }
        if (this.type.isUndirected()) {
            return this.degreeOf(vertex);
        }
        return this.incomingEdgesOf(vertex).size();
    }

    @Override
    public int outDegreeOf(V vertex) {
        if (this.type.isMixed()) {
            int d = 0;
            if (this.g1.containsVertex(vertex)) {
                d += this.g1.outDegreeOf(vertex);
            }
            if (this.g2.containsVertex(vertex)) {
                d += this.g2.outDegreeOf(vertex);
            }
            return d;
        }
        if (this.type.isUndirected()) {
            return this.degreeOf(vertex);
        }
        return this.outgoingEdgesOf(vertex).size();
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public boolean removeEdge(E e) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public boolean removeVertex(V v) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public Set<V> vertexSet() {
        return new UnmodifiableUnionSet<V>(this.g1.vertexSet(), this.g2.vertexSet());
    }

    @Override
    public V getEdgeSource(E e) {
        if (this.g1.containsEdge(e)) {
            return this.g1.getEdgeSource(e);
        }
        if (this.g2.containsEdge(e)) {
            return this.g2.getEdgeSource(e);
        }
        return null;
    }

    @Override
    public V getEdgeTarget(E e) {
        if (this.g1.containsEdge(e)) {
            return this.g1.getEdgeTarget(e);
        }
        if (this.g2.containsEdge(e)) {
            return this.g2.getEdgeTarget(e);
        }
        return null;
    }

    @Override
    public double getEdgeWeight(E e) {
        if (this.g1.containsEdge(e) && this.g2.containsEdge(e)) {
            return this.operator.combine(this.g1.getEdgeWeight(e), this.g2.getEdgeWeight(e));
        }
        if (this.g1.containsEdge(e)) {
            return this.g1.getEdgeWeight(e);
        }
        if (this.g2.containsEdge(e)) {
            return this.g2.getEdgeWeight(e);
        }
        throw new IllegalArgumentException("no such edge in the union");
    }

    @Override
    public GraphType getType() {
        return this.type;
    }

    @Override
    public void setEdgeWeight(E e, double weight) {
        throw new UnsupportedOperationException(READ_ONLY);
    }
}

