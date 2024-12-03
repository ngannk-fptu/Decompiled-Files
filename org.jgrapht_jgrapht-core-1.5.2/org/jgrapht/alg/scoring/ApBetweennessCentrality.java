/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apfloat.Apfloat
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
import org.apfloat.Apfloat;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class ApBetweennessCentrality<V, E>
implements VertexScoringAlgorithm<V, Apfloat> {
    private final Graph<V, E> graph;
    private final boolean normalize;
    private Map<V, Apfloat> scores;
    private final long precision;

    public ApBetweennessCentrality(Graph<V, E> graph) {
        this(graph, false);
    }

    public ApBetweennessCentrality(Graph<V, E> graph, boolean normalize) {
        this(graph, normalize, Long.MAX_VALUE);
    }

    public ApBetweennessCentrality(Graph<V, E> graph, boolean normalize, long precision) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.scores = null;
        this.normalize = normalize;
        this.precision = precision;
    }

    @Override
    public Map<V, Apfloat> getScores() {
        if (this.scores == null) {
            this.compute();
        }
        return Collections.unmodifiableMap(this.scores);
    }

    @Override
    public Apfloat getVertexScore(V v) {
        if (!this.graph.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        if (this.scores == null) {
            this.compute();
        }
        return this.scores.get(v);
    }

    private void compute() {
        long n;
        this.scores = new HashMap<V, Apfloat>();
        this.graph.iterables().vertices().forEach(v -> this.scores.put((Apfloat)v, new Apfloat(0L, this.precision)));
        this.graph.iterables().vertices().forEach(this::compute);
        Apfloat two = new Apfloat(2L, this.precision);
        if (!this.graph.getType().isDirected()) {
            this.scores.forEach((v, score) -> this.scores.put((Apfloat)v, score.divide(two)));
        }
        Apfloat one = new Apfloat(1L, this.precision);
        if (this.normalize && (n = this.graph.iterables().vertexCount()) > 2L) {
            Apfloat apn = new Apfloat(n, this.precision);
            Apfloat nf = apn.subtract(one).multiply(apn.subtract(two));
            this.scores.forEach((v, score) -> this.scores.put((Apfloat)v, score.divide(nf)));
        }
    }

    private void compute(V s) {
        ArrayDeque stack = new ArrayDeque();
        HashMap predecessors = new HashMap();
        this.graph.iterables().vertices().forEach(w -> predecessors.put(w, new ArrayList()));
        HashMap sigma = new HashMap();
        Apfloat zero = new Apfloat(0L, this.precision);
        Apfloat one = new Apfloat(1L, this.precision);
        this.graph.iterables().vertices().forEach(t -> sigma.put(t, zero));
        sigma.put(s, one);
        HashMap distance = new HashMap();
        this.graph.iterables().vertices().forEach(t -> distance.put(t, Double.POSITIVE_INFINITY));
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
                    sigma.put(w2, (Apfloat)sigma.get(v2));
                    ((List)predecessors.get(w2)).add(v2);
                    continue;
                }
                if ((Double)distance.get(w2) == d) {
                    sigma.put(w2, ((Apfloat)sigma.get(w2)).add((Apfloat)sigma.get(v2)));
                    ((List)predecessors.get(w2)).add(v2);
                    continue;
                }
                if (!((Double)distance.get(w2) > d)) continue;
                queue.update(w2, d);
                distance.put(w2, d);
                sigma.put(w2, (Apfloat)sigma.get(v2));
                ((List)predecessors.get(w2)).clear();
                ((List)predecessors.get(w2)).add(v2);
            }
        }
        HashMap dependency = new HashMap();
        this.graph.iterables().vertices().forEach(v -> dependency.put(v, zero));
        while (!stack.isEmpty()) {
            Object w3 = stack.pop();
            for (Object v3 : (List)predecessors.get(w3)) {
                dependency.put(v3, ((Apfloat)dependency.get(v3)).add(((Apfloat)sigma.get(v3)).divide((Apfloat)sigma.get(w3)).multiply(((Apfloat)dependency.get(w3)).add(one))));
            }
            if (w3.equals(s)) continue;
            this.scores.put((Apfloat)w3, this.scores.get(w3).add((Apfloat)dependency.get(w3)));
        }
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

