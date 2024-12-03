/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.util.ConcurrencyUtil;

public class DeltaSteppingShortestPath<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    private static final String NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED = "Negative edge weight not allowed";
    private static final String DELTA_MUST_BE_NON_NEGATIVE = "Delta must be non-negative";
    private static final int DEFAULT_PARALLELISM = Runtime.getRuntime().availableProcessors();
    private static final int TASKS_TO_THREADS_RATIO = 20;
    private double delta;
    private int parallelism;
    private int numOfBuckets;
    private double maxEdgeWeight;
    private Map<V, Pair<Double, E>> distanceAndPredecessorMap;
    private List<Set<V>> bucketStructure;
    private Comparator<V> vertexComparator;
    private Supplier<Set<V>> bucketsSupplier;
    private ExecutorCompletionService<Void> completionService;
    private Queue<V> verticesQueue;
    private Runnable lightRelaxTask;
    private Runnable heavyRelaxTask;
    private volatile boolean allVerticesAdded;

    public DeltaSteppingShortestPath(Graph<V, E> graph, ThreadPoolExecutor executor) {
        this(graph, 0.0, executor);
    }

    public DeltaSteppingShortestPath(Graph<V, E> graph, ThreadPoolExecutor executor, Comparator<V> vertexComparator) {
        this(graph, 0.0, executor, vertexComparator);
    }

    @Deprecated
    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta) {
        this(graph, delta, DEFAULT_PARALLELISM);
    }

    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta, ThreadPoolExecutor executor) {
        super(graph);
        Objects.requireNonNull(executor, "executor must not be null!");
        this.init(graph, delta, executor, null);
    }

    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta, ThreadPoolExecutor executor, Comparator<V> vertexComparator) {
        super(graph);
        Objects.requireNonNull(executor, "executor must not be null!");
        Objects.requireNonNull(executor, "vertexComparator must not be null!");
        this.init(graph, delta, executor, vertexComparator);
    }

    @Deprecated
    public DeltaSteppingShortestPath(Graph<V, E> graph, int parallelism) {
        this(graph, 0.0, parallelism);
    }

    @Deprecated
    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta, int parallelism) {
        super(graph);
        this.init(graph, delta, ConcurrencyUtil.createThreadPoolExecutor(parallelism), null);
    }

    private void init(Graph<V, E> graph, double delta, ThreadPoolExecutor executor, Comparator<V> vertexComparator) {
        if (delta < 0.0) {
            throw new IllegalArgumentException(DELTA_MUST_BE_NON_NEGATIVE);
        }
        this.delta = delta;
        this.parallelism = executor.getMaximumPoolSize();
        this.vertexComparator = vertexComparator;
        this.distanceAndPredecessorMap = new ConcurrentHashMap<V, Pair<Double, E>>(graph.vertexSet().size());
        this.completionService = new ExecutorCompletionService(executor);
        this.verticesQueue = new ConcurrentLinkedQueue<V>();
        this.lightRelaxTask = new LightRelaxTask(this.verticesQueue);
        this.heavyRelaxTask = new HeavyRelaxTask(this.verticesQueue);
    }

    private double getMaxEdgeWeight() {
        ForkJoinTask<Double> task = ForkJoinPool.commonPool().submit(new MaxEdgeWeightTask(this.graph.edgeSet().spliterator(), this.graph.edgeSet().size() / (20 * this.parallelism) + 1));
        return task.join();
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        return this.getPaths(source).getPath(sink);
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        this.maxEdgeWeight = this.getMaxEdgeWeight();
        if (this.delta == 0.0) {
            this.delta = this.findDelta();
        }
        this.numOfBuckets = (int)(Math.ceil(this.maxEdgeWeight / this.delta) + 1.0);
        this.bucketStructure = new ArrayList<Set<V>>(this.numOfBuckets);
        this.bucketsSupplier = this.getBucketsSupplier(source);
        for (int i = 0; i < this.numOfBuckets; ++i) {
            this.bucketStructure.add(this.bucketsSupplier.get());
        }
        this.fillDistanceAndPredecessorMap();
        this.computeShortestPaths(source);
        return new TreeSingleSourcePathsImpl<V, E>(this.graph, source, this.distanceAndPredecessorMap);
    }

    private Supplier<Set<V>> getBucketsSupplier(V vertex) {
        if (this.vertexComparator != null) {
            return () -> new ConcurrentSkipListSet<V>(this.vertexComparator);
        }
        if (vertex instanceof Comparable) {
            return () -> new ConcurrentSkipListSet();
        }
        return () -> Collections.newSetFromMap(new ConcurrentHashMap());
    }

    private double findDelta() {
        if (this.maxEdgeWeight == 0.0) {
            return 1.0;
        }
        int maxOutDegree = this.graph.vertexSet().parallelStream().mapToInt(this.graph::outDegreeOf).max().orElse(0);
        return this.maxEdgeWeight / (double)maxOutDegree;
    }

    private void fillDistanceAndPredecessorMap() {
        this.graph.vertexSet().parallelStream().forEach(v -> this.distanceAndPredecessorMap.put((Pair<Double, Object>)v, (Pair<Double, E>)Pair.of(Double.POSITIVE_INFINITY, null)));
    }

    private void computeShortestPaths(V source) {
        this.relax(source, null, 0.0);
        ArrayList<Set<V>> removed = new ArrayList<Set<V>>();
        while (true) {
            int firstNonEmptyBucket;
            for (firstNonEmptyBucket = 0; firstNonEmptyBucket < this.numOfBuckets && this.bucketStructure.get(firstNonEmptyBucket).isEmpty(); ++firstNonEmptyBucket) {
            }
            if (firstNonEmptyBucket == this.numOfBuckets) break;
            Set<V> bucketElements = this.getContentAndReplace(firstNonEmptyBucket);
            while (!bucketElements.isEmpty()) {
                removed.add(bucketElements);
                this.findAndRelaxLightRequests(bucketElements);
                bucketElements = this.getContentAndReplace(firstNonEmptyBucket);
            }
            this.findAndRelaxHeavyRequests(removed);
            removed.clear();
        }
    }

    private void findAndRelaxLightRequests(Set<V> vertices) {
        int numOfTasks;
        this.allVerticesAdded = false;
        int numOfVertices = vertices.size();
        if (numOfVertices >= this.parallelism) {
            numOfTasks = this.parallelism;
            Iterator<V> iterator = vertices.iterator();
            this.addSetVertices(iterator, this.parallelism);
            this.submitTasks(this.lightRelaxTask, this.parallelism - 1);
            this.addSetRemaining(iterator);
            this.submitTasks(this.lightRelaxTask, 1);
        } else {
            numOfTasks = numOfVertices;
            this.addSetRemaining(vertices.iterator());
            this.submitTasks(this.lightRelaxTask, numOfVertices);
        }
        this.allVerticesAdded = true;
        this.waitForTasksCompletion(numOfTasks);
    }

    private void findAndRelaxHeavyRequests(List<Set<V>> verticesSets) {
        int numOfTasks;
        this.allVerticesAdded = false;
        int numOfVertices = verticesSets.stream().mapToInt(Set::size).sum();
        if (numOfVertices >= this.parallelism) {
            numOfTasks = this.parallelism;
            Iterator<Set<V>> setIterator = verticesSets.iterator();
            Iterator<V> iterator = this.addSetsVertices(setIterator, this.parallelism);
            this.submitTasks(this.heavyRelaxTask, this.parallelism - 1);
            this.addSetRemaining(iterator);
            this.addSetsRemaining(setIterator);
            this.submitTasks(this.heavyRelaxTask, 1);
        } else {
            numOfTasks = numOfVertices;
            this.addSetsRemaining(verticesSets.iterator());
            this.submitTasks(this.heavyRelaxTask, numOfVertices);
        }
        this.allVerticesAdded = true;
        this.waitForTasksCompletion(numOfTasks);
    }

    private void addSetVertices(Iterator<V> iterator, int numOfVertices) {
        for (int i = 0; i < numOfVertices && iterator.hasNext(); ++i) {
            this.verticesQueue.add(iterator.next());
        }
    }

    private void addSetRemaining(Iterator<V> iterator) {
        while (iterator.hasNext()) {
            this.verticesQueue.add(iterator.next());
        }
    }

    private Iterator<V> addSetsVertices(Iterator<Set<V>> setIterator, int numOfVertices) {
        int i = 0;
        Iterator<V> iterator = null;
        while (setIterator.hasNext() && i < numOfVertices) {
            iterator = setIterator.next().iterator();
            while (iterator.hasNext() && i < numOfVertices) {
                this.verticesQueue.add(iterator.next());
                ++i;
            }
        }
        return iterator;
    }

    private void addSetsRemaining(Iterator<Set<V>> setIterator) {
        while (setIterator.hasNext()) {
            this.verticesQueue.addAll((Collection)setIterator.next());
        }
    }

    private void submitTasks(Runnable task, int numOfTasks) {
        for (int i = 0; i < numOfTasks; ++i) {
            this.completionService.submit(task, null);
        }
    }

    private void waitForTasksCompletion(int numOfTasks) {
        for (int i = 0; i < numOfTasks; ++i) {
            try {
                this.completionService.take();
                continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void relax(V v, E e, double distance) {
        int updatedBucket = this.bucketIndex(distance);
        V v2 = v;
        synchronized (v2) {
            Pair<Double, E> oldData = this.distanceAndPredecessorMap.get(v);
            if (distance < oldData.getFirst()) {
                if (!oldData.getFirst().equals(Double.POSITIVE_INFINITY)) {
                    this.bucketStructure.get(this.bucketIndex(oldData.getFirst())).remove(v);
                }
                this.bucketStructure.get(updatedBucket).add(v);
                this.distanceAndPredecessorMap.put((Pair<Double, E>)v, Pair.of(distance, e));
            }
        }
    }

    private int bucketIndex(double distance) {
        return (int)Math.round(distance / this.delta) % this.numOfBuckets;
    }

    private Set<V> getContentAndReplace(int bucketIndex) {
        Set<V> result = this.bucketStructure.get(bucketIndex);
        this.bucketStructure.set(bucketIndex, this.bucketsSupplier.get());
        return result;
    }

    class LightRelaxTask
    implements Runnable {
        private Queue<V> vertices;

        LightRelaxTask(Queue<V> vertices) {
            this.vertices = vertices;
        }

        /*
         * Unable to fully structure code
         */
        @Override
        public void run() {
            block0: while (true) {
                if ((v = this.vertices.poll()) == null) {
                    if (!DeltaSteppingShortestPath.this.allVerticesAdded || !this.vertices.isEmpty()) continue;
                    break;
                }
                var2_2 = DeltaSteppingShortestPath.this.graph.outgoingEdgesOf(v).iterator();
                while (true) {
                    if (var2_2.hasNext()) ** break;
                    continue block0;
                    e = var2_2.next();
                    if (!(DeltaSteppingShortestPath.this.graph.getEdgeWeight(e) <= DeltaSteppingShortestPath.this.delta)) continue;
                    DeltaSteppingShortestPath.this.relax(Graphs.getOppositeVertex(DeltaSteppingShortestPath.this.graph, e, v), e, DeltaSteppingShortestPath.this.distanceAndPredecessorMap.get(v).getFirst() + DeltaSteppingShortestPath.this.graph.getEdgeWeight(e));
                }
                break;
            }
        }
    }

    class HeavyRelaxTask
    implements Runnable {
        private Queue<V> vertices;

        HeavyRelaxTask(Queue<V> vertices) {
            this.vertices = vertices;
        }

        /*
         * Unable to fully structure code
         */
        @Override
        public void run() {
            block0: while (true) {
                if ((v = this.vertices.poll()) == null) {
                    if (!DeltaSteppingShortestPath.this.allVerticesAdded || !this.vertices.isEmpty()) continue;
                    break;
                }
                var2_2 = DeltaSteppingShortestPath.this.graph.outgoingEdgesOf(v).iterator();
                while (true) {
                    if (var2_2.hasNext()) ** break;
                    continue block0;
                    e = var2_2.next();
                    if (!(DeltaSteppingShortestPath.this.graph.getEdgeWeight(e) > DeltaSteppingShortestPath.this.delta)) continue;
                    DeltaSteppingShortestPath.this.relax(Graphs.getOppositeVertex(DeltaSteppingShortestPath.this.graph, e, v), e, DeltaSteppingShortestPath.this.distanceAndPredecessorMap.get(v).getFirst() + DeltaSteppingShortestPath.this.graph.getEdgeWeight(e));
                }
                break;
            }
        }
    }

    class MaxEdgeWeightTask
    extends RecursiveTask<Double> {
        Spliterator<E> spliterator;
        long loadBalancing;

        MaxEdgeWeightTask(Spliterator<E> spliterator, long loadBalancing) {
            this.spliterator = spliterator;
            this.loadBalancing = loadBalancing;
        }

        @Override
        protected Double compute() {
            if (this.spliterator.estimateSize() <= this.loadBalancing) {
                double[] max = new double[]{0.0};
                this.spliterator.forEachRemaining(e -> {
                    double weight = DeltaSteppingShortestPath.this.graph.getEdgeWeight(e);
                    if (weight < 0.0) {
                        throw new IllegalArgumentException(DeltaSteppingShortestPath.NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED);
                    }
                    max[0] = Math.max(weight, max[0]);
                });
                return max[0];
            }
            MaxEdgeWeightTask t1 = new MaxEdgeWeightTask(this.spliterator.trySplit(), this.loadBalancing);
            t1.fork();
            MaxEdgeWeightTask t2 = new MaxEdgeWeightTask(this.spliterator, this.loadBalancing);
            return Math.max(t2.compute(), (Double)t1.join());
        }
    }
}

