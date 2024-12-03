/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.AbstractGraphIterator;

public class DegeneracyOrderingIterator<V, E>
extends AbstractGraphIterator<V, E> {
    private Set<V>[] buckets;
    private Map<V, Integer> degrees;
    private int minDegree = Integer.MAX_VALUE;
    private V cur;

    public DegeneracyOrderingIterator(Graph<V, E> graph) {
        super(graph);
        int maxDegree = 0;
        this.degrees = new HashMap<V, Integer>();
        for (V v : graph.vertexSet()) {
            int d = 0;
            for (E e : graph.edgesOf(v)) {
                V u = Graphs.getOppositeVertex(graph, e, v);
                if (v.equals(u)) continue;
                ++d;
            }
            this.degrees.put((Integer)v, d);
            this.minDegree = Math.min(this.minDegree, d);
            maxDegree = Math.max(maxDegree, d);
        }
        this.minDegree = Math.min(this.minDegree, maxDegree);
        this.buckets = (Set[])Array.newInstance(Set.class, maxDegree + 1);
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i] = new LinkedHashSet<V>();
        }
        for (V v : graph.vertexSet()) {
            this.buckets[this.degrees.get(v)].add(v);
        }
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
        while (this.minDegree < this.buckets.length && this.buckets[this.minDegree].isEmpty()) {
            ++this.minDegree;
        }
        V result = null;
        if (this.minDegree < this.buckets.length) {
            Set<V> b = this.buckets[this.minDegree];
            V v = b.iterator().next();
            b.remove(v);
            this.degrees.remove(v);
            for (Object e : this.graph.edgesOf(v)) {
                int uDegree;
                V u = Graphs.getOppositeVertex(this.graph, e, v);
                if (v.equals(u) || !this.degrees.containsKey(u) || (uDegree = this.degrees.get(u).intValue()) <= this.minDegree) continue;
                this.buckets[uDegree].remove(u);
                this.degrees.put((Integer)u, --uDegree);
                this.buckets[uDegree].add(u);
            }
            result = v;
        }
        return result;
    }
}

