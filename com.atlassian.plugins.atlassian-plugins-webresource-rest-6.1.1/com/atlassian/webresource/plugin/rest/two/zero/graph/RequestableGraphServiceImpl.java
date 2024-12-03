/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.graph.DependencyGraph
 *  com.atlassian.plugin.webresource.graph.RequestableKeyValidator
 *  com.atlassian.plugin.webresource.impl.config.Config
 *  com.atlassian.plugin.webresource.models.Requestable
 *  com.atlassian.plugin.webresource.models.WebResourceContextKey
 *  com.atlassian.plugin.webresource.models.WebResourceKey
 */
package com.atlassian.webresource.plugin.rest.two.zero.graph;

import com.atlassian.plugin.webresource.graph.DependencyGraph;
import com.atlassian.plugin.webresource.graph.RequestableKeyValidator;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.webresource.plugin.rest.two.zero.graph.RequestableGraph;
import com.atlassian.webresource.plugin.rest.two.zero.graph.RequestableGraphService;

public class RequestableGraphServiceImpl
implements RequestableGraphService {
    @Override
    public boolean hasById(String requestableId) {
        Requestable key = this.create(requestableId);
        return this.getGraph().hasDependency((Object)key);
    }

    @Override
    public RequestableGraph getConsumerGraphById(String requestableId) {
        Requestable key = this.create(requestableId);
        DependencyGraph ancestorGraph = this.getGraph().findDependantsSubGraphByKey((Object)key);
        return new RequestableGraph((DependencyGraph<Requestable>)ancestorGraph);
    }

    @Override
    public RequestableGraph getDependencyGraphById(String requestableId) {
        Requestable key = this.create(requestableId);
        DependencyGraph dependencyGraph = this.getGraph().findDependencySubGraphByRequestableKey((Object)key);
        return new RequestableGraph((DependencyGraph<Requestable>)dependencyGraph);
    }

    private Requestable create(String key) {
        if (RequestableKeyValidator.isWebResourceContext((String)key)) {
            return new WebResourceContextKey(key);
        }
        return new WebResourceKey(key);
    }

    private DependencyGraph<Requestable> getGraph() {
        return Config.getDependencyGraph();
    }
}

