/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import org.jgrapht.graph.IntrusiveWeightedEdge;

public class DefaultWeightedEdge
extends IntrusiveWeightedEdge {
    private static final long serialVersionUID = -3259071493169286685L;

    protected Object getSource() {
        return this.source;
    }

    protected Object getTarget() {
        return this.target;
    }

    protected double getWeight() {
        return this.weight;
    }

    public String toString() {
        return "(" + this.source + " : " + this.target + ")";
    }
}

