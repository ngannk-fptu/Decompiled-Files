/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.event;

import org.jgrapht.event.GraphChangeEvent;

public class GraphEdgeChangeEvent<V, E>
extends GraphChangeEvent {
    private static final long serialVersionUID = -4421610303769803253L;
    public static final int BEFORE_EDGE_ADDED = 21;
    public static final int BEFORE_EDGE_REMOVED = 22;
    public static final int EDGE_ADDED = 23;
    public static final int EDGE_REMOVED = 24;
    public static final int EDGE_WEIGHT_UPDATED = 25;
    protected E edge;
    protected V edgeSource;
    protected V edgeTarget;
    protected double edgeWeight;

    public GraphEdgeChangeEvent(Object eventSource, int type, E edge, V edgeSource, V edgeTarget) {
        this(eventSource, type, edge, edgeSource, edgeTarget, 1.0);
    }

    public GraphEdgeChangeEvent(Object eventSource, int type, E edge, V edgeSource, V edgeTarget, double edgeWeight) {
        super(eventSource, type);
        this.edge = edge;
        this.edgeSource = edgeSource;
        this.edgeTarget = edgeTarget;
        this.edgeWeight = edgeWeight;
    }

    public E getEdge() {
        return this.edge;
    }

    public V getEdgeSource() {
        return this.edgeSource;
    }

    public V getEdgeTarget() {
        return this.edgeTarget;
    }

    public double getEdgeWeight() {
        return this.edgeWeight;
    }
}

