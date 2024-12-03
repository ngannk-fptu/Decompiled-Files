/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.scoring;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class BetweennessCentrality<V, E>
implements VertexScoringAlgorithm<V, Double> {
    private final Graph<V, E> graph;
    private final boolean normalize;
    private Map<V, Double> scores;
    private OverflowStrategy overflowStrategy;

    public BetweennessCentrality(Graph<V, E> graph) {
        this(graph, false);
    }

    public BetweennessCentrality(Graph<V, E> graph, boolean normalize) {
        this(graph, normalize, OverflowStrategy.IGNORE_OVERFLOW);
    }

    public BetweennessCentrality(Graph<V, E> graph, boolean normalize, OverflowStrategy overflowStrategy) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.scores = null;
        this.normalize = normalize;
        this.overflowStrategy = overflowStrategy;
    }

    @Override
    public Map<V, Double> getScores() {
        if (this.scores == null) {
            this.compute();
        }
        return Collections.unmodifiableMap(this.scores);
    }

    @Override
    public Double getVertexScore(V v) {
        if (!this.graph.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        if (this.scores == null) {
            this.compute();
        }
        return this.scores.get(v);
    }

    private void compute() {
        int n;
        int normalizationFactor;
        this.scores = new HashMap<V, Double>();
        this.graph.vertexSet().forEach(v -> this.scores.put((Double)v, 0.0));
        this.graph.vertexSet().forEach(this::compute);
        if (!this.graph.getType().isDirected()) {
            this.scores.forEach((v, score) -> this.scores.put((Double)v, score / 2.0));
        }
        if (this.normalize && (normalizationFactor = ((n = this.graph.vertexSet().size()) - 1) * (n - 2)) != 0) {
            this.scores.forEach((v, score) -> this.scores.put((Double)v, score / (double)normalizationFactor));
        }
    }

    private void compute(V s) {
        ArrayDeque stack = new ArrayDeque();
        HashMap predecessors = new HashMap();
        this.graph.vertexSet().forEach(w -> predecessors.put(w, new ArrayList()));
        HashMap sigma = new HashMap();
        this.graph.vertexSet().forEach(t -> sigma.put(t, 0L));
        sigma.put(s, 1L);
        HashMap distance = new HashMap();
        this.graph.vertexSet().forEach(t -> distance.put(t, Double.POSITIVE_INFINITY));
        distance.put(s, 0.0);
        MyQueue queue = this.graph.getType().isWeighted() ? new WeightedQueue() : new UnweightedQueue();
        queue.insert(s, 0.0);
        while (!queue.isEmpty()) {
            Object v2 = queue.remove();
            stack.push(v2);
            for (E e : this.graph.outgoingEdgesOf(v2)) {
                V w2 = Graphs.getOppositeVertex(this.graph, e, v2);
                double eWeight = this.graph.getEdgeWeight(e);
                if (eWeight < 0.0) {
                    throw new IllegalArgumentException("Negative edge weight not allowed");
                }
                double d = (Double)distance.get(v2) + eWeight;
                if ((Double)distance.get(w2) == Double.POSITIVE_INFINITY) {
                    queue.insert(w2, d);
                    distance.put(w2, d);
                    sigma.put(w2, (Long)sigma.get(v2));
                    ((List)predecessors.get(w2)).add(v2);
                    continue;
                }
                if ((Double)distance.get(w2) == d) {
                    long wCounter = (Long)sigma.get(w2);
                    long vCounter = (Long)sigma.get(v2);
                    long sum = wCounter + vCounter;
                    if (this.overflowStrategy.equals((Object)OverflowStrategy.THROW_EXCEPTION_ON_OVERFLOW) && sum < 0L) {
                        throw new ArithmeticException("long overflow");
                    }
                    sigma.put(w2, sum);
                    ((List)predecessors.get(w2)).add(v2);
                    continue;
                }
                if (!((Double)distance.get(w2) > d)) continue;
                queue.update(w2, d);
                distance.put(w2, d);
                sigma.put(w2, (Long)sigma.get(v2));
                ((List)predecessors.get(w2)).clear();
                ((List)predecessors.get(w2)).add(v2);
            }
        }
        HashMap dependency = new HashMap();
        this.graph.vertexSet().forEach(v -> dependency.put(v, 0.0));
        while (!stack.isEmpty()) {
            Object w3 = stack.pop();
            for (Object v3 : (List)predecessors.get(w3)) {
                dependency.put(v3, (Double)dependency.get(v3) + ((Long)sigma.get(v3)).doubleValue() / ((Long)sigma.get(w3)).doubleValue() * (1.0 + (Double)dependency.get(w3)));
            }
            if (w3.equals(s)) continue;
            this.scores.put((Double)w3, this.scores.get(w3) + (Double)dependency.get(w3));
        }
    }

    public static enum OverflowStrategy {
        IGNORE_OVERFLOW,
        THROW_EXCEPTION_ON_OVERFLOW;

    }

    private class WeightedQueue
    implements MyQueue<V, Double> {
        AddressableHeap<Double, V> delegate = new PairingHeap();
        Map<V, AddressableHeap.Handle<Double, V>> seen = new HashMap();

        private WeightedQueue() {
        }

        @Override
        public void insert(V t, Double d) {
            AddressableHeap.Handle node = this.delegate.insert((Object)d, t);
            this.seen.put((AddressableHeap.Handle)t, (AddressableHeap.Handle<Double, AddressableHeap.Handle>)node);
        }

        @Override
        public void update(V t, Double d) {
            if (!this.seen.containsKey(t)) {
                throw new IllegalArgumentException("Element " + t + " does not exist in queue");
            }
            this.seen.get(t).decreaseKey((Object)d);
        }

        @Override
        public V remove() {
            return this.delegate.deleteMin().getValue();
        }

        @Override
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }
    }

    private class UnweightedQueue
    implements MyQueue<V, Double> {
        Queue<V> delegate = new ArrayDeque();

        private UnweightedQueue() {
        }

        @Override
        public void insert(V t, Double d) {
            this.delegate.add(t);
        }

        @Override
        public void update(V t, Double d) {
        }

        @Override
        public V remove() {
            return this.delegate.remove();
        }

        @Override
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }
    }

    private static interface MyQueue<T, D> {
        public void insert(T var1, D var2);

        public void update(T var1, D var2);

        public T remove();

        public boolean isEmpty();
    }
}

