/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BaseKDisjointShortestPathsAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;

public class BhandariKDisjointShortestPaths<V, E>
extends BaseKDisjointShortestPathsAlgorithm<V, E> {
    public BhandariKDisjointShortestPaths(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    protected void transformGraph(List<E> previousPath) {
        for (E originalEdge : previousPath) {
            Object source = this.workingGraph.getEdgeSource(originalEdge);
            Object target = this.workingGraph.getEdgeTarget(originalEdge);
            double originalEdgeWeight = this.workingGraph.getEdgeWeight(originalEdge);
            this.workingGraph.removeEdge(originalEdge);
            this.workingGraph.addEdge(target, source);
            Object reversedEdge = this.workingGraph.getEdge(target, source);
            this.workingGraph.setEdgeWeight(reversedEdge, -originalEdgeWeight);
        }
    }

    @Override
    protected GraphPath<V, E> calculateShortestPath(V startVertex, V endVertex) {
        return new BellmanFordShortestPath(this.workingGraph).getPath(startVertex, endVertex);
    }
}

