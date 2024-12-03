/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.connectivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class BlockCutpointGraph<V, E>
extends SimpleGraph<Graph<V, E>, DefaultEdge> {
    private static final long serialVersionUID = -9101341117013163934L;
    private Graph<V, E> graph;
    private Set<V> cutpoints;
    private Set<Graph<V, E>> blocks;
    private Map<V, Graph<V, E>> vertex2block = new HashMap<V, Graph<V, E>>();

    public BlockCutpointGraph(Graph<V, E> graph) {
        super(DefaultEdge.class);
        this.graph = graph;
        BiconnectivityInspector<Graph<V, E>, E> biconnectivityInspector = new BiconnectivityInspector<Graph<V, E>, E>(graph);
        this.cutpoints = biconnectivityInspector.getCutpoints();
        this.blocks = biconnectivityInspector.getBlocks();
        for (Graph<V, E> block : this.blocks) {
            for (V v : block.vertexSet()) {
                this.vertex2block.put((Graph<V, E>)v, (Graph<Graph<V, E>, E>)block);
            }
        }
        Graphs.addAllVertices(this, this.blocks);
        for (Graph<V, E> cutpoint : this.cutpoints) {
            AsSubgraph<Graph<V, E>, E> subgraph = new AsSubgraph<Graph<V, E>, E>(graph, Collections.singleton(cutpoint));
            this.vertex2block.put((AsSubgraph<Graph<V, E>, E>)cutpoint, (Graph<AsSubgraph<Graph<V, E>, E>, E>)subgraph);
            this.addVertex(subgraph);
            for (Graph<Graph<V, E>, E> block : biconnectivityInspector.getBlocks(cutpoint)) {
                this.addEdge(subgraph, block);
            }
        }
    }

    public Graph<V, E> getBlock(V vertex) {
        assert (this.graph.containsVertex(vertex));
        return this.vertex2block.get(vertex);
    }

    public Set<Graph<V, E>> getBlocks() {
        return this.blocks;
    }

    public Set<V> getCutpoints() {
        return this.cutpoints;
    }

    public boolean isCutpoint(V vertex) {
        return this.cutpoints.contains(vertex);
    }
}

