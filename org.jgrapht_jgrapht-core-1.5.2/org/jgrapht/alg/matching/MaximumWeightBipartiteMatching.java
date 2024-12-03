/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.FibonacciHeap
 */
package org.jgrapht.alg.matching;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.FibonacciHeap;

public class MaximumWeightBipartiteMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;
    private final Comparator<BigDecimal> comparator;
    private final Function<Comparator<BigDecimal>, AddressableHeap<BigDecimal, V>> heapSupplier;
    private Map<V, BigDecimal> pot;
    private Map<V, E> matchedEdge;
    private AddressableHeap<BigDecimal, V> heap;
    private Map<V, AddressableHeap.Handle<BigDecimal, V>> nodeInHeap;
    private Map<V, E> pred;
    private Map<V, BigDecimal> dist;
    private Set<E> matching;
    private BigDecimal matchingWeight;

    public MaximumWeightBipartiteMatching(Graph<V, E> graph, Set<V> partition1, Set<V> partition2) {
        this(graph, partition1, partition2, comparator -> new FibonacciHeap(comparator));
    }

    public MaximumWeightBipartiteMatching(Graph<V, E> graph, Set<V> partition1, Set<V> partition2, Function<Comparator<BigDecimal>, AddressableHeap<BigDecimal, V>> heapSupplier) {
        this.graph = GraphTests.requireUndirected(graph);
        this.partition1 = Objects.requireNonNull(partition1, "Partition 1 cannot be null");
        this.partition2 = Objects.requireNonNull(partition2, "Partition 2 cannot be null");
        this.comparator = Comparator.naturalOrder();
        this.heapSupplier = Objects.requireNonNull(heapSupplier, "Heap supplier cannot be null");
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        if (!GraphTests.isSimple(this.graph)) {
            throw new IllegalArgumentException("Only simple graphs supported");
        }
        if (!GraphTests.isBipartitePartition(this.graph, this.partition1, this.partition2)) {
            throw new IllegalArgumentException("Graph partition is not bipartite");
        }
        this.matching = new LinkedHashSet();
        this.matchingWeight = BigDecimal.ZERO;
        if (this.graph.edgeSet().isEmpty()) {
            return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, this.matching, this.matchingWeight.doubleValue());
        }
        this.pot = new HashMap<V, BigDecimal>();
        this.dist = new HashMap<V, BigDecimal>();
        this.matchedEdge = new HashMap<V, E>();
        this.heap = this.heapSupplier.apply(this.comparator);
        this.nodeInHeap = new HashMap<V, AddressableHeap.Handle<BigDecimal, V>>();
        this.pred = new HashMap<V, E>();
        this.graph.vertexSet().forEach(v -> {
            this.pot.put((BigDecimal)v, BigDecimal.ZERO);
            this.pred.put(v, null);
            this.dist.put((BigDecimal)v, BigDecimal.ZERO);
        });
        this.simpleHeuristic();
        for (V v2 : this.partition1) {
            if (this.matchedEdge.containsKey(v2)) continue;
            this.augment(v2);
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, this.matching, this.matchingWeight.doubleValue());
    }

    public Map<V, BigDecimal> getPotentials() {
        if (this.pot == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.pot);
    }

    public BigDecimal getMatchingWeight() {
        return this.matchingWeight;
    }

    private void augment(V a) {
        BigDecimal potChange;
        Object v;
        BigDecimal delta;
        this.dist.put((BigDecimal)a, BigDecimal.ZERO);
        V bestInA = a;
        BigDecimal minA = this.pot.get(a);
        ArrayDeque<V> reachedA = new ArrayDeque<V>();
        reachedA.push(a);
        ArrayDeque<V> reachedB = new ArrayDeque<V>();
        Object a1 = a;
        for (E e1 : this.graph.edgesOf(a1)) {
            if (this.matching.contains(e1)) continue;
            V b1 = Graphs.getOppositeVertex(this.graph, e1, a1);
            BigDecimal db1 = this.dist.get(a1).add(this.pot.get(a1)).add(this.pot.get(b1)).subtract(BigDecimal.valueOf(this.graph.getEdgeWeight(e1)));
            if (this.pred.get(b1) == null) {
                this.dist.put((BigDecimal)b1, db1);
                this.pred.put(b1, e1);
                reachedB.push(b1);
                AddressableHeap.Handle node = this.heap.insert((Object)db1, b1);
                this.nodeInHeap.put((AddressableHeap.Handle)b1, (AddressableHeap.Handle<BigDecimal, AddressableHeap.Handle>)node);
                continue;
            }
            if (this.comparator.compare(db1, this.dist.get(b1)) >= 0) continue;
            this.dist.put((BigDecimal)b1, db1);
            this.pred.put(b1, e1);
            this.nodeInHeap.get(b1).decreaseKey((Object)db1);
        }
        block1: while (true) {
            Object b = null;
            BigDecimal db = BigDecimal.ZERO;
            if (!this.heap.isEmpty()) {
                b = this.heap.deleteMin().getValue();
                this.nodeInHeap.remove(b);
                db = this.dist.get(b);
            }
            if (b == null || this.comparator.compare(db, minA) >= 0) {
                delta = minA;
                this.augmentPathTo(bestInA);
                break;
            }
            E e = this.matchedEdge.get(b);
            if (e == null) {
                delta = db;
                this.augmentPathTo(b);
                break;
            }
            a1 = Graphs.getOppositeVertex(this.graph, e, b);
            this.pred.put(a1, e);
            reachedA.push(a1);
            this.dist.put((BigDecimal)a1, db);
            if (this.comparator.compare(db.add(this.pot.get(a1)), minA) < 0) {
                bestInA = a1;
                minA = db.add(this.pot.get(a1));
            }
            Iterator<E> iterator = this.graph.edgesOf(a1).iterator();
            while (true) {
                if (!iterator.hasNext()) continue block1;
                E e1 = iterator.next();
                if (this.matching.contains(e1)) continue;
                V b1 = Graphs.getOppositeVertex(this.graph, e1, a1);
                BigDecimal db1 = this.dist.get(a1).add(this.pot.get(a1)).add(this.pot.get(b1)).subtract(BigDecimal.valueOf(this.graph.getEdgeWeight(e1)));
                if (this.pred.get(b1) == null) {
                    this.dist.put((BigDecimal)b1, db1);
                    this.pred.put(b1, e1);
                    reachedB.push(b1);
                    AddressableHeap.Handle node = this.heap.insert((Object)db1, b1);
                    this.nodeInHeap.put((AddressableHeap.Handle)b1, (AddressableHeap.Handle<BigDecimal, AddressableHeap.Handle>)node);
                    continue;
                }
                if (this.comparator.compare(db1, this.dist.get(b1)) >= 0) continue;
                this.dist.put((BigDecimal)b1, db1);
                this.pred.put(b1, e1);
                this.nodeInHeap.get(b1).decreaseKey((Object)db1);
            }
            break;
        }
        while (!reachedA.isEmpty()) {
            v = reachedA.pop();
            this.pred.put(v, null);
            potChange = delta.subtract(this.dist.get(v));
            if (this.comparator.compare(potChange, BigDecimal.ZERO) <= 0) continue;
            this.pot.put((BigDecimal)v, this.pot.get(v).subtract(potChange));
        }
        while (!reachedB.isEmpty()) {
            v = reachedB.pop();
            this.pred.put(v, null);
            if (this.nodeInHeap.containsKey(v)) {
                this.nodeInHeap.remove(v).delete();
            }
            if (this.comparator.compare(potChange = delta.subtract(this.dist.get(v)), BigDecimal.ZERO) <= 0) continue;
            this.pot.put((BigDecimal)v, this.pot.get(v).add(potChange));
        }
    }

    private void augmentPathTo(V v) {
        V t;
        V s;
        BigDecimal w;
        ArrayList<E> matched = new ArrayList<E>();
        ArrayList<E> free = new ArrayList<E>();
        E e1 = this.pred.get(v);
        while (e1 != null) {
            if (this.matching.contains(e1)) {
                matched.add(e1);
            } else {
                free.add(e1);
            }
            v = Graphs.getOppositeVertex(this.graph, e1, v);
            e1 = this.pred.get(v);
        }
        for (Object e : matched) {
            w = BigDecimal.valueOf(this.graph.getEdgeWeight(e));
            s = this.graph.getEdgeSource(e);
            t = this.graph.getEdgeTarget(e);
            this.matchedEdge.remove(s);
            this.matchedEdge.remove(t);
            this.matchingWeight = this.matchingWeight.subtract(w);
            this.matching.remove(e);
        }
        for (Object e : free) {
            w = BigDecimal.valueOf(this.graph.getEdgeWeight(e));
            s = this.graph.getEdgeSource(e);
            t = this.graph.getEdgeTarget(e);
            this.matchedEdge.put(s, e);
            this.matchedEdge.put(t, e);
            this.matchingWeight = this.matchingWeight.add(w);
            this.matching.add(e);
        }
    }

    private void simpleHeuristic() {
        for (V v : this.partition1) {
            V u;
            Object maxEdge = null;
            BigDecimal maxWeight = BigDecimal.ZERO;
            for (E e : this.graph.edgesOf(v)) {
                BigDecimal w = BigDecimal.valueOf(this.graph.getEdgeWeight(e));
                if (this.comparator.compare(w, maxWeight) <= 0) continue;
                maxWeight = w;
                maxEdge = e;
            }
            this.pot.put((BigDecimal)v, maxWeight);
            if (maxEdge == null || this.matchedEdge.containsKey(u = Graphs.getOppositeVertex(this.graph, maxEdge, v))) continue;
            this.matching.add(maxEdge);
            this.matchingWeight = this.matchingWeight.add(maxWeight);
            this.matchedEdge.put(v, maxEdge);
            this.matchedEdge.put(u, maxEdge);
        }
    }
}

