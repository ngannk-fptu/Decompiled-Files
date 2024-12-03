/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.vertexcover;

import java.util.LinkedHashSet;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.graph.AsSubgraph;

public class EdgeBasedTwoApproxVCImpl<V, E>
implements VertexCoverAlgorithm<V> {
    private final Graph<V, E> graph;

    public EdgeBasedTwoApproxVCImpl(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
    }

    @Override
    public VertexCoverAlgorithm.VertexCover<V> getVertexCover() {
        LinkedHashSet<V> cover = new LinkedHashSet<V>();
        AsSubgraph<V, E> sg = new AsSubgraph<V, E>(this.graph, null, null);
        while (sg.edgeSet().size() != 0) {
            Object e = sg.edgeSet().iterator().next();
            V u = this.graph.getEdgeSource(e);
            V v = this.graph.getEdgeTarget(e);
            cover.add(u);
            cover.add(v);
            sg.removeVertex(u);
            sg.removeVertex(v);
        }
        return new VertexCoverAlgorithm.VertexCoverImpl(cover);
    }
}

