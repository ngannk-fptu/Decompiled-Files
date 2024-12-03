/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.traverse.NotDirectedAcyclicGraphException;
import org.jgrapht.util.ModifiableInteger;

public class TopologicalOrderIterator<V, E>
extends AbstractGraphIterator<V, E> {
    private Queue<V> queue;
    private Map<V, ModifiableInteger> inDegreeMap;
    private int remainingVertices;
    private V cur;

    public TopologicalOrderIterator(Graph<V, E> graph) {
        this(graph, null);
    }

    public TopologicalOrderIterator(Graph<V, E> graph, Comparator<V> comparator) {
        super(graph);
        GraphTests.requireDirected(graph);
        this.queue = comparator == null ? new ArrayDeque<V>() : new PriorityQueue<V>(comparator);
        this.inDegreeMap = new HashMap<V, ModifiableInteger>();
        for (V v : graph.vertexSet()) {
            int d = 0;
            for (E e : graph.incomingEdgesOf(v)) {
                V u = Graphs.getOppositeVertex(graph, e, v);
                if (v.equals(u)) {
                    throw new NotDirectedAcyclicGraphException();
                }
                ++d;
            }
            this.inDegreeMap.put((ModifiableInteger)v, new ModifiableInteger(d));
            if (d != 0) continue;
            this.queue.offer(v);
        }
        this.remainingVertices = graph.vertexSet().size();
    }

    @Override
    public boolean isCrossComponentTraversal() {
        return true;
    }

    @Override
    public void setCrossComponentTraversal(boolean crossComponentTraversal) {
        if (!crossComponentTraversal) {
            throw new IllegalArgumentException("Iterator is always cross-component");
        }
    }

    @Override
    public boolean hasNext() {
        if (this.cur != null) {
            return true;
        }
        this.cur = this.advance();
        if (this.cur != null && this.nListeners != 0) {
            this.fireVertexTraversed(this.createVertexTraversalEvent(this.cur));
        }
        return this.cur != null;
    }

    @Override
    public V next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        V result = this.cur;
        this.cur = null;
        if (this.nListeners != 0) {
            this.fireVertexFinished(this.createVertexTraversalEvent(result));
        }
        return result;
    }

    private V advance() {
        V result = this.queue.poll();
        if (result != null) {
            for (Object e : this.graph.outgoingEdgesOf(result)) {
                V other = Graphs.getOppositeVertex(this.graph, e, result);
                ModifiableInteger inDegree = this.inDegreeMap.get(other);
                if (inDegree.value <= 0) continue;
                --inDegree.value;
                if (inDegree.value != 0) continue;
                this.queue.offer(other);
            }
            --this.remainingVertices;
        } else if (this.remainingVertices > 0) {
            throw new NotDirectedAcyclicGraphException();
        }
        return result;
    }
}

