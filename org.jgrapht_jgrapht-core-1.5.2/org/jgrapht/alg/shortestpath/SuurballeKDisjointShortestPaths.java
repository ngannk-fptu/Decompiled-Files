/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseKDisjointShortestPathsAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

public class SuurballeKDisjointShortestPaths<V, E>
extends BaseKDisjointShortestPathsAlgorithm<V, E> {
    private ShortestPathAlgorithm.SingleSourcePaths<V, E> singleSourcePaths;

    public SuurballeKDisjointShortestPaths(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    protected void transformGraph(List<E> previousPath) {
        for (Object edge : this.workingGraph.edgeSet()) {
            Object source = this.workingGraph.getEdgeSource(edge);
            Object target = this.workingGraph.getEdgeTarget(edge);
            double modifiedWeight = this.workingGraph.getEdgeWeight(edge) - this.singleSourcePaths.getWeight(target) + this.singleSourcePaths.getWeight(source);
            this.workingGraph.setEdgeWeight(edge, modifiedWeight);
        }
        for (E originalEdge : previousPath) {
            double zeroWeight = this.workingGraph.getEdgeWeight(originalEdge);
            if (zeroWeight != 0.0) {
                throw new IllegalStateException("Expected zero weight edge along the path");
            }
            Object source = this.workingGraph.getEdgeSource(originalEdge);
            Object target = this.workingGraph.getEdgeTarget(originalEdge);
            this.workingGraph.removeEdge(originalEdge);
            this.workingGraph.addEdge(target, source);
            Object reversedEdge = this.workingGraph.getEdge(target, source);
            this.workingGraph.setEdgeWeight(reversedEdge, zeroWeight);
        }
    }

    @Override
    protected GraphPath<V, E> calculateShortestPath(V startVertex, V endVertex) {
        this.singleSourcePaths = new DijkstraShortestPath(this.workingGraph).getPaths(startVertex);
        return this.singleSourcePaths.getPath(endVertex);
    }
}

