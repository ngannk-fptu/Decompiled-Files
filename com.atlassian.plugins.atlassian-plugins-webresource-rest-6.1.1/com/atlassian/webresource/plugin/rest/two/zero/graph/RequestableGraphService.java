/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.plugin.rest.two.zero.graph;

import com.atlassian.webresource.plugin.rest.two.zero.graph.RequestableGraph;

public interface RequestableGraphService {
    public boolean hasById(String var1);

    public RequestableGraph getConsumerGraphById(String var1);

    public RequestableGraph getDependencyGraphById(String var1);
}

