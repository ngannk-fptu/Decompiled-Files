/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import org.jgrapht.GraphPath;

public class NegativeCycleDetectedException
extends RuntimeException {
    private static final long serialVersionUID = -8064609917721881630L;
    private GraphPath<?, ?> cycle;

    public NegativeCycleDetectedException() {
    }

    public NegativeCycleDetectedException(String message) {
        super(message);
    }

    public NegativeCycleDetectedException(String message, GraphPath<?, ?> cycle) {
        super(message);
        this.cycle = cycle;
    }

    public GraphPath<?, ?> getCycle() {
        return this.cycle;
    }

    public void setCycle(GraphPath<?, ?> cycle) {
        this.cycle = cycle;
    }
}

