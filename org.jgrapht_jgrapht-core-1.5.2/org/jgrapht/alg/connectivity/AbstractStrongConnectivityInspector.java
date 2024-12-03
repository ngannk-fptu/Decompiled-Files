/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.util.CollectionUtil;

abstract class AbstractStrongConnectivityInspector<V, E>
implements StrongConnectivityAlgorithm<V, E> {
    protected final Graph<V, E> graph;
    protected List<Set<V>> stronglyConnectedSets;
    protected List<Graph<V, E>> stronglyConnectedSubgraphs;

    protected AbstractStrongConnectivityInspector(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph);
    }

    @Override
    public Graph<V, E> getGraph() {
        return this.graph;
    }

    @Override
    public boolean isStronglyConnected() {
        return this.stronglyConnectedSets().size() == 1;
    }

    @Override
    public List<Graph<V, E>> getStronglyConnectedComponents() {
        if (this.stronglyConnectedSubgraphs == null) {
            List sets = this.stronglyConnectedSets();
            this.stronglyConnectedSubgraphs = new ArrayList<Graph<V, E>>(sets.size());
            for (Set set : sets) {
                this.stronglyConnectedSubgraphs.add(new AsSubgraph<V, E>(this.graph, set, null));
            }
        }
        return this.stronglyConnectedSubgraphs;
    }

    @Override
    public Graph<Graph<V, E>, DefaultEdge> getCondensation() {
        List sets = this.stronglyConnectedSets();
        SimpleDirectedGraph<Graph<Graph, E>, DefaultEdge> condensation = new SimpleDirectedGraph<Graph<Graph, E>, DefaultEdge>(DefaultEdge.class);
        HashMap vertexToComponent = CollectionUtil.newHashMapWithExpectedSize(this.graph.vertexSet().size());
        for (Set set : sets) {
            AsSubgraph<V, E> component = new AsSubgraph<V, E>(this.graph, set, null);
            condensation.addVertex(component);
            for (Object v : set) {
                vertexToComponent.put(v, component);
            }
        }
        for (Set<Object> e : this.graph.edgeSet()) {
            V t;
            Graph tComponent;
            V s = this.graph.getEdgeSource(e);
            Graph sComponent = (Graph)vertexToComponent.get(s);
            if (sComponent == (tComponent = (Graph)vertexToComponent.get(t = this.graph.getEdgeTarget(e)))) continue;
            condensation.addEdge(sComponent, tComponent);
        }
        return condensation;
    }
}

