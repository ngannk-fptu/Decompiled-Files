/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.specifics;

import java.util.Set;
import java.util.function.Supplier;

public interface Specifics<V, E> {
    public boolean addVertex(V var1);

    public Set<V> getVertexSet();

    public Set<E> getAllEdges(V var1, V var2);

    public E getEdge(V var1, V var2);

    public boolean addEdgeToTouchingVertices(V var1, V var2, E var3);

    public boolean addEdgeToTouchingVerticesIfAbsent(V var1, V var2, E var3);

    public E createEdgeToTouchingVerticesIfAbsent(V var1, V var2, Supplier<E> var3);

    public int degreeOf(V var1);

    public Set<E> edgesOf(V var1);

    public int inDegreeOf(V var1);

    public Set<E> incomingEdgesOf(V var1);

    public int outDegreeOf(V var1);

    public Set<E> outgoingEdgesOf(V var1);

    public void removeEdgeFromTouchingVertices(V var1, V var2, E var3);
}

