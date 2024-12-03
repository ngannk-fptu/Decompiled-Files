/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.specifics;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import org.jgrapht.graph.EdgeSetFactory;

public class UndirectedEdgeContainer<V, E>
implements Serializable {
    private static final long serialVersionUID = -6623207588411170010L;
    Set<E> vertexEdges;
    private transient Set<E> unmodifiableVertexEdges = null;

    UndirectedEdgeContainer(EdgeSetFactory<V, E> edgeSetFactory, V vertex) {
        this.vertexEdges = edgeSetFactory.createEdgeSet(vertex);
    }

    public Set<E> getUnmodifiableVertexEdges() {
        if (this.unmodifiableVertexEdges == null) {
            this.unmodifiableVertexEdges = Collections.unmodifiableSet(this.vertexEdges);
        }
        return this.unmodifiableVertexEdges;
    }

    public void addEdge(E e) {
        this.vertexEdges.add(e);
    }

    public int edgeCount() {
        return this.vertexEdges.size();
    }

    public void removeEdge(E e) {
        this.vertexEdges.remove(e);
    }
}

