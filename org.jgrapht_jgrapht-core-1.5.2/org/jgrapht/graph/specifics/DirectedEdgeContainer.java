/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.specifics;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import org.jgrapht.graph.EdgeSetFactory;

public class DirectedEdgeContainer<V, E>
implements Serializable {
    private static final long serialVersionUID = 7494242245729767106L;
    Set<E> incoming;
    Set<E> outgoing;
    private transient Set<E> unmodifiableIncoming = null;
    private transient Set<E> unmodifiableOutgoing = null;

    DirectedEdgeContainer(EdgeSetFactory<V, E> edgeSetFactory, V vertex) {
        this.incoming = edgeSetFactory.createEdgeSet(vertex);
        this.outgoing = edgeSetFactory.createEdgeSet(vertex);
    }

    public Set<E> getUnmodifiableIncomingEdges() {
        if (this.unmodifiableIncoming == null) {
            this.unmodifiableIncoming = Collections.unmodifiableSet(this.incoming);
        }
        return this.unmodifiableIncoming;
    }

    public Set<E> getUnmodifiableOutgoingEdges() {
        if (this.unmodifiableOutgoing == null) {
            this.unmodifiableOutgoing = Collections.unmodifiableSet(this.outgoing);
        }
        return this.unmodifiableOutgoing;
    }

    public void addIncomingEdge(E e) {
        this.incoming.add(e);
    }

    public void addOutgoingEdge(E e) {
        this.outgoing.add(e);
    }

    public void removeIncomingEdge(E e) {
        this.incoming.remove(e);
    }

    public void removeOutgoingEdge(E e) {
        this.outgoing.remove(e);
    }
}

