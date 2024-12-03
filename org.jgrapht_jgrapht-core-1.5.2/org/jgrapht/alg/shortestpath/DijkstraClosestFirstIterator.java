/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

class DijkstraClosestFirstIterator<V, E>
implements Iterator<V> {
    private final Graph<V, E> graph;
    private final V source;
    private final double radius;
    private final Map<V, AddressableHeap.Handle<Double, Pair<V, E>>> seen;
    private AddressableHeap<Double, Pair<V, E>> heap;

    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source) {
        this(graph, source, Double.POSITIVE_INFINITY, PairingHeap::new);
    }

    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source, double radius) {
        this(graph, source, radius, PairingHeap::new);
    }

    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
        this(graph, source, Double.POSITIVE_INFINITY, heapSupplier);
    }

    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source, double radius, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.source = Objects.requireNonNull(source, "Source vertex cannot be null");
        Objects.requireNonNull(heapSupplier, "Heap supplier cannot be null");
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }
        this.radius = radius;
        this.seen = new HashMap<V, AddressableHeap.Handle<Double, Pair<V, E>>>();
        this.heap = heapSupplier.get();
        this.updateDistance(source, null, 0.0);
    }

    @Override
    public boolean hasNext() {
        if (this.heap.isEmpty()) {
            return false;
        }
        AddressableHeap.Handle vNode = this.heap.findMin();
        double vDistance = (Double)vNode.getKey();
        if (this.radius < vDistance) {
            this.heap.clear();
            return false;
        }
        return true;
    }

    @Override
    public V next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        AddressableHeap.Handle vNode = this.heap.deleteMin();
        Object v = ((Pair)vNode.getValue()).getFirst();
        double vDistance = (Double)vNode.getKey();
        for (E e : this.graph.outgoingEdgesOf(v)) {
            V u = Graphs.getOppositeVertex(this.graph, e, v);
            double eWeight = this.graph.getEdgeWeight(e);
            if (eWeight < 0.0) {
                throw new IllegalArgumentException("Negative edge weight not allowed");
            }
            this.updateDistance(u, e, vDistance + eWeight);
        }
        return (V)v;
    }

    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths() {
        return new TreeSingleSourcePathsImpl<V, E>(this.graph, this.source, this.getDistanceAndPredecessorMap());
    }

    public Map<V, Pair<Double, E>> getDistanceAndPredecessorMap() {
        HashMap distanceAndPredecessorMap = new HashMap();
        for (AddressableHeap.Handle<Double, Pair<V, E>> vNode : this.seen.values()) {
            double vDistance = (Double)vNode.getKey();
            if (this.radius < vDistance) continue;
            Object v = ((Pair)vNode.getValue()).getFirst();
            distanceAndPredecessorMap.put(v, Pair.of(vDistance, ((Pair)vNode.getValue()).getSecond()));
        }
        return distanceAndPredecessorMap;
    }

    private void updateDistance(V v, E e, double distance) {
        AddressableHeap.Handle node = this.seen.get(v);
        if (node == null) {
            node = this.heap.insert((Object)distance, Pair.of(v, e));
            this.seen.put((AddressableHeap.Handle)v, (AddressableHeap.Handle<Double, Pair<AddressableHeap.Handle, E>>)node);
        } else if (distance < (Double)node.getKey()) {
            node.decreaseKey((Object)distance);
            node.setValue(Pair.of(((Pair)node.getValue()).getFirst(), e));
        }
    }
}

