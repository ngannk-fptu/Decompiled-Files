/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.scoring;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.EdgeScoringAlgorithm;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class EdgeBetweennessCentrality<V, E>
implements EdgeScoringAlgorithm<E, Double> {
    private final Graph<V, E> graph;
    private final Iterable<V> startVertices;
    private final boolean divideByTwo;
    private Map<E, Double> scores;
    private final OverflowStrategy overflowStrategy;

    public EdgeBetweennessCentrality(Graph<V, E> graph) {
        this(graph, OverflowStrategy.IGNORE_OVERFLOW, null);
    }

    public EdgeBetweennessCentrality(Graph<V, E> graph, OverflowStrategy overflowStrategy) {
        this(graph, overflowStrategy, null);
    }

    public EdgeBetweennessCentrality(Graph<V, E> graph, OverflowStrategy overflowStrategy, Iterable<V> startVertices) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (GraphTests.hasMultipleEdges(graph)) {
            throw new IllegalArgumentException("Graphs with multiple edges not supported");
        }
        this.scores = null;
        this.overflowStrategy = overflowStrategy;
        if (startVertices == null) {
            this.startVertices = graph.vertexSet();
            this.divideByTwo = graph.getType().isUndirected();
        } else {
            this.startVertices = startVertices;
            this.divideByTwo = false;
        }
    }

    @Override
    public Map<E, Double> getScores() {
        if (this.scores == null) {
            this.scores = this.graph.getType().isWeighted() ? new WeightedAlgorithm().getScores() : new Algorithm().getScores();
        }
        return Collections.unmodifiableMap(this.scores);
    }

    @Override
    public Double getEdgeScore(E e) {
        if (!this.graph.containsEdge(e)) {
            throw new IllegalArgumentException("Cannot return score of unknown edge");
        }
        if (this.scores == null) {
            this.scores = this.graph.getType().isWeighted() ? new WeightedAlgorithm().getScores() : new Algorithm().getScores();
        }
        return this.scores.get(e);
    }

    public static enum OverflowStrategy {
        IGNORE_OVERFLOW,
        THROW_EXCEPTION_ON_OVERFLOW;

    }

    private class WeightedAlgorithm
    extends Algorithm {
        private WeightedAlgorithm() {
        }

        @Override
        protected void singleVertexUpdate(V source) {
            HashMap<Object, List> pred = new HashMap<Object, List>();
            HashMap dist = new HashMap();
            HashMap sigma = new HashMap();
            PairingHeap heap = new PairingHeap();
            for (Object v : EdgeBetweennessCentrality.this.graph.vertexSet()) {
                sigma.put(v, 0L);
            }
            sigma.put(source, 1L);
            dist.put(source, heap.insert((Object)0.0, source));
            while (!heap.isEmpty()) {
                Object v;
                AddressableHeap.Handle vHandle = heap.deleteMin();
                v = vHandle.getValue();
                double vDistance = (Double)vHandle.getKey();
                this.stack.push(v);
                for (Object e : EdgeBetweennessCentrality.this.graph.outgoingEdgesOf(v)) {
                    Object w = Graphs.getOppositeVertex(EdgeBetweennessCentrality.this.graph, e, v);
                    if (w.equals(v)) continue;
                    double eWeight = EdgeBetweennessCentrality.this.graph.getEdgeWeight(e);
                    if (eWeight < 0.0) {
                        throw new IllegalArgumentException("Negative edge weights are not allowed");
                    }
                    double newDistance = vDistance + eWeight;
                    AddressableHeap.Handle wHandle = (AddressableHeap.Handle)dist.get(w);
                    if (wHandle == null) {
                        wHandle = heap.insert((Object)newDistance, w);
                        dist.put(w, wHandle);
                        sigma.put(w, 0L);
                        pred.put(w, new ArrayList());
                    } else if (Double.compare((Double)wHandle.getKey(), newDistance) > 0) {
                        wHandle.decreaseKey((Object)newDistance);
                        sigma.put(w, 0L);
                        pred.put(w, new ArrayList());
                    }
                    if (Double.compare((Double)wHandle.getKey(), newDistance) != 0) continue;
                    long wCounter = (Long)sigma.get(w);
                    long vCounter = (Long)sigma.get(v);
                    long sum = wCounter + vCounter;
                    if (EdgeBetweennessCentrality.this.overflowStrategy.equals((Object)OverflowStrategy.THROW_EXCEPTION_ON_OVERFLOW) && sum < 0L) {
                        throw new ArithmeticException("long overflow");
                    }
                    sigma.put(w, sum);
                    pred.computeIfAbsent(w, k -> new ArrayList()).add(e);
                }
            }
            this.accumulate(pred, sigma);
        }
    }

    private class Algorithm {
        protected Map<E, Double> scores = new HashMap();
        protected Deque<V> stack = new ArrayDeque();

        private Algorithm() {
        }

        public Map<E, Double> getScores() {
            for (Object e2 : EdgeBetweennessCentrality.this.graph.iterables().edges()) {
                this.scores.put(e2, 0.0);
            }
            for (Object v : EdgeBetweennessCentrality.this.startVertices) {
                this.singleVertexUpdate(v);
            }
            if (EdgeBetweennessCentrality.this.divideByTwo) {
                this.scores.forEach((e, score) -> this.scores.put(e, score / 2.0));
            }
            return this.scores;
        }

        protected void singleVertexUpdate(V source) {
            HashMap<Object, List> pred = new HashMap<Object, List>();
            HashMap dist = new HashMap();
            HashMap sigma = new HashMap();
            ArrayDeque queue = new ArrayDeque();
            for (Object v : EdgeBetweennessCentrality.this.graph.vertexSet()) {
                sigma.put(v, 0L);
            }
            sigma.put(source, 1L);
            dist.put(source, 0.0);
            queue.add(source);
            while (!queue.isEmpty()) {
                Object v = queue.remove();
                this.stack.push(v);
                double vDistance = (Double)dist.get(v);
                for (Object e : EdgeBetweennessCentrality.this.graph.outgoingEdgesOf(v)) {
                    double wDistance;
                    Object w = Graphs.getOppositeVertex(EdgeBetweennessCentrality.this.graph, e, v);
                    if (w.equals(v)) continue;
                    if (!dist.containsKey(w)) {
                        dist.put(w, vDistance + 1.0);
                        queue.add(w);
                    }
                    if (Double.compare(wDistance = ((Double)dist.get(w)).doubleValue(), vDistance + 1.0) != 0) continue;
                    long wCounter = (Long)sigma.get(w);
                    long vCounter = (Long)sigma.get(v);
                    long sum = wCounter + vCounter;
                    if (EdgeBetweennessCentrality.this.overflowStrategy.equals((Object)OverflowStrategy.THROW_EXCEPTION_ON_OVERFLOW) && sum < 0L) {
                        throw new ArithmeticException("long overflow");
                    }
                    sigma.put(w, sum);
                    pred.computeIfAbsent(w, k -> new ArrayList()).add(e);
                }
            }
            this.accumulate(pred, sigma);
        }

        protected void accumulate(Map<V, List<E>> pred, Map<V, Long> sigma) {
            HashMap delta = new HashMap();
            for (Object v : EdgeBetweennessCentrality.this.graph.iterables().vertices()) {
                delta.put(v, 0.0);
            }
            while (!this.stack.isEmpty()) {
                Object w = this.stack.pop();
                List wPred = pred.get(w);
                if (wPred == null) continue;
                for (Object e : wPred) {
                    Object v = Graphs.getOppositeVertex(EdgeBetweennessCentrality.this.graph, e, w);
                    double c = sigma.get(v).doubleValue() / sigma.get(w).doubleValue() * (1.0 + (Double)delta.get(w));
                    this.scores.put(e, this.scores.get(e) + c);
                    delta.put(v, (Double)delta.get(v) + c);
                }
            }
        }
    }
}

