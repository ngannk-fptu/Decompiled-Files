/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.traverse;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.CrossComponentIterator;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class ClosestFirstIterator<V, E>
extends CrossComponentIterator<V, E, AddressableHeap.Handle<Double, QueueEntry<V, E>>> {
    private AddressableHeap<Double, QueueEntry<V, E>> heap;
    private double radius = Double.POSITIVE_INFINITY;
    private boolean initialized = false;

    public ClosestFirstIterator(Graph<V, E> g, V startVertex) {
        this(g, startVertex, Double.POSITIVE_INFINITY);
    }

    public ClosestFirstIterator(Graph<V, E> g, Iterable<V> startVertices) {
        this(g, startVertices, Double.POSITIVE_INFINITY);
    }

    public ClosestFirstIterator(Graph<V, E> g, V startVertex, double radius) {
        this(g, (Iterable<V>)(startVertex == null ? null : Collections.singletonList(startVertex)), radius, PairingHeap::new);
    }

    public ClosestFirstIterator(Graph<V, E> g, V startVertex, double radius, Supplier<AddressableHeap<Double, QueueEntry<V, E>>> heapSupplier) {
        this(g, (Iterable<V>)(startVertex == null ? null : Collections.singletonList(startVertex)), radius, heapSupplier);
    }

    public ClosestFirstIterator(Graph<V, E> g, Iterable<V> startVertices, double radius) {
        this(g, startVertices, radius, PairingHeap::new);
    }

    public ClosestFirstIterator(Graph<V, E> g, Iterable<V> startVertices, double radius, Supplier<AddressableHeap<Double, QueueEntry<V, E>>> heapSupplier) {
        super(g, startVertices);
        this.radius = radius;
        Objects.requireNonNull(heapSupplier, "Heap supplier cannot be null");
        this.heap = heapSupplier.get();
        this.checkRadiusTraversal(this.isCrossComponentTraversal());
        this.initialized = true;
        if (!this.crossComponentTraversal) {
            this.hasNext();
            Iterator<V> iter = startVertices.iterator();
            if (iter.hasNext()) {
                iter.next();
                while (iter.hasNext()) {
                    V v = iter.next();
                    this.encounterVertex(v, null);
                }
            }
        }
    }

    @Override
    public void setCrossComponentTraversal(boolean crossComponentTraversal) {
        if (this.initialized) {
            this.checkRadiusTraversal(crossComponentTraversal);
        }
        super.setCrossComponentTraversal(crossComponentTraversal);
    }

    public double getShortestPathLength(V vertex) {
        AddressableHeap.Handle node = (AddressableHeap.Handle)this.getSeenData(vertex);
        if (node == null) {
            return Double.POSITIVE_INFINITY;
        }
        return (Double)node.getKey();
    }

    public E getSpanningTreeEdge(V vertex) {
        AddressableHeap.Handle node = (AddressableHeap.Handle)this.getSeenData(vertex);
        if (node == null) {
            return null;
        }
        return ((QueueEntry)node.getValue()).spanningTreeEdge;
    }

    @Override
    protected boolean isConnectedComponentExhausted() {
        if (this.heap.size() == 0L) {
            return true;
        }
        if ((Double)this.heap.findMin().getKey() > this.radius) {
            this.heap.clear();
            return true;
        }
        return false;
    }

    @Override
    protected void encounterVertex(V vertex, E edge) {
        double shortestPathLength = edge == null ? 0.0 : this.calculatePathLength(vertex, edge);
        AddressableHeap.Handle handle = this.heap.insert((Object)shortestPathLength, new QueueEntry<V, E>(vertex, edge));
        this.putSeenData(vertex, handle);
    }

    @Override
    protected void encounterVertexAgain(V vertex, E edge) {
        AddressableHeap.Handle node = (AddressableHeap.Handle)this.getSeenData(vertex);
        if (((QueueEntry)node.getValue()).frozen) {
            return;
        }
        double candidatePathLength = this.calculatePathLength(vertex, edge);
        if (candidatePathLength < (Double)node.getKey()) {
            ((QueueEntry)node.getValue()).spanningTreeEdge = edge;
            node.decreaseKey((Object)candidatePathLength);
        }
    }

    @Override
    protected V provideNextVertex() {
        AddressableHeap.Handle node = this.heap.deleteMin();
        ((QueueEntry)node.getValue()).frozen = true;
        return ((QueueEntry)node.getValue()).vertex;
    }

    private void assertNonNegativeEdge(E edge) {
        if (this.getGraph().getEdgeWeight(edge) < 0.0) {
            throw new IllegalArgumentException("negative edge weights not allowed");
        }
    }

    private double calculatePathLength(V vertex, E edge) {
        this.assertNonNegativeEdge(edge);
        Object otherVertex = Graphs.getOppositeVertex(this.getGraph(), edge, vertex);
        AddressableHeap.Handle otherEntry = (AddressableHeap.Handle)this.getSeenData(otherVertex);
        return (Double)otherEntry.getKey() + this.getGraph().getEdgeWeight(edge);
    }

    private void checkRadiusTraversal(boolean crossComponentTraversal) {
        if (crossComponentTraversal && this.radius != Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("radius may not be specified for cross-component traversal");
        }
    }

    static class QueueEntry<V, E> {
        V vertex;
        E spanningTreeEdge;
        boolean frozen;

        QueueEntry(V vertex, E spanningTreeEdge) {
            this.vertex = vertex;
            this.spanningTreeEdge = spanningTreeEdge;
        }
    }
}

