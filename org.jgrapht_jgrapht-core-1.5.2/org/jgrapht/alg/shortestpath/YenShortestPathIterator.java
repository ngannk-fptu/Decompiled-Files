/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.MaskSubgraph;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class YenShortestPathIterator<V, E>
implements Iterator<GraphPath<V, E>> {
    private final Graph<V, E> graph;
    private final V source;
    private final V sink;
    private PathValidator<V, E> pathValidator;
    private List<GraphPath<V, E>> resultList;
    private AddressableHeap<Double, Pair<GraphPath<V, E>, Boolean>> candidatePaths;
    private Map<GraphPath<V, E>, V> firstDeviations;
    private Map<GraphPath<V, E>, V> lastDeviations;
    private int numberOfValidPathInQueue;
    private boolean shortestPathComputed;

    public YenShortestPathIterator(Graph<V, E> graph, V source, V sink) {
        this(graph, source, sink, PairingHeap::new);
    }

    public YenShortestPathIterator(Graph<V, E> graph, V source, V sink, PathValidator<V, E> pathValidator) {
        this(graph, source, sink, PairingHeap::new, pathValidator);
    }

    public YenShortestPathIterator(Graph<V, E> graph, V source, V sink, Supplier<AddressableHeap<Double, Pair<GraphPath<V, E>, Boolean>>> heapSupplier) {
        this(graph, source, sink, heapSupplier, null);
    }

    public YenShortestPathIterator(Graph<V, E> graph, V source, V sink, Supplier<AddressableHeap<Double, Pair<GraphPath<V, E>, Boolean>>> heapSupplier, PathValidator<V, E> pathValidator) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null!");
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph should contain source vertex!");
        }
        this.source = source;
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph should contain sink vertex!");
        }
        this.sink = sink;
        this.pathValidator = pathValidator;
        Objects.requireNonNull(heapSupplier, "Heap supplier cannot be null");
        this.resultList = new ArrayList<GraphPath<V, E>>();
        this.candidatePaths = heapSupplier.get();
        this.firstDeviations = new HashMap<GraphPath<V, E>, V>();
        this.lastDeviations = new HashMap<GraphPath<V, E>, V>();
    }

    private void lazyInitializePathHeap() {
        GraphPath<V, E> shortestPath;
        if (!this.shortestPathComputed && (shortestPath = DijkstraShortestPath.findPathBetween(this.graph, this.source, this.sink)) != null) {
            V lastValidDeviation = this.getLastValidDeviation(shortestPath, this.source);
            boolean shortestPathIsValid = lastValidDeviation == null;
            this.candidatePaths.insert((Object)shortestPath.getWeight(), Pair.of(shortestPath, shortestPathIsValid));
            this.firstDeviations.put(shortestPath, this.source);
            this.lastDeviations.put(shortestPath, lastValidDeviation);
            if (shortestPathIsValid) {
                ++this.numberOfValidPathInQueue;
            }
            this.ensureAtLeastOneValidPathInQueue();
        }
        this.shortestPathComputed = true;
    }

    private void ensureAtLeastOneValidPathInQueue() {
        while (this.numberOfValidPathInQueue == 0 && !this.candidatePaths.isEmpty()) {
            Pair p = (Pair)this.candidatePaths.deleteMin().getValue();
            GraphPath currentPath = (GraphPath)p.getFirst();
            this.resultList.add(currentPath);
            int numberOfValidDeviations = this.addDeviations(currentPath);
            this.numberOfValidPathInQueue += numberOfValidDeviations;
        }
    }

    private V getLastValidDeviation(GraphPath<V, E> path, V firstDeviation) {
        int firstDeviationIndex;
        if (this.pathValidator == null) {
            return null;
        }
        List<V> vertices = path.getVertexList();
        List<E> edges = path.getEdgeList();
        V result = null;
        double partialPathWeight = 0.0;
        for (int i = firstDeviationIndex = vertices.indexOf(firstDeviation); i < edges.size(); ++i) {
            E edge;
            GraphWalk<V, E> partialPath = new GraphWalk<V, E>(path.getGraph(), path.getStartVertex(), vertices.get(i), vertices.subList(0, i + 1), edges.subList(0, i), partialPathWeight);
            boolean isValid = this.pathValidator.isValidPath(partialPath, edge = edges.get(i));
            if (!isValid) {
                result = vertices.get(i);
                break;
            }
            partialPathWeight += this.graph.getEdgeWeight(edge);
        }
        return result;
    }

    @Override
    public boolean hasNext() {
        this.lazyInitializePathHeap();
        return !this.candidatePaths.isEmpty();
    }

    @Override
    public GraphPath<V, E> next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        GraphPath result = null;
        while (result == null) {
            Pair p = (Pair)this.candidatePaths.deleteMin().getValue();
            GraphPath path = (GraphPath)p.getFirst();
            boolean isValid = (Boolean)p.getSecond();
            if (isValid) {
                result = path;
                --this.numberOfValidPathInQueue;
            }
            this.resultList.add(path);
            int numberOfValidDeviations = this.addDeviations(path);
            this.numberOfValidPathInQueue += numberOfValidDeviations;
        }
        this.ensureAtLeastOneValidPathInQueue();
        return result;
    }

    private int addDeviations(GraphPath<V, E> path) {
        int result = 0;
        V pathDeviation = this.firstDeviations.get(path);
        List<V> pathVertices = path.getVertexList();
        List<E> pathEdges = path.getEdgeList();
        int pathVerticesSize = pathVertices.size();
        int pathDeviationIndex = pathVertices.indexOf(pathDeviation);
        Pair<Set<V>, Set<E>> p = this.getMaskedVerticesAndEdges(path, pathDeviation, pathDeviationIndex);
        Set<V> maskedVertices = p.getFirst();
        Set<E> maskedEdges = p.getSecond();
        MaskSubgraph<Object, Object> maskSubgraph = new MaskSubgraph<Object, Object>(this.graph, maskedVertices::contains, maskedEdges::contains);
        EdgeReversedGraph<Object, Object> reversedMaskedGraph = new EdgeReversedGraph<Object, Object>(maskSubgraph);
        DijkstraShortestPath<Object, Object> shortestPath = new DijkstraShortestPath<Object, Object>(reversedMaskedGraph);
        TreeSingleSourcePathsImpl singleSourcePaths = (TreeSingleSourcePathsImpl)shortestPath.getPaths(this.sink);
        HashMap distanceAndPredecessorMap = new HashMap(singleSourcePaths.getDistanceAndPredecessorMap());
        YenShortestPathsTree customTree = new YenShortestPathsTree(maskSubgraph, maskedVertices, maskedEdges, distanceAndPredecessorMap, this.sink);
        V lastDeviation = this.lastDeviations.get(path);
        int lastDeviationIndex = lastDeviation == null ? pathVerticesSize - 2 : pathVertices.indexOf(lastDeviation);
        boolean proceed = true;
        for (int i = pathVerticesSize - 2; i >= 0 && proceed; --i) {
            V recoverVertex = pathVertices.get(i);
            if (recoverVertex.equals(pathDeviation)) {
                proceed = false;
            }
            customTree.recoverVertex(recoverVertex);
            customTree.correctDistanceForward(recoverVertex);
            GraphPath spurPath = customTree.getPath(recoverVertex);
            if (spurPath != null) {
                customTree.correctDistanceBackward(recoverVertex);
                if (i <= lastDeviationIndex) {
                    GraphPath<V, E> candidate = this.getCandidatePath(path, i, spurPath);
                    double candidateWeight = candidate.getWeight();
                    V candidateLastDeviation = this.getLastValidDeviation(candidate, recoverVertex);
                    boolean candidateIsValid = candidateLastDeviation == null;
                    this.candidatePaths.insert((Object)candidateWeight, Pair.of(candidate, candidateIsValid));
                    this.firstDeviations.put(candidate, recoverVertex);
                    this.lastDeviations.put(candidate, candidateLastDeviation);
                    if (candidateIsValid) {
                        ++result;
                    }
                }
            }
            V recoverVertexSuccessor = pathVertices.get(i + 1);
            E edge = pathEdges.get(i);
            customTree.recoverEdge(edge);
            double recoverVertexUpdatedDistance = maskSubgraph.getEdgeWeight(edge) + (Double)((Pair)customTree.map.get(recoverVertexSuccessor)).getFirst();
            if (!((Double)((Pair)customTree.map.get(recoverVertex)).getFirst() > recoverVertexUpdatedDistance)) continue;
            customTree.map.put(recoverVertex, Pair.of(recoverVertexUpdatedDistance, edge));
            customTree.correctDistanceBackward(recoverVertex);
        }
        return result;
    }

    private Pair<Set<V>, Set<E>> getMaskedVerticesAndEdges(GraphPath<V, E> path, V pathDeviation, int pathDeviationIndex) {
        List<V> pathVertices = path.getVertexList();
        List<E> pathEdges = path.getEdgeList();
        HashSet<V> maskedVertices = new HashSet<V>();
        HashSet<E> maskedEdges = new HashSet<E>();
        int pathVerticesSize = pathVertices.size();
        for (int i = 0; i < pathVerticesSize - 1; ++i) {
            maskedVertices.add(pathVertices.get(i));
            maskedEdges.add(pathEdges.get(i));
        }
        int resultListSize = this.resultList.size();
        for (int i = 0; i < resultListSize - 1; ++i) {
            GraphPath<V, E> resultPath = this.resultList.get(i);
            List<V> resultPathVertices = resultPath.getVertexList();
            int deviationIndex = resultPathVertices.indexOf(pathDeviation);
            if (deviationIndex < 0 || deviationIndex != pathDeviationIndex || !this.equalLists(pathVertices, resultPathVertices, deviationIndex)) continue;
            maskedEdges.add(resultPath.getEdgeList().get(deviationIndex));
        }
        return Pair.of(maskedVertices, maskedEdges);
    }

    private GraphPath<V, E> getCandidatePath(GraphPath<V, E> path, int recoverVertexIndex, GraphPath<V, E> spurPath) {
        List<V> pathVertices = path.getVertexList();
        List<E> pathEdges = path.getEdgeList();
        LinkedList<V> candidatePathVertices = new LinkedList<V>();
        LinkedList<E> candidatePathEdges = new LinkedList<E>();
        double rootPathWeight = 0.0;
        for (int i = 0; i < recoverVertexIndex; ++i) {
            E edge = pathEdges.get(i);
            rootPathWeight += this.graph.getEdgeWeight(edge);
            candidatePathEdges.add(edge);
            candidatePathVertices.add(pathVertices.get(i));
        }
        ListIterator<V> spurPathVerticesIterator = spurPath.getVertexList().listIterator(spurPath.getVertexList().size());
        while (spurPathVerticesIterator.hasPrevious()) {
            candidatePathVertices.add(spurPathVerticesIterator.previous());
        }
        ListIterator<E> spurPathEdgesIterator = spurPath.getEdgeList().listIterator(spurPath.getEdgeList().size());
        while (spurPathEdgesIterator.hasPrevious()) {
            candidatePathEdges.add(spurPathEdgesIterator.previous());
        }
        double candidateWeight = rootPathWeight + spurPath.getWeight();
        return new GraphWalk<V, E>(this.graph, this.source, this.sink, candidatePathVertices, candidatePathEdges, candidateWeight);
    }

    private boolean equalLists(List<V> first, List<V> second, int index) {
        for (int i = 0; i <= index; ++i) {
            if (first.get(i).equals(second.get(i))) continue;
            return false;
        }
        return true;
    }

    class YenShortestPathsTree
    extends TreeSingleSourcePathsImpl<V, E> {
        Set<V> maskedVertices;
        Set<E> maskedEdges;

        YenShortestPathsTree(Graph<V, E> maskSubgraph, Set<V> maskedVertices, Set<E> maskedEdges, Map<V, Pair<Double, E>> reversedTree, V treeSource) {
            super(maskSubgraph, treeSource, reversedTree);
            this.maskedVertices = maskedVertices;
            this.maskedEdges = maskedEdges;
        }

        void recoverVertex(V v) {
            this.maskedVertices.remove(v);
        }

        void recoverEdge(E e) {
            this.maskedEdges.remove(e);
        }

        void correctDistanceForward(V v) {
            this.map.putIfAbsent((Pair<Double, Object>)v, new Pair<Double, Object>(Double.POSITIVE_INFINITY, null));
            for (Object e : this.g.outgoingEdgesOf(v)) {
                Object successor = Graphs.getOppositeVertex(this.g, e, v);
                if (successor.equals(v)) continue;
                double updatedDistance = Double.POSITIVE_INFINITY;
                if (this.map.containsKey(successor)) {
                    updatedDistance = this.map.get(successor).getFirst();
                }
                updatedDistance += this.g.getEdgeWeight(e);
                double currentDistance = this.map.get(v).getFirst();
                if (!(currentDistance > updatedDistance)) continue;
                this.map.put(v, Pair.of(updatedDistance, e));
            }
        }

        void correctDistanceBackward(V v) {
            LinkedList vertices = new LinkedList();
            vertices.add(v);
            while (!vertices.isEmpty()) {
                Object vertex = vertices.remove(0);
                double vertexDistance = this.map.get(vertex).getFirst();
                for (Object e : this.g.incomingEdgesOf(vertex)) {
                    double updatedDistance;
                    Object predecessor = Graphs.getOppositeVertex(this.g, e, vertex);
                    if (predecessor.equals(vertex)) continue;
                    double predecessorDistance = Double.POSITIVE_INFINITY;
                    if (this.map.containsKey(predecessor)) {
                        predecessorDistance = this.map.get(predecessor).getFirst();
                    }
                    if (!(predecessorDistance > (updatedDistance = vertexDistance + this.g.getEdgeWeight(e)))) continue;
                    this.map.put(predecessor, Pair.of(updatedDistance, e));
                    vertices.add(predecessor);
                }
            }
        }
    }
}

