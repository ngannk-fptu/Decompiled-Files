/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.ContractionHierarchyBidirectionalDijkstra;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.jgrapht.alg.shortestpath.TransitNodeRoutingPrecomputation;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;

public class TransitNodeRoutingShortestPath<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    private ThreadPoolExecutor executor;
    private ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> contractionHierarchy;
    private ShortestPathAlgorithm<V, E> localQueriesAlgorithm;
    private ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> manyToManyShortestPaths;
    private TransitNodeRoutingPrecomputation.AccessVertices<V, E> accessVertices;
    private TransitNodeRoutingPrecomputation.LocalityFilter<V> localityFilter;

    public TransitNodeRoutingShortestPath(Graph<V, E> graph, ThreadPoolExecutor executor) {
        super(graph);
        this.executor = Objects.requireNonNull(executor, "executor cannot be null!");
    }

    TransitNodeRoutingShortestPath(TransitNodeRoutingPrecomputation.TransitNodeRouting<V, E> transitNodeRouting) {
        super(transitNodeRouting.getContractionHierarchy().getGraph());
        this.initialize(transitNodeRouting);
    }

    public void performPrecomputation() {
        if (this.contractionHierarchy != null) {
            return;
        }
        TransitNodeRoutingPrecomputation.TransitNodeRouting routing = new TransitNodeRoutingPrecomputation(this.graph, this.executor).computeTransitNodeRouting();
        this.initialize(routing);
    }

    private void initialize(TransitNodeRoutingPrecomputation.TransitNodeRouting<V, E> transitNodeRouting) {
        this.contractionHierarchy = transitNodeRouting.getContractionHierarchy();
        this.localityFilter = transitNodeRouting.getLocalityFilter();
        this.accessVertices = transitNodeRouting.getAccessVertices();
        this.manyToManyShortestPaths = transitNodeRouting.getTransitVerticesPaths();
        this.localQueriesAlgorithm = new ContractionHierarchyBidirectionalDijkstra<V, E>(transitNodeRouting.getContractionHierarchy());
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        this.performPrecomputation();
        if (this.localityFilter.isLocal(source, sink)) {
            return this.localQueriesAlgorithm.getPath(source, sink);
        }
        Pair<TransitNodeRoutingPrecomputation.AccessVertex<V, E>, TransitNodeRoutingPrecomputation.AccessVertex<V, E>> p = this.getMinWeightAccessVertices(source, sink);
        TransitNodeRoutingPrecomputation.AccessVertex<V, E> forwardAccessVertex = p.getFirst();
        TransitNodeRoutingPrecomputation.AccessVertex<V, E> backwardAccessVertex = p.getSecond();
        if (forwardAccessVertex == null) {
            return this.createEmptyPath(source, sink);
        }
        return this.mergePaths(forwardAccessVertex.getPath(), this.manyToManyShortestPaths.getPath(forwardAccessVertex.getVertex(), backwardAccessVertex.getVertex()), backwardAccessVertex.getPath());
    }

    @Override
    public double getPathWeight(V source, V sink) {
        this.performPrecomputation();
        if (this.localityFilter.isLocal(source, sink)) {
            return this.localQueriesAlgorithm.getPathWeight(source, sink);
        }
        Pair<TransitNodeRoutingPrecomputation.AccessVertex<V, E>, TransitNodeRoutingPrecomputation.AccessVertex<V, E>> p = this.getMinWeightAccessVertices(source, sink);
        TransitNodeRoutingPrecomputation.AccessVertex<V, E> forwardAccessVertex = p.getFirst();
        TransitNodeRoutingPrecomputation.AccessVertex<V, E> backwardAccessVertex = p.getSecond();
        if (forwardAccessVertex == null) {
            return Double.POSITIVE_INFINITY;
        }
        return forwardAccessVertex.getPath().getWeight() + this.manyToManyShortestPaths.getWeight(forwardAccessVertex.getVertex(), backwardAccessVertex.getVertex()) + backwardAccessVertex.getPath().getWeight();
    }

    private Pair<TransitNodeRoutingPrecomputation.AccessVertex<V, E>, TransitNodeRoutingPrecomputation.AccessVertex<V, E>> getMinWeightAccessVertices(V source, V sink) {
        ContractionHierarchyPrecomputation.ContractionVertex<V> contractedSource = this.contractionHierarchy.getContractionMapping().get(source);
        ContractionHierarchyPrecomputation.ContractionVertex<V> contractedSink = this.contractionHierarchy.getContractionMapping().get(sink);
        TransitNodeRoutingPrecomputation.AccessVertex<V, E> forwardAccessVertex = null;
        TransitNodeRoutingPrecomputation.AccessVertex<V, E> backwardAccessVertex = null;
        double minimumWeight = Double.POSITIVE_INFINITY;
        for (TransitNodeRoutingPrecomputation.AccessVertex<V, E> sourceAccessVertex : this.accessVertices.getForwardAccessVertices(contractedSource)) {
            for (TransitNodeRoutingPrecomputation.AccessVertex<V, E> sinkAccessVertex : this.accessVertices.getBackwardAccessVertices(contractedSink)) {
                double currentWeight = sourceAccessVertex.getPath().getWeight() + this.manyToManyShortestPaths.getWeight(sourceAccessVertex.getVertex(), sinkAccessVertex.getVertex()) + sinkAccessVertex.getPath().getWeight();
                if (!(currentWeight < minimumWeight)) continue;
                minimumWeight = currentWeight;
                forwardAccessVertex = sourceAccessVertex;
                backwardAccessVertex = sinkAccessVertex;
            }
        }
        if (minimumWeight == Double.POSITIVE_INFINITY) {
            return new Pair<Object, Object>(null, null);
        }
        return Pair.of(forwardAccessVertex, backwardAccessVertex);
    }

    private GraphPath<V, E> mergePaths(GraphPath<V, E> first, GraphPath<V, E> second, GraphPath<V, E> third) {
        V startVertex = first.getStartVertex();
        V endVertex = third.getEndVertex();
        double totalWeight = first.getWeight() + second.getWeight() + third.getWeight();
        int vertexListSize = first.getVertexList().size() + second.getVertexList().size() + third.getVertexList().size() - 2;
        ArrayList<V> vertexList = new ArrayList<V>(vertexListSize);
        int edgeListSize = first.getLength() + second.getLength() + third.getLength();
        ArrayList<E> edgeList = new ArrayList<E>(edgeListSize);
        Iterator<V> firstIt = first.getVertexList().iterator();
        while (firstIt.hasNext()) {
            V element = firstIt.next();
            if (!firstIt.hasNext()) continue;
            vertexList.add(element);
        }
        vertexList.addAll(second.getVertexList());
        Iterator<V> thirdIt = third.getVertexList().iterator();
        thirdIt.next();
        while (thirdIt.hasNext()) {
            vertexList.add(thirdIt.next());
        }
        edgeList.addAll(first.getEdgeList());
        edgeList.addAll(second.getEdgeList());
        edgeList.addAll(third.getEdgeList());
        return new GraphWalk(this.graph, startVertex, endVertex, vertexList, edgeList, totalWeight);
    }
}

