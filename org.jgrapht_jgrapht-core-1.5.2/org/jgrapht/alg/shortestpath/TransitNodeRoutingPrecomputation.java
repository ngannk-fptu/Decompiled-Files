/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.shortestpath.CHManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.jgrapht.alg.shortestpath.DefaultManyToManyShortestPaths;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.MaskSubgraph;
import org.jgrapht.util.CollectionUtil;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

class TransitNodeRoutingPrecomputation<V, E> {
    private static final int NO_VORONOI_CELL = -1;
    private ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> contractionHierarchy;
    private Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph;
    private Map<V, ContractionHierarchyPrecomputation.ContractionVertex<V>> contractionMapping;
    private int numberOfTransitVertices;
    private int parallelism;
    private Supplier<AddressableHeap<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>> heapSupplier;
    private List<ContractionHierarchyPrecomputation.ContractionVertex<V>> contractionVertices;
    private ManyToManyShortestPathsAlgorithm<V, E> manyToManyShortestPathsAlgorithm;
    private Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> contractedTransitVerticesSet;
    private Set<V> transitVerticesSet;
    private List<V> transitVerticesList;
    private VoronoiDiagram<V> voronoiDiagram;
    private ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> transitVerticesPaths;
    private ExecutorService executor;
    private ExecutorCompletionService<Void> completionService;

    public TransitNodeRoutingPrecomputation(Graph<V, E> graph, ThreadPoolExecutor executor) {
        this(new ContractionHierarchyPrecomputation<V, E>(graph, executor).computeContractionHierarchy(), executor);
    }

    public TransitNodeRoutingPrecomputation(ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> hierarchy, ThreadPoolExecutor executor) {
        this(hierarchy, (int)Math.sqrt(hierarchy.getGraph().vertexSet().size()), executor);
    }

    public TransitNodeRoutingPrecomputation(ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> hierarchy, int numberOfTransitVertices, ThreadPoolExecutor executor) {
        this(hierarchy, numberOfTransitVertices, PairingHeap::new, executor);
    }

    public TransitNodeRoutingPrecomputation(ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> hierarchy, int numberOfTransitVertices, Supplier<AddressableHeap<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>> heapSupplier, ThreadPoolExecutor executor) {
        if (numberOfTransitVertices > hierarchy.getGraph().vertexSet().size()) {
            throw new IllegalArgumentException("number of transit vertices is larger than the number of vertices in the graph");
        }
        this.contractionHierarchy = hierarchy;
        this.contractionGraph = hierarchy.getContractionGraph();
        this.contractionMapping = hierarchy.getContractionMapping();
        this.numberOfTransitVertices = numberOfTransitVertices;
        this.parallelism = executor.getMaximumPoolSize();
        this.heapSupplier = heapSupplier;
        this.contractionVertices = new ArrayList<Object>(Collections.nCopies(this.contractionGraph.vertexSet().size(), null));
        this.manyToManyShortestPathsAlgorithm = new CHManyToManyShortestPaths<V, E>(hierarchy);
        this.executor = executor;
        this.completionService = new ExecutorCompletionService(this.executor);
    }

    public TransitNodeRouting<V, E> computeTransitNodeRouting() {
        this.fillContractionVerticesList();
        this.contractedTransitVerticesSet = this.selectTopKTransitVertices(this.numberOfTransitVertices);
        this.transitVerticesSet = this.contractedTransitVerticesSet.stream().map(v -> v.vertex).collect(Collectors.toCollection(HashSet::new));
        this.transitVerticesList = new ArrayList<V>(this.transitVerticesSet);
        VoronoiDiagramComputation voronoiDiagramComputation = new VoronoiDiagramComputation();
        this.voronoiDiagram = voronoiDiagramComputation.computeVoronoiDiagram();
        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> contractedPaths = this.manyToManyShortestPathsAlgorithm.getManyToManyPaths(this.transitVerticesSet, this.transitVerticesSet);
        this.transitVerticesPaths = this.unpackPaths(contractedPaths);
        Pair<AccessVertices<V, E>, LocalityFilter<V>> avAndLf = this.computeAVAndLF();
        return new TransitNodeRouting<V, E>(this.contractionHierarchy, this.contractedTransitVerticesSet, this.transitVerticesPaths, this.voronoiDiagram, avAndLf.getFirst(), avAndLf.getSecond());
    }

    private void fillContractionVerticesList() {
        for (ContractionHierarchyPrecomputation.ContractionVertex<V> v : this.contractionGraph.vertexSet()) {
            this.contractionVertices.set(v.vertexId, v);
        }
    }

    private ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> unpackPaths(ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> shortestPaths) {
        HashMap pathsMap = CollectionUtil.newHashMapWithExpectedSize(this.numberOfTransitVertices);
        for (V v : this.transitVerticesList) {
            pathsMap.put(v, CollectionUtil.newHashMapWithExpectedSize(this.numberOfTransitVertices));
        }
        for (int taskId = 0; taskId < this.parallelism; ++taskId) {
            PathsUnpackingTask task = new PathsUnpackingTask(taskId, this.transitVerticesList, pathsMap, shortestPaths);
            this.completionService.submit(task, null);
        }
        this.waitForTasksCompletion(this.parallelism);
        return new DefaultManyToManyShortestPaths.DefaultManyToManyShortestPathsImpl(this.transitVerticesSet, this.transitVerticesSet, pathsMap);
    }

    private Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> selectTopKTransitVertices(int numberOfTransitVertices) {
        int numberOfVertices = this.contractionGraph.vertexSet().size();
        HashSet<ContractionHierarchyPrecomputation.ContractionVertex<V>> result = CollectionUtil.newHashSetWithExpectedSize(numberOfTransitVertices);
        for (ContractionHierarchyPrecomputation.ContractionVertex<V> vertex : this.contractionGraph.vertexSet()) {
            if (vertex.contractionLevel < numberOfVertices - numberOfTransitVertices) continue;
            result.add(vertex);
        }
        return result;
    }

    private Pair<AccessVertices<V, E>, LocalityFilter<V>> computeAVAndLF() {
        LocalityFilterBuilder localityFilterBuilder = new LocalityFilterBuilder(this.contractionGraph.vertexSet().size());
        AccessVerticesBuilder accessVerticesBuilder = new AccessVerticesBuilder(this.contractionGraph.vertexSet().size());
        ContractionHierarchyBFS forwardBFS = new ContractionHierarchyBFS(new MaskSubgraph(this.contractionGraph, v -> false, e -> !e.isUpward));
        ContractionHierarchyBFS backwardBFS = new ContractionHierarchyBFS(new MaskSubgraph(new EdgeReversedGraph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>(this.contractionGraph), v -> false, e -> e.isUpward));
        for (int taskId = 0; taskId < this.parallelism; ++taskId) {
            AVAndLFConstructionTask task = new AVAndLFConstructionTask(taskId, localityFilterBuilder, accessVerticesBuilder, forwardBFS, backwardBFS);
            this.completionService.submit(task, null);
        }
        this.waitForTasksCompletion(this.parallelism);
        return Pair.of(accessVerticesBuilder.buildVertices(), localityFilterBuilder.buildLocalityFilter());
    }

    private void waitForTasksCompletion(int numberOfTasks) {
        for (int i = 0; i < numberOfTasks; ++i) {
            try {
                this.completionService.take().get();
                continue;
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private int workerSegmentStart(int segmentStart, int segmentEnd, int taskId) {
        return segmentStart + (segmentEnd - segmentStart) * taskId / this.parallelism;
    }

    private int workerSegmentEnd(int segmentStart, int segmentEnd, int taskId) {
        return segmentStart + (segmentEnd - segmentStart) * (taskId + 1) / this.parallelism;
    }

    private class VoronoiDiagramComputation {
        private AddressableHeap<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>> heap;
        private Map<ContractionHierarchyPrecomputation.ContractionVertex<V>, AddressableHeap.Handle<Double, ContractionHierarchyPrecomputation.ContractionVertex<V>>> seen;
        private int[] voronoiCells;
        private double[] distanceToCenter;

        VoronoiDiagramComputation() {
            this.heap = TransitNodeRoutingPrecomputation.this.heapSupplier.get();
            this.seen = new HashMap();
        }

        VoronoiDiagram<V> computeVoronoiDiagram() {
            int numberOfVertices = TransitNodeRoutingPrecomputation.this.contractionGraph.vertexSet().size();
            this.voronoiCells = new int[numberOfVertices];
            this.distanceToCenter = new double[numberOfVertices];
            Arrays.fill(this.voronoiCells, -1);
            Arrays.fill(this.distanceToCenter, Double.POSITIVE_INFINITY);
            EdgeReversedGraph<ContractionHierarchyPrecomputation.ContractionVertex, ContractionHierarchyPrecomputation.ContractionEdge> searchGraph = new EdgeReversedGraph<ContractionHierarchyPrecomputation.ContractionVertex, ContractionHierarchyPrecomputation.ContractionEdge>(new MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex, ContractionHierarchyPrecomputation.ContractionEdge>(TransitNodeRoutingPrecomputation.this.contractionGraph, v -> false, e -> e.edge == null));
            for (ContractionHierarchyPrecomputation.ContractionVertex transitVertex : TransitNodeRoutingPrecomputation.this.contractedTransitVerticesSet) {
                this.updateDistance(transitVertex, transitVertex, 0.0);
            }
            while (!this.heap.isEmpty()) {
                AddressableHeap.Handle entry = this.heap.deleteMin();
                double distance = (Double)entry.getKey();
                ContractionHierarchyPrecomputation.ContractionVertex v2 = (ContractionHierarchyPrecomputation.ContractionVertex)entry.getValue();
                for (ContractionHierarchyPrecomputation.ContractionEdge edge : searchGraph.outgoingEdgesOf(v2)) {
                    ContractionHierarchyPrecomputation.ContractionVertex successor = Graphs.getOppositeVertex(searchGraph, edge, v2);
                    double updatedDistance = distance + searchGraph.getEdgeWeight(edge);
                    if (!(updatedDistance < this.distanceToCenter[successor.vertexId])) continue;
                    this.updateDistance(successor, v2, updatedDistance);
                }
            }
            return new VoronoiDiagram(this.voronoiCells);
        }

        private void updateDistance(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex, ContractionHierarchyPrecomputation.ContractionVertex<V> predecessor, double distance) {
            AddressableHeap.Handle handle = this.seen.get(vertex);
            if (handle == null) {
                handle = this.heap.insert((Object)distance, vertex);
                this.seen.put(vertex, handle);
                this.visitVertex(vertex, predecessor, distance);
            } else if (distance < (Double)handle.getKey()) {
                handle.decreaseKey((Object)distance);
                handle.setValue((Object)((ContractionHierarchyPrecomputation.ContractionVertex)handle.getValue()));
                this.visitVertex(vertex, predecessor, distance);
            }
        }

        private void visitVertex(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex, ContractionHierarchyPrecomputation.ContractionVertex<V> predecessor, double distance) {
            int updatedVoronoiCell = vertex.vertexId == predecessor.vertexId ? vertex.vertexId : this.voronoiCells[predecessor.vertexId];
            this.voronoiCells[vertex.vertexId] = updatedVoronoiCell;
            this.distanceToCenter[vertex.vertexId] = distance;
        }
    }

    public static class VoronoiDiagram<V> {
        private int[] voronoiCells;

        public VoronoiDiagram(int[] voronoiCells) {
            this.voronoiCells = voronoiCells;
        }

        public int getVoronoiCellId(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex) {
            return this.voronoiCells[vertex.vertexId];
        }
    }

    static class TransitNodeRouting<V, E> {
        private ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> contractionHierarchy;
        private Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> transitVertices;
        private ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> transitVerticesPaths;
        private VoronoiDiagram<V> voronoiDiagram;
        private AccessVertices<V, E> accessVertices;
        private LocalityFilter<V> localityFilter;

        public ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> getContractionHierarchy() {
            return this.contractionHierarchy;
        }

        public Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> getTransitVertices() {
            return this.transitVertices;
        }

        public ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> getTransitVerticesPaths() {
            return this.transitVerticesPaths;
        }

        public VoronoiDiagram<V> getVoronoiDiagram() {
            return this.voronoiDiagram;
        }

        public AccessVertices<V, E> getAccessVertices() {
            return this.accessVertices;
        }

        public LocalityFilter<V> getLocalityFilter() {
            return this.localityFilter;
        }

        public TransitNodeRouting(ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> contractionHierarchy, Set<ContractionHierarchyPrecomputation.ContractionVertex<V>> transitVertices, ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> transitVerticesPaths, VoronoiDiagram<V> voronoiDiagram, AccessVertices<V, E> accessVertices, LocalityFilter<V> localityFilter) {
            this.contractionHierarchy = contractionHierarchy;
            this.transitVertices = transitVertices;
            this.transitVerticesPaths = transitVerticesPaths;
            this.voronoiDiagram = voronoiDiagram;
            this.localityFilter = localityFilter;
            this.accessVertices = accessVertices;
        }
    }

    public static class AccessVertices<V, E> {
        private List<List<AccessVertex<V, E>>> forwardAccessVertices;
        private List<List<AccessVertex<V, E>>> backwardAccessVertices;

        public AccessVertices(List<List<AccessVertex<V, E>>> forwardAccessVertices, List<List<AccessVertex<V, E>>> backwardAccessVertices) {
            this.forwardAccessVertices = forwardAccessVertices;
            this.backwardAccessVertices = backwardAccessVertices;
        }

        public List<AccessVertex<V, E>> getForwardAccessVertices(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex) {
            return this.forwardAccessVertices.get(vertex.vertexId);
        }

        public List<AccessVertex<V, E>> getBackwardAccessVertices(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex) {
            return this.backwardAccessVertices.get(vertex.vertexId);
        }
    }

    public static class LocalityFilter<V> {
        private Map<V, ContractionHierarchyPrecomputation.ContractionVertex<V>> contractionMapping;
        private List<Set<Integer>> visitedForwardVoronoiCells;
        private List<Set<Integer>> visitedBackwardVoronoiCells;

        public LocalityFilter(Map<V, ContractionHierarchyPrecomputation.ContractionVertex<V>> contractionMapping, List<Set<Integer>> visitedForwardVoronoiCells, List<Set<Integer>> visitedBackwardVoronoiCells) {
            this.contractionMapping = contractionMapping;
            this.visitedForwardVoronoiCells = visitedForwardVoronoiCells;
            this.visitedBackwardVoronoiCells = visitedBackwardVoronoiCells;
        }

        public boolean isLocal(V source, V sink) {
            Set<Integer> largerSet;
            Set<Integer> smallerSet;
            ContractionHierarchyPrecomputation.ContractionVertex<V> contractedSource = this.contractionMapping.get(source);
            ContractionHierarchyPrecomputation.ContractionVertex<V> contractedSink = this.contractionMapping.get(sink);
            Set<Integer> sourceVisitedVoronoiCells = this.visitedForwardVoronoiCells.get(contractedSource.vertexId);
            Set<Integer> sinkVisitedVoronoiCells = this.visitedBackwardVoronoiCells.get(contractedSink.vertexId);
            if (sourceVisitedVoronoiCells.contains(-1) || sinkVisitedVoronoiCells.contains(-1)) {
                return true;
            }
            if (sourceVisitedVoronoiCells.size() <= sinkVisitedVoronoiCells.size()) {
                smallerSet = sourceVisitedVoronoiCells;
                largerSet = sinkVisitedVoronoiCells;
            } else {
                smallerSet = sinkVisitedVoronoiCells;
                largerSet = sourceVisitedVoronoiCells;
            }
            for (Integer visitedVoronoiCell : smallerSet) {
                if (!largerSet.contains(visitedVoronoiCell)) continue;
                return true;
            }
            return false;
        }
    }

    private class PathsUnpackingTask
    implements Runnable {
        private int taskId;
        private List<V> transitVertices;
        private Map<V, Map<V, GraphPath<V, E>>> pathsMap;
        private ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> shortestPaths;

        public PathsUnpackingTask(int taskId, List<V> transitVertices, Map<V, Map<V, GraphPath<V, E>>> pathsMap, ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> shortestPaths) {
            this.taskId = taskId;
            this.transitVertices = transitVertices;
            this.pathsMap = pathsMap;
            this.shortestPaths = shortestPaths;
        }

        @Override
        public void run() {
            int start = TransitNodeRoutingPrecomputation.this.workerSegmentStart(0, this.transitVertices.size(), this.taskId);
            int end = TransitNodeRoutingPrecomputation.this.workerSegmentEnd(0, this.transitVertices.size(), this.taskId);
            for (int i = start; i < end; ++i) {
                Object v1 = this.transitVertices.get(i);
                Map targetToPathsMap = this.pathsMap.get(v1);
                for (Object v2 : this.transitVertices) {
                    targetToPathsMap.put((GraphPath)v2, (GraphPath)this.shortestPaths.getPath(v1, v2));
                }
            }
        }
    }

    private class LocalityFilterBuilder {
        private List<Set<Integer>> visitedForwardVoronoiCells;
        private List<Set<Integer>> visitedBackwardVoronoiCells;

        public LocalityFilterBuilder(int numberOfVertices) {
            this.visitedForwardVoronoiCells = new ArrayList<Set<Integer>>(numberOfVertices);
            this.visitedBackwardVoronoiCells = new ArrayList<Set<Integer>>(numberOfVertices);
            for (int i = 0; i < numberOfVertices; ++i) {
                this.visitedForwardVoronoiCells.add(null);
                this.visitedBackwardVoronoiCells.add(null);
            }
        }

        public void addForwardVisitedVoronoiCells(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex, Set<Integer> visitedVoronoiCells) {
            this.visitedForwardVoronoiCells.set(vertex.vertexId, visitedVoronoiCells);
        }

        public void addBackwardVisitedVoronoiCells(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex, Set<Integer> visitedVoronoiCells) {
            this.visitedBackwardVoronoiCells.set(vertex.vertexId, visitedVoronoiCells);
        }

        public LocalityFilter<V> buildLocalityFilter() {
            return new LocalityFilter(TransitNodeRoutingPrecomputation.this.contractionMapping, this.visitedForwardVoronoiCells, this.visitedBackwardVoronoiCells);
        }
    }

    private class AccessVerticesBuilder {
        private List<List<AccessVertex<V, E>>> forwardAccessVertices;
        private List<List<AccessVertex<V, E>>> backwardAccessVertices;

        public AccessVerticesBuilder(int numberOfVertices) {
            this.forwardAccessVertices = new ArrayList(numberOfVertices);
            this.backwardAccessVertices = new ArrayList(numberOfVertices);
            for (int i = 0; i < numberOfVertices; ++i) {
                this.forwardAccessVertices.add(new ArrayList());
                this.backwardAccessVertices.add(new ArrayList());
            }
        }

        public AccessVertices<V, E> buildVertices() {
            return new AccessVertices(this.forwardAccessVertices, this.backwardAccessVertices);
        }

        public void addForwardAccessVertices(ContractionHierarchyPrecomputation.ContractionVertex<V> v, Set<V> vertices) {
            ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths manyToManyShortestPaths = TransitNodeRoutingPrecomputation.this.manyToManyShortestPathsAlgorithm.getManyToManyPaths(Collections.singleton(v.vertex), vertices);
            Set prunedVertices = this.getPrunedAccessVertices(v.vertex, vertices, manyToManyShortestPaths, true);
            List accessVerticesList = this.forwardAccessVertices.get(v.vertexId);
            for (Object unpackedVertex : vertices) {
                if (prunedVertices.contains(unpackedVertex)) continue;
                accessVerticesList.add(new AccessVertex(unpackedVertex, manyToManyShortestPaths.getPath(v.vertex, unpackedVertex)));
            }
        }

        public void addBackwardAccessVertices(ContractionHierarchyPrecomputation.ContractionVertex<V> v, Set<V> vertices) {
            ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths manyToManyShortestPaths = TransitNodeRoutingPrecomputation.this.manyToManyShortestPathsAlgorithm.getManyToManyPaths(vertices, Collections.singleton(v.vertex));
            Set prunedVertices = this.getPrunedAccessVertices(v.vertex, vertices, manyToManyShortestPaths, false);
            List accessVerticesList = this.backwardAccessVertices.get(v.vertexId);
            for (Object unpackedVertex : vertices) {
                if (prunedVertices.contains(unpackedVertex)) continue;
                accessVerticesList.add(new AccessVertex(unpackedVertex, manyToManyShortestPaths.getPath(unpackedVertex, v.vertex)));
            }
        }

        private Set<V> getPrunedAccessVertices(V v, Set<V> vertices, ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> manyToManyShortestPaths, boolean forwardAccessVertices) {
            HashSet result = new HashSet();
            for (Object v1 : vertices) {
                if (result.contains(v1)) continue;
                for (Object v2 : vertices) {
                    if (v1.equals(v2) || result.contains(v2)) continue;
                    if (forwardAccessVertices) {
                        if (!(manyToManyShortestPaths.getWeight(v, v1) + TransitNodeRoutingPrecomputation.this.transitVerticesPaths.getWeight(v1, v2) <= manyToManyShortestPaths.getWeight(v, v2))) continue;
                        result.add(v2);
                        continue;
                    }
                    if (!(TransitNodeRoutingPrecomputation.this.transitVerticesPaths.getWeight(v2, v1) + manyToManyShortestPaths.getWeight(v1, v) <= manyToManyShortestPaths.getWeight(v2, v))) continue;
                    result.add(v2);
                }
            }
            return result;
        }
    }

    private class ContractionHierarchyBFS {
        private Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph;

        public ContractionHierarchyBFS(Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph) {
            this.contractionGraph = contractionGraph;
        }

        public Pair<Set<V>, Set<Integer>> runSearch(ContractionHierarchyPrecomputation.ContractionVertex<V> vertex) {
            HashSet accessVertices = new HashSet();
            HashSet<Integer> visitedVoronoiCells = new HashSet<Integer>();
            HashSet<Integer> visitedVerticesIds = new HashSet<Integer>();
            LinkedList<ContractionHierarchyPrecomputation.ContractionVertex> queue = new LinkedList<ContractionHierarchyPrecomputation.ContractionVertex>();
            queue.add(vertex);
            while (!queue.isEmpty()) {
                ContractionHierarchyPrecomputation.ContractionVertex v = (ContractionHierarchyPrecomputation.ContractionVertex)queue.remove();
                visitedVerticesIds.add(v.vertexId);
                if (TransitNodeRoutingPrecomputation.this.contractedTransitVerticesSet.contains(v)) {
                    accessVertices.add(v.vertex);
                    continue;
                }
                visitedVoronoiCells.add(TransitNodeRoutingPrecomputation.this.voronoiDiagram.getVoronoiCellId(v));
                for (ContractionHierarchyPrecomputation.ContractionEdge e : this.contractionGraph.outgoingEdgesOf(v)) {
                    ContractionHierarchyPrecomputation.ContractionVertex successor = Graphs.getOppositeVertex(this.contractionGraph, e, v);
                    if (visitedVerticesIds.contains(successor.vertexId)) continue;
                    queue.add(successor);
                }
            }
            return Pair.of(accessVertices, visitedVoronoiCells);
        }
    }

    private class AVAndLFConstructionTask
    implements Runnable {
        private int taskId;
        private LocalityFilterBuilder localityFilterBuilder;
        private AccessVerticesBuilder accessVerticesBuilder;
        private ContractionHierarchyBFS forwardBFS;
        private ContractionHierarchyBFS backwardBFS;

        public AVAndLFConstructionTask(int taskId, LocalityFilterBuilder localityFilterBuilder, AccessVerticesBuilder accessVerticesBuilder, ContractionHierarchyBFS forwardBFS, ContractionHierarchyBFS backwardBFS) {
            this.taskId = taskId;
            this.localityFilterBuilder = localityFilterBuilder;
            this.accessVerticesBuilder = accessVerticesBuilder;
            this.forwardBFS = forwardBFS;
            this.backwardBFS = backwardBFS;
        }

        @Override
        public void run() {
            int start = TransitNodeRoutingPrecomputation.this.workerSegmentStart(0, TransitNodeRoutingPrecomputation.this.contractionVertices.size(), this.taskId);
            int end = TransitNodeRoutingPrecomputation.this.workerSegmentEnd(0, TransitNodeRoutingPrecomputation.this.contractionVertices.size(), this.taskId);
            for (int i = start; i < end; ++i) {
                ContractionHierarchyPrecomputation.ContractionVertex v = TransitNodeRoutingPrecomputation.this.contractionVertices.get(i);
                Pair forwardData = this.forwardBFS.runSearch(v);
                Pair backwardData = this.backwardBFS.runSearch(v);
                this.accessVerticesBuilder.addForwardAccessVertices(v, forwardData.getFirst());
                this.accessVerticesBuilder.addBackwardAccessVertices(v, backwardData.getFirst());
                this.localityFilterBuilder.addForwardVisitedVoronoiCells(v, forwardData.getSecond());
                this.localityFilterBuilder.addBackwardVisitedVoronoiCells(v, backwardData.getSecond());
            }
        }
    }

    public static class AccessVertex<V, E> {
        private V vertex;
        private GraphPath<V, E> path;

        public V getVertex() {
            return this.vertex;
        }

        public GraphPath<V, E> getPath() {
            return this.path;
        }

        public AccessVertex(V vertex, GraphPath<V, E> path) {
            this.vertex = vertex;
            this.path = path;
        }
    }
}

