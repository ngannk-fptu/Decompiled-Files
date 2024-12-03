/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.shortestpath.BaseManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.MaskSubgraph;

public class CHManyToManyShortestPaths<V, E>
extends BaseManyToManyShortestPaths<V, E> {
    private ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> contractionHierarchy;
    private Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph;
    private Map<V, ContractionHierarchyPrecomputation.ContractionVertex<V>> contractionMapping;

    public CHManyToManyShortestPaths(Graph<V, E> graph, ThreadPoolExecutor executor) {
        this(new ContractionHierarchyPrecomputation<V, E>(graph, executor).computeContractionHierarchy());
    }

    public CHManyToManyShortestPaths(ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> contractionHierarchy) {
        super(contractionHierarchy.getGraph());
        this.contractionHierarchy = contractionHierarchy;
        this.contractionGraph = contractionHierarchy.getContractionGraph();
        this.contractionMapping = contractionHierarchy.getContractionMapping();
    }

    @Override
    public ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> getManyToManyPaths(Set<V> sources, Set<V> targets) {
        boolean reversed;
        Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> searchContractionGraph;
        Objects.requireNonNull(sources, "sources cannot be null!");
        Objects.requireNonNull(targets, "targets cannot be null!");
        if (sources.size() <= targets.size()) {
            searchContractionGraph = this.contractionGraph;
            reversed = false;
        } else {
            searchContractionGraph = new EdgeReversedGraph<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex<V>>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge<E>>>(this.contractionGraph);
            reversed = true;
            Set<V> tmp = targets;
            targets = sources;
            sources = tmp;
        }
        HashMap<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> forwardSearchSpaces = new HashMap<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>>();
        HashMap<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> backwardSearchSpaces = new HashMap<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>>();
        HashMap<Pair<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionVertex<V>>, Pair<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>> middleVertices = new HashMap<Pair<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionVertex<V>>, Pair<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>>();
        Set contractedSources = sources.stream().map(this.contractionMapping::get).collect(Collectors.toCollection(HashSet::new));
        Set contractedTargets = targets.stream().map(this.contractionMapping::get).collect(Collectors.toCollection(HashSet::new));
        HashMap bucketsMap = new HashMap();
        for (ContractionHierarchyPrecomputation.ContractionVertex<V> vertex : searchContractionGraph.vertexSet()) {
            bucketsMap.put(vertex, new ArrayList());
        }
        for (ContractionHierarchyPrecomputation.ContractionVertex<Object> contractedTarget : contractedTargets) {
            this.backwardSearch(searchContractionGraph, contractedTarget, contractedSources, bucketsMap, backwardSearchSpaces, reversed);
        }
        for (ContractionHierarchyPrecomputation.ContractionVertex<Object> contractedSource : contractedSources) {
            this.forwardSearch(searchContractionGraph, contractedSource, contractedTargets, bucketsMap, forwardSearchSpaces, middleVertices, reversed);
        }
        if (reversed) {
            return new CHManyToManyShortestPathsImpl(this.graph, this.contractionHierarchy, targets, sources, backwardSearchSpaces, forwardSearchSpaces, middleVertices);
        }
        return new CHManyToManyShortestPathsImpl(this.graph, this.contractionHierarchy, sources, targets, forwardSearchSpaces, backwardSearchSpaces, middleVertices);
    }

    private void backwardSearch(Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph, ContractionHierarchyPrecomputation.ContractionVertex<V> target, Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> contractedSources, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, List<BucketEntry>> bucketsMap, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> backwardSearchSpaces, boolean reversed) {
        MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex<Object>, ContractionHierarchyPrecomputation.ContractionEdge<Object>> maskSubgraph = reversed ? new MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>(new EdgeReversedGraph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>(contractionGraph), v -> false, e -> !e.isUpward) : new MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex, ContractionHierarchyPrecomputation.ContractionEdge>(new EdgeReversedGraph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>(contractionGraph), v -> false, e -> e.isUpward);
        Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>> distanceAndPredecessorMap = this.getDistanceAndPredecessorMap(maskSubgraph, target, contractedSources);
        backwardSearchSpaces.put(target, distanceAndPredecessorMap);
        for (Map.Entry<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>> entry : distanceAndPredecessorMap.entrySet()) {
            bucketsMap.get(entry.getKey()).add(new BucketEntry(target, entry.getValue().getFirst()));
        }
    }

    private void forwardSearch(Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph, ContractionHierarchyPrecomputation.ContractionVertex<V> source, Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> contractedTargets, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, List<BucketEntry>> bucketsMap, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> forwardSearchSpaces, Map<Pair<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionVertex<V>>, Pair<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>> middleVerticesMap, boolean reversed) {
        MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex<Object>, ContractionHierarchyPrecomputation.ContractionEdge<Object>> maskSubgraph = reversed ? new MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>(contractionGraph, v -> false, e -> e.isUpward) : new MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex, ContractionHierarchyPrecomputation.ContractionEdge>(contractionGraph, v -> false, e -> !e.isUpward);
        Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>> distanceAndPredecessorMap = this.getDistanceAndPredecessorMap(maskSubgraph, source, contractedTargets);
        forwardSearchSpaces.put(source, distanceAndPredecessorMap);
        for (Map.Entry<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>> entry : distanceAndPredecessorMap.entrySet()) {
            ContractionHierarchyPrecomputation.ContractionVertex middleVertex = entry.getKey();
            double forwardDistance = entry.getValue().getFirst();
            for (BucketEntry bucketEntry : bucketsMap.get(middleVertex)) {
                double pathDistance = forwardDistance + bucketEntry.distance;
                Pair pair = reversed ? Pair.of(bucketEntry.target, source) : Pair.of(source, bucketEntry.target);
                middleVerticesMap.compute(pair, (p, distanceAndMiddleNode) -> {
                    if (distanceAndMiddleNode == null || (Double)distanceAndMiddleNode.getFirst() > pathDistance) {
                        return Pair.of(pathDistance, middleVertex);
                    }
                    return distanceAndMiddleNode;
                });
            }
        }
    }

    private Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>> getDistanceAndPredecessorMap(Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph, ContractionHierarchyPrecomputation.ContractionVertex<V> source, Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> targets) {
        return ((TreeSingleSourcePathsImpl)CHManyToManyShortestPaths.getShortestPathsTree(contractionGraph, source, targets)).map;
    }

    private class CHManyToManyShortestPathsImpl
    extends ManyToManyShortestPathsAlgorithm.BaseManyToManyShortestPathsImpl<V, E> {
        private final Graph<V, E> graph;
        private final Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph;
        private final Map<V, ContractionHierarchyPrecomputation.ContractionVertex<V>> contractionMapping;
        private Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> forwardSearchSpaces;
        private Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> backwardSearchSpaces;
        private Map<Pair<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionVertex<V>>, Pair<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>> distanceAndMiddleVertexMap;

        public CHManyToManyShortestPathsImpl(Graph<V, E> graph, ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> hierarchy, Set<V> sources, Set<V> targets, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> forwardSearchSpaces, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, Pair<Double, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> backwardSearchSpaces, Map<Pair<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionVertex<V>>, Pair<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>> distanceAndMiddleVertexMap) {
            super(sources, targets);
            this.graph = graph;
            this.contractionGraph = hierarchy.getContractionGraph();
            this.contractionMapping = hierarchy.getContractionMapping();
            this.forwardSearchSpaces = forwardSearchSpaces;
            this.backwardSearchSpaces = backwardSearchSpaces;
            this.distanceAndMiddleVertexMap = distanceAndMiddleVertexMap;
        }

        @Override
        public GraphPath<V, E> getPath(V source, V target) {
            ContractionHierarchyPrecomputation.ContractionEdge e;
            this.assertCorrectSourceAndTarget(source, target);
            LinkedList edgeList = new LinkedList();
            LinkedList vertexList = new LinkedList();
            ContractionHierarchyPrecomputation.ContractionVertex contractedSource = this.contractionMapping.get(source);
            ContractionHierarchyPrecomputation.ContractionVertex contractedTarget = this.contractionMapping.get(target);
            Pair contractedVertices = Pair.of(contractedSource, contractedTarget);
            Map forwardTree = this.forwardSearchSpaces.get(contractedSource);
            Map backwardTree = this.backwardSearchSpaces.get(contractedTarget);
            Pair distanceAndCommonVertex = this.distanceAndMiddleVertexMap.get(contractedVertices);
            if (distanceAndCommonVertex == null) {
                return null;
            }
            ContractionHierarchyPrecomputation.ContractionVertex commonVertex = distanceAndCommonVertex.getSecond();
            vertexList.add(commonVertex.vertex);
            ContractionHierarchyPrecomputation.ContractionVertex v = commonVertex;
            while ((e = forwardTree.get(v).getSecond()) != null) {
                CHManyToManyShortestPaths.this.contractionHierarchy.unpackBackward(e, vertexList, edgeList);
                v = this.contractionGraph.getEdgeSource(e);
            }
            v = commonVertex;
            while ((e = backwardTree.get(v).getSecond()) != null) {
                CHManyToManyShortestPaths.this.contractionHierarchy.unpackForward(e, vertexList, edgeList);
                v = this.contractionGraph.getEdgeTarget(e);
            }
            return new GraphWalk(this.graph, source, target, vertexList, edgeList, distanceAndCommonVertex.getFirst());
        }

        @Override
        public double getWeight(V source, V target) {
            this.assertCorrectSourceAndTarget(source, target);
            Pair contractedVertices = Pair.of(this.contractionMapping.get(source), this.contractionMapping.get(target));
            if (this.distanceAndMiddleVertexMap.containsKey(contractedVertices)) {
                return this.distanceAndMiddleVertexMap.get(contractedVertices).getFirst();
            }
            return Double.POSITIVE_INFINITY;
        }
    }

    private class BucketEntry {
        ContractionHierarchyPrecomputation.ContractionVertex<V> target;
        double distance;

        public BucketEntry(ContractionHierarchyPrecomputation.ContractionVertex<V> target, double distance) {
            this.target = target;
            this.distance = distance;
        }
    }
}

