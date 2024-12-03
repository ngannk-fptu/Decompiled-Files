/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.GraphIterables;
import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultGraphIterables;

public interface Graph<V, E> {
    public static final double DEFAULT_EDGE_WEIGHT = 1.0;

    public Set<E> getAllEdges(V var1, V var2);

    public E getEdge(V var1, V var2);

    public Supplier<V> getVertexSupplier();

    public Supplier<E> getEdgeSupplier();

    public E addEdge(V var1, V var2);

    public boolean addEdge(V var1, V var2, E var3);

    public V addVertex();

    public boolean addVertex(V var1);

    public boolean containsEdge(V var1, V var2);

    public boolean containsEdge(E var1);

    public boolean containsVertex(V var1);

    public Set<E> edgeSet();

    public int degreeOf(V var1);

    public Set<E> edgesOf(V var1);

    public int inDegreeOf(V var1);

    public Set<E> incomingEdgesOf(V var1);

    public int outDegreeOf(V var1);

    public Set<E> outgoingEdgesOf(V var1);

    public boolean removeAllEdges(Collection<? extends E> var1);

    public Set<E> removeAllEdges(V var1, V var2);

    public boolean removeAllVertices(Collection<? extends V> var1);

    public E removeEdge(V var1, V var2);

    public boolean removeEdge(E var1);

    public boolean removeVertex(V var1);

    public Set<V> vertexSet();

    public V getEdgeSource(E var1);

    public V getEdgeTarget(E var1);

    public GraphType getType();

    public double getEdgeWeight(E var1);

    public void setEdgeWeight(E var1, double var2);

    default public void setEdgeWeight(V sourceVertex, V targetVertex, double weight) {
        this.setEdgeWeight(this.getEdge(sourceVertex, targetVertex), weight);
    }

    default public GraphIterables<V, E> iterables() {
        return new DefaultGraphIterables(this);
    }
}

