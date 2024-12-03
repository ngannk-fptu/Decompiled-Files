/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

public class GraphCycleProhibitedException
extends IllegalArgumentException {
    private static final long serialVersionUID = 2440845437318796595L;

    public GraphCycleProhibitedException() {
        super("Edge would induce a cycle");
    }
}

