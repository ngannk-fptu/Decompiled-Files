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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.MaskSubgraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class ContractionHierarchyPrecomputation<V, E> {
    private Graph<V, E> graph;
    private Graph<ContractionVertex<V>, ContractionEdge<E>> contractionGraph;
    private Map<V, ContractionVertex<V>> contractionMapping;
    private Graph<ContractionVertex<V>, ContractionEdge<E>> maskedContractionGraph;
    private List<ContractionVertex<V>> vertices;
    private List<List<Pair<ContractionEdge<E>, ContractionEdge<E>>>> shortcutEdges;
    private List<VertexData> verticesData;
    private AtomicInteger contractionLevelCounter;
    private Supplier<AddressableHeap<Double, ContractionVertex<V>>> shortcutsSearchHeapSupplier;
    private ExecutorCompletionService<Void> completionService;
    private int parallelism;
    private List<ContractionTask> tasks;
    private List<Consumer<ContractionVertex<V>>> computeInitialPrioritiesConsumers;
    private Consumer<ContractionVertex<V>> computeIndependentSetConsumer;
    private Consumer<ContractionVertex<V>> computeShortcutsConsumer;
    private Consumer<ContractionVertex<V>> updateNeighboursConsumer;
    private Consumer<ContractionVertex<V>> markUpwardEdgesConsumer;

    public ContractionHierarchyPrecomputation(Graph<V, E> graph, ThreadPoolExecutor executor) {
        this(graph, Random::new, executor);
    }

    public ContractionHierarchyPrecomputation(Graph<V, E> graph, Supplier<Random> randomSupplier, ThreadPoolExecutor executor) {
        this(graph, randomSupplier, PairingHeap::new, executor);
    }

    public ContractionHierarchyPrecomputation(Graph<V, E> graph, Supplier<Random> randomSupplier, Supplier<AddressableHeap<Double, ContractionVertex<V>>> shortcutsSearchHeapSupplier, ThreadPoolExecutor executor) {
        this.init(graph, randomSupplier, shortcutsSearchHeapSupplier, executor);
    }

    private void init(Graph<V, E> graph, final Supplier<Random> randomSupplier, Supplier<AddressableHeap<Double, ContractionVertex<V>>> shortcutsSearchHeapSupplier, ThreadPoolExecutor executor) {
        this.graph = graph;
        this.contractionGraph = GraphTypeBuilder.directed().weighted(true).allowingMultipleEdges(false).allowingSelfLoops(false).buildGraph();
        this.parallelism = executor.getMaximumPoolSize();
        this.shortcutsSearchHeapSupplier = shortcutsSearchHeapSupplier;
        this.vertices = new ArrayList<ContractionVertex<V>>(graph.vertexSet().size());
        this.shortcutEdges = new ArrayList<Object>(Collections.nCopies(graph.vertexSet().size(), null));
        this.verticesData = new ArrayList<Object>(Collections.nCopies(graph.vertexSet().size(), null));
        this.contractionLevelCounter = new AtomicInteger();
        this.maskedContractionGraph = new MaskSubgraph<ContractionVertex, ContractionEdge>(this.contractionGraph, v -> this.verticesData.get(v.vertexId) != null && this.verticesData.get((int)v.vertexId).isContracted, e -> false);
        this.contractionMapping = new HashMap<V, ContractionVertex<V>>();
        this.completionService = new ExecutorCompletionService(executor);
        this.tasks = new ArrayList<ContractionTask>(this.parallelism);
        this.computeInitialPrioritiesConsumers = new ArrayList<Consumer<ContractionVertex<V>>>(this.parallelism);
        for (int i = 0; i < this.parallelism; ++i) {
            this.tasks.add(new ContractionTask(i));
            this.computeInitialPrioritiesConsumers.add(new Consumer<ContractionVertex<V>>(){
                Random random;
                {
                    this.random = (Random)randomSupplier.get();
                }

                @Override
                public void accept(ContractionVertex<V> vertex) {
                    ContractionHierarchyPrecomputation.this.verticesData.set(vertex.vertexId, ContractionHierarchyPrecomputation.this.getVertexData(vertex, this.random.nextInt()));
                }
            });
        }
        this.computeIndependentSetConsumer = vertex -> {
            this.verticesData.get((int)vertex.vertexId).isIndependent = this.vertexIsIndependent((ContractionVertex<V>)vertex);
        };
        this.computeShortcutsConsumer = vertex -> this.shortcutEdges.set(vertex.vertexId, this.getShortcuts((ContractionVertex<V>)vertex));
        this.updateNeighboursConsumer = vertex -> this.updateNeighboursData((ContractionVertex<V>)vertex);
        this.markUpwardEdgesConsumer = vertex -> this.contractionGraph.outgoingEdgesOf((ContractionVertex<ContractionVertex>)vertex).forEach(e -> {
            e.isUpward = this.contractionGraph.getEdgeSource(e).contractionLevel < this.contractionGraph.getEdgeTarget(e).contractionLevel;
        });
    }

    public ContractionHierarchy<V, E> computeContractionHierarchy() {
        this.fillContractionGraphAndVerticesArray();
        this.submitTasks(0, this.contractionGraph.vertexSet().size(), this.computeInitialPrioritiesConsumers);
        this.contractVertices();
        this.submitTasks(0, this.contractionGraph.vertexSet().size(), this.markUpwardEdgesConsumer);
        return new ContractionHierarchy<V, E>(this.graph, this.contractionGraph, this.contractionMapping);
    }

    private void fillContractionGraphAndVerticesArray() {
        int vertexId = 0;
        for (V vertex : this.graph.vertexSet()) {
            ContractionVertex<V> contractionVertex = new ContractionVertex<V>(vertex, vertexId);
            this.vertices.add(contractionVertex);
            ++vertexId;
            this.contractionGraph.addVertex(contractionVertex);
            this.contractionMapping.put((ContractionVertex<V>)vertex, (ContractionVertex<ContractionVertex<V>>)contractionVertex);
        }
        for (Object e : this.graph.edgeSet()) {
            V target;
            V source = this.graph.getEdgeSource(e);
            if (source.equals(target = this.graph.getEdgeTarget(e))) continue;
            ContractionVertex<V> contractionSource = this.contractionMapping.get(source);
            ContractionVertex<V> contractionTarget = this.contractionMapping.get(target);
            double eWeight = this.graph.getEdgeWeight(e);
            ContractionEdge<E> oldEdge = this.contractionGraph.getEdge(contractionSource, contractionTarget);
            if (oldEdge == null) {
                ContractionEdge<Object> forward = new ContractionEdge<Object>(e);
                this.contractionGraph.addEdge(contractionSource, contractionTarget, forward);
                this.contractionGraph.setEdgeWeight(forward, eWeight);
                if (!this.graph.getType().isUndirected()) continue;
                ContractionEdge<Object> backward = new ContractionEdge<Object>(e);
                this.contractionGraph.addEdge(contractionTarget, contractionSource, backward);
                this.contractionGraph.setEdgeWeight(backward, eWeight);
                continue;
            }
            double oldWeight = this.contractionGraph.getEdgeWeight(oldEdge);
            if (!(eWeight < oldWeight)) continue;
            this.contractionGraph.setEdgeWeight(oldEdge, eWeight);
            oldEdge.edge = e;
            if (!this.graph.getType().isUndirected()) continue;
            ContractionEdge<E> oldBackwardEdge = this.contractionGraph.getEdge(contractionTarget, contractionSource);
            oldBackwardEdge.edge = e;
            this.contractionGraph.setEdgeWeight(oldBackwardEdge, eWeight);
        }
    }

    private void contractVertices() {
        int independentSetEnd = this.graph.vertexSet().size();
        while (independentSetEnd != 0) {
            this.submitTasks(0, independentSetEnd, this.computeIndependentSetConsumer);
            int independentSetStart = this.partitionIndependentSet(independentSetEnd);
            this.submitTasks(independentSetStart, independentSetEnd, this.computeShortcutsConsumer);
            this.contractIndependentSet(independentSetStart, independentSetEnd);
            this.submitTasks(independentSetStart, independentSetEnd, this.updateNeighboursConsumer);
            this.markContracted(independentSetStart, independentSetEnd);
            independentSetEnd = independentSetStart;
        }
    }

    private boolean vertexIsIndependent(ContractionVertex<V> vertex) {
        for (ContractionVertex<V> firstLevelNeighbour : Graphs.neighborSetOf(this.maskedContractionGraph, vertex)) {
            if (this.isGreater(vertex, firstLevelNeighbour)) {
                return false;
            }
            for (ContractionVertex<V> secondLevelNeighbour : Graphs.neighborSetOf(this.maskedContractionGraph, firstLevelNeighbour)) {
                if (secondLevelNeighbour.equals(vertex) || !this.isGreater(vertex, secondLevelNeighbour)) continue;
                return false;
            }
        }
        return true;
    }

    private boolean isGreater(ContractionVertex<V> vertex1, ContractionVertex<V> vertex2) {
        VertexData data1 = this.verticesData.get(vertex1.vertexId);
        VertexData data2 = this.verticesData.get(vertex2.vertexId);
        if (data1.priority != data2.priority) {
            return data1.priority > data2.priority;
        }
        if (data1.random != data2.random) {
            return data1.random > data2.random;
        }
        return vertex1.vertexId > vertex2.vertexId;
    }

    private int partitionIndependentSet(int notContractedVerticesEnd) {
        int left = 0;
        int right = notContractedVerticesEnd - 1;
        while (left <= right) {
            while (!this.verticesData.get((int)left).isIndependent) {
                ++left;
            }
            while (right >= 0 && this.verticesData.get((int)right).isIndependent) {
                --right;
            }
            if (left > right) continue;
            ContractionVertex<V> leftVertex = this.vertices.get(left);
            ContractionVertex<V> rightVertex = this.vertices.get(right);
            this.swap(this.verticesData, left, right);
            this.swap(this.vertices, left, right);
            this.swap(this.shortcutEdges, left, right);
            int tmpId = leftVertex.vertexId;
            leftVertex.vertexId = rightVertex.vertexId;
            rightVertex.vertexId = tmpId;
        }
        return left;
    }

    private <T> void swap(List<T> list, int i, int j) {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }

    private void contractIndependentSet(int independentSetStart, int independentSetEnd) {
        this.vertices.subList(independentSetStart, independentSetEnd).forEach(v -> this.contractVertex((ContractionVertex<V>)v, this.contractionLevelCounter.getAndIncrement()));
    }

    private void contractVertex(ContractionVertex<V> vertex, int contractionLevel) {
        List<Pair<ContractionEdge<E>, ContractionEdge<E>>> shortcuts = this.shortcutEdges.get(vertex.vertexId);
        for (Pair pair : shortcuts) {
            ContractionVertex<V> shortcutSource = this.maskedContractionGraph.getEdgeSource(pair.getFirst());
            ContractionVertex<V> shortcutTarget = this.maskedContractionGraph.getEdgeTarget(pair.getSecond());
            ContractionEdge shortcutEdge = new ContractionEdge(pair);
            double shortcutWeight = this.maskedContractionGraph.getEdgeWeight(pair.getFirst()) + this.maskedContractionGraph.getEdgeWeight(pair.getSecond());
            boolean added = this.contractionGraph.addEdge(shortcutSource, shortcutTarget, shortcutEdge);
            if (added) {
                this.contractionGraph.setEdgeWeight(shortcutEdge, shortcutWeight);
                continue;
            }
            ContractionEdge<E> originalEdge = this.contractionGraph.getEdge(shortcutSource, shortcutTarget);
            originalEdge.edge = null;
            originalEdge.bypassedEdges = pair;
            originalEdge.originalEdges = pair.getFirst().originalEdges + pair.getSecond().originalEdges;
            this.contractionGraph.setEdgeWeight(originalEdge, shortcutWeight);
        }
        vertex.contractionLevel = contractionLevel;
    }

    private void updateNeighboursData(ContractionVertex<V> vertex) {
        VertexData vertexData = this.verticesData.get(vertex.vertexId);
        for (ContractionVertex<V> neighbour : Graphs.neighborSetOf(this.maskedContractionGraph, vertex)) {
            VertexData neighbourData = this.verticesData.get(neighbour.vertexId);
            neighbourData.depth = Math.max(neighbourData.depth, vertexData.depth + 1);
            this.updatePriority(neighbour, neighbourData);
        }
    }

    private VertexData getVertexData(ContractionVertex<V> vertex, int random) {
        VertexData result = new VertexData(random);
        this.updatePriority(vertex, result);
        return result;
    }

    private void updatePriority(ContractionVertex<V> vertex, VertexData data) {
        VertexStatistics statistics = this.getStatistics(vertex);
        data.priority = statistics.removedContractionEdges * statistics.removedOriginalEdges == 0 ? (double)data.depth : 4.0 * (double)statistics.addedContractionEdges / (double)statistics.removedContractionEdges + 2.0 * (double)statistics.addedOriginalEdges / (double)statistics.removedOriginalEdges + 1.0 * (double)data.depth;
    }

    private VertexStatistics getStatistics(ContractionVertex<V> vertex) {
        ToStatisticsConsumer consumer = new ToStatisticsConsumer();
        this.iterateShortcutEdges(vertex, consumer);
        this.maskedContractionGraph.edgesOf(vertex).forEach(edge -> {
            ++consumer.statistics.removedContractionEdges;
            consumer.statistics.removedOriginalEdges += edge.originalEdges;
        });
        return consumer.statistics;
    }

    private List<Pair<ContractionEdge<E>, ContractionEdge<E>>> getShortcuts(ContractionVertex<V> vertex) {
        ToListConsumer consumer = new ToListConsumer();
        this.iterateShortcutEdges(vertex, consumer);
        return consumer.shortcuts;
    }

    private void iterateShortcutEdges(ContractionVertex<V> vertex, BiConsumer<ContractionEdge<E>, ContractionEdge<E>> shortcutConsumer) {
        HashSet<ContractionVertex<V>> successors = new HashSet<ContractionVertex<V>>();
        double maxOutgoingEdgeWeight = Double.MIN_VALUE;
        for (ContractionEdge<E> outEdge : this.maskedContractionGraph.outgoingEdgesOf(vertex)) {
            ContractionVertex<V> successor = this.maskedContractionGraph.getEdgeTarget(outEdge);
            if (this.verticesData.get(successor.vertexId) != null && this.verticesData.get((int)successor.vertexId).isIndependent) continue;
            successors.add(successor);
            maxOutgoingEdgeWeight = Math.max(maxOutgoingEdgeWeight, this.contractionGraph.getEdgeWeight(outEdge));
        }
        for (ContractionEdge<E> inEdge : this.maskedContractionGraph.incomingEdgesOf(vertex)) {
            ContractionVertex<V> predecessor = this.contractionGraph.getEdgeSource(inEdge);
            if (this.verticesData.get(predecessor.vertexId) != null && this.verticesData.get((int)predecessor.vertexId).isIndependent) continue;
            boolean containedPredecessor = successors.remove(predecessor);
            Map<ContractionVertex<V>, AddressableHeap.Handle<Double, ContractionVertex<V>>> distances = this.iterateToSuccessors(this.maskedContractionGraph, predecessor, successors, vertex, this.contractionGraph.getEdgeWeight(inEdge) + maxOutgoingEdgeWeight);
            for (ContractionVertex contractionVertex : successors) {
                ContractionEdge<E> outEdge = this.contractionGraph.getEdge(vertex, contractionVertex);
                double pathWeight = this.contractionGraph.getEdgeWeight(inEdge) + this.contractionGraph.getEdgeWeight(outEdge);
                if (distances.containsKey(contractionVertex) && !((Double)distances.get(contractionVertex).getKey() > pathWeight)) continue;
                shortcutConsumer.accept(inEdge, outEdge);
                if (!this.graph.getType().isUndirected()) continue;
                shortcutConsumer.accept(this.contractionGraph.getEdge(contractionVertex, vertex), this.contractionGraph.getEdge(vertex, predecessor));
            }
            if (!containedPredecessor || !this.graph.getType().isDirected()) continue;
            successors.add(predecessor);
        }
    }

    private Map<ContractionVertex<V>, AddressableHeap.Handle<Double, ContractionVertex<V>>> iterateToSuccessors(Graph<ContractionVertex<V>, ContractionEdge<E>> graph, ContractionVertex<V> source, Set<ContractionVertex<V>> successors, ContractionVertex<V> vertexToIgnore, double radius) {
        AddressableHeap<Double, ContractionVertex<V>> heap = this.shortcutsSearchHeapSupplier.get();
        HashMap<ContractionVertex<V>, AddressableHeap.Handle<Double, ContractionVertex<V>>> distanceMap = new HashMap<ContractionVertex<V>, AddressableHeap.Handle<Double, ContractionVertex<V>>>();
        this.updateDistance(source, 0.0, heap, distanceMap);
        int numOfSuccessors = successors.size();
        int passedSuccessors = 0;
        while (!heap.isEmpty()) {
            AddressableHeap.Handle min = heap.deleteMin();
            ContractionVertex vertex = (ContractionVertex)min.getValue();
            double distance = (Double)min.getKey();
            if (distance > radius || successors.contains(vertex) && ++passedSuccessors == numOfSuccessors) break;
            this.relaxNode(graph, heap, distanceMap, vertex, distance, vertexToIgnore);
        }
        return distanceMap;
    }

    private void relaxNode(Graph<ContractionVertex<V>, ContractionEdge<E>> graph, AddressableHeap<Double, ContractionVertex<V>> heap, Map<ContractionVertex<V>, AddressableHeap.Handle<Double, ContractionVertex<V>>> distanceMap, ContractionVertex<V> vertex, double vertexDistance, ContractionVertex<V> vertexToIgnore) {
        for (ContractionEdge<E> edge : graph.outgoingEdgesOf(vertex)) {
            ContractionVertex<V> successor = graph.getEdgeTarget(edge);
            double edgeWeight = graph.getEdgeWeight(edge);
            if (edgeWeight < 0.0) {
                throw new IllegalArgumentException("Negative edge weight not allowed");
            }
            if (successor.equals(vertexToIgnore) || this.verticesData.get(successor.vertexId) != null && this.verticesData.get((int)successor.vertexId).isIndependent) continue;
            double updatedDistance = vertexDistance + edgeWeight;
            this.updateDistance(successor, updatedDistance, heap, distanceMap);
        }
    }

    private void updateDistance(ContractionVertex<V> vertex, double distance, AddressableHeap<Double, ContractionVertex<V>> heap, Map<ContractionVertex<V>, AddressableHeap.Handle<Double, ContractionVertex<V>>> distanceMap) {
        AddressableHeap.Handle node = distanceMap.get(vertex);
        if (node == null) {
            node = heap.insert((Object)distance, vertex);
            distanceMap.put(vertex, node);
        } else if (distance < (Double)node.getKey()) {
            node.decreaseKey((Object)distance);
        }
    }

    private void markContracted(int independentSetStart, int independentSetEnd) {
        for (int i = independentSetStart; i < independentSetEnd; ++i) {
            this.verticesData.get((int)this.vertices.get((int)i).vertexId).isContracted = true;
        }
    }

    private void submitTasks(int segmentStart, int segmentEnd, Consumer<ContractionVertex<V>> consumer) {
        for (ContractionTask task : this.tasks) {
            task.consumer = consumer;
            task.segmentStart = segmentStart;
            task.segmentsEnd = segmentEnd;
            this.completionService.submit(task, null);
        }
        this.waitForTasksCompletion(this.tasks.size());
    }

    private void submitTasks(int segmentStart, int segmentEnd, List<Consumer<ContractionVertex<V>>> consumers) {
        for (int i = 0; i < this.tasks.size(); ++i) {
            ContractionTask task = this.tasks.get(i);
            task.consumer = consumers.get(i);
            task.segmentStart = segmentStart;
            task.segmentsEnd = segmentEnd;
            this.completionService.submit(task, null);
        }
        this.waitForTasksCompletion(this.tasks.size());
    }

    private void waitForTasksCompletion(int numOfTasks) {
        for (int i = 0; i < numOfTasks; ++i) {
            try {
                this.completionService.take().get();
                continue;
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private class ContractionTask
    implements Runnable {
        int taskId;
        int segmentStart;
        int segmentsEnd;
        Consumer<ContractionVertex<V>> consumer;

        public ContractionTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            int start = this.workerSegmentStart(this.segmentStart, this.segmentsEnd);
            int end = this.workerSegmentEnd(this.segmentStart, this.segmentsEnd);
            for (int i = start; i < end; ++i) {
                this.consumer.accept(ContractionHierarchyPrecomputation.this.vertices.get(i));
            }
        }

        private int workerSegmentStart(int segmentStart, int segmentEnd) {
            return segmentStart + (segmentEnd - segmentStart) * this.taskId / ContractionHierarchyPrecomputation.this.parallelism;
        }

        private int workerSegmentEnd(int segmentStart, int segmentEnd) {
            return segmentStart + (segmentEnd - segmentStart) * (this.taskId + 1) / ContractionHierarchyPrecomputation.this.parallelism;
        }
    }

    public static class ContractionHierarchy<V, E> {
        private Graph<V, E> graph;
        private Graph<ContractionVertex<V>, ContractionEdge<E>> contractionGraph;
        private Map<V, ContractionVertex<V>> contractionMapping;

        public Graph<V, E> getGraph() {
            return this.graph;
        }

        public Graph<ContractionVertex<V>, ContractionEdge<E>> getContractionGraph() {
            return this.contractionGraph;
        }

        public Map<V, ContractionVertex<V>> getContractionMapping() {
            return this.contractionMapping;
        }

        ContractionHierarchy(Graph<V, E> graph, Graph<ContractionVertex<V>, ContractionEdge<E>> contractionGraph, Map<V, ContractionVertex<V>> contractionMapping) {
            this.graph = graph;
            this.contractionGraph = contractionGraph;
            this.contractionMapping = contractionMapping;
        }

        public void unpackBackward(ContractionEdge<E> edge, LinkedList<V> vertexList, LinkedList<E> edgeList) {
            if (edge.bypassedEdges == null) {
                vertexList.addFirst(this.contractionGraph.getEdgeSource(edge).vertex);
                edgeList.addFirst(edge.edge);
            } else {
                this.unpackBackward(edge.bypassedEdges.getSecond(), vertexList, edgeList);
                this.unpackBackward(edge.bypassedEdges.getFirst(), vertexList, edgeList);
            }
        }

        public void unpackForward(ContractionEdge<E> edge, LinkedList<V> vertexList, LinkedList<E> edgeList) {
            if (edge.bypassedEdges == null) {
                vertexList.addLast(this.contractionGraph.getEdgeTarget(edge).vertex);
                edgeList.addLast(edge.edge);
            } else {
                this.unpackForward(edge.bypassedEdges.getFirst(), vertexList, edgeList);
                this.unpackForward(edge.bypassedEdges.getSecond(), vertexList, edgeList);
            }
        }
    }

    public static class ContractionVertex<V1> {
        int vertexId;
        V1 vertex;
        int contractionLevel;

        ContractionVertex(V1 vertex, int vertexId) {
            this.vertexId = vertexId;
            this.vertex = vertex;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ContractionVertex that = (ContractionVertex)o;
            return Objects.equals(this.vertex, that.vertex);
        }

        public int hashCode() {
            return Objects.hash(this.vertex);
        }
    }

    public static class ContractionEdge<E1> {
        E1 edge;
        Pair<ContractionEdge<E1>, ContractionEdge<E1>> bypassedEdges;
        boolean isUpward;
        int originalEdges;

        ContractionEdge(E1 edge) {
            this.edge = edge;
            this.originalEdges = 1;
        }

        ContractionEdge(Pair<ContractionEdge<E1>, ContractionEdge<E1>> bypassedEdges) {
            this.bypassedEdges = bypassedEdges;
            this.originalEdges = bypassedEdges.getFirst().originalEdges + bypassedEdges.getSecond().originalEdges;
        }
    }

    private static class VertexData {
        int depth;
        int random;
        double priority;
        boolean isContracted;
        boolean isIndependent;

        VertexData(int random) {
            this.random = random;
        }
    }

    private static class VertexStatistics {
        int addedContractionEdges;
        int removedContractionEdges;
        int addedOriginalEdges;
        int removedOriginalEdges;

        private VertexStatistics() {
        }
    }

    private class ToStatisticsConsumer
    implements BiConsumer<ContractionEdge<E>, ContractionEdge<E>> {
        VertexStatistics statistics = new VertexStatistics();

        ToStatisticsConsumer() {
        }

        @Override
        public void accept(ContractionEdge<E> e1, ContractionEdge<E> e2) {
            ++this.statistics.addedContractionEdges;
            this.statistics.addedOriginalEdges += e1.originalEdges + e2.originalEdges;
        }
    }

    private class ToListConsumer
    implements BiConsumer<ContractionEdge<E>, ContractionEdge<E>> {
        List<Pair<ContractionEdge<E>, ContractionEdge<E>>> shortcuts = new ArrayList();

        ToListConsumer() {
        }

        @Override
        public void accept(ContractionEdge<E> e1, ContractionEdge<E> e2) {
            this.shortcuts.add(Pair.of(e1, e2));
        }
    }
}

