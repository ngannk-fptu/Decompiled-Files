/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.plugin.rest.two.zero.graph;

import com.atlassian.webresource.plugin.rest.two.zero.graph.Requestable;
import com.atlassian.webresource.plugin.rest.two.zero.model.ResourcePhase;

public class RequestableEdge {
    private final Requestable source;
    private final Requestable target;
    private final ResourcePhase phase;

    public RequestableEdge(Requestable source, Requestable target, ResourcePhase phase) {
        this.source = source;
        this.target = target;
        this.phase = phase;
    }

    public Requestable getSource() {
        return this.source;
    }

    public Requestable getTarget() {
        return this.target;
    }

    public ResourcePhase getPhase() {
        return this.phase;
    }
}

