/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.graph.DependencyEdge
 *  com.atlassian.plugin.webresource.graph.DependencyGraph
 *  com.atlassian.plugin.webresource.models.Requestable
 */
package com.atlassian.webresource.plugin.rest.two.zero.graph;

import com.atlassian.plugin.webresource.graph.DependencyEdge;
import com.atlassian.plugin.webresource.graph.DependencyGraph;
import com.atlassian.webresource.plugin.rest.two.zero.graph.Requestable;
import com.atlassian.webresource.plugin.rest.two.zero.graph.RequestableEdge;
import com.atlassian.webresource.plugin.rest.two.zero.model.ResourcePhase;
import java.util.Collection;
import java.util.stream.Collectors;

public class RequestableGraph {
    private final Collection<RequestableEdge> edges;

    public RequestableGraph(DependencyGraph<com.atlassian.plugin.webresource.models.Requestable> graph) {
        this.edges = graph.getEdges().stream().map(RequestableGraph::toDTO).collect(Collectors.toList());
    }

    public Iterable<RequestableEdge> getEdges() {
        return this.edges;
    }

    private static RequestableEdge toDTO(DependencyEdge edge) {
        ResourcePhase phaseDTO = null;
        return new RequestableEdge(RequestableGraph.toDTO((com.atlassian.plugin.webresource.models.Requestable)edge.getSource()), RequestableGraph.toDTO((com.atlassian.plugin.webresource.models.Requestable)edge.getTarget()), phaseDTO);
    }

    private static Requestable toDTO(com.atlassian.plugin.webresource.models.Requestable node) {
        return new Requestable(node.toLooseType());
    }
}

