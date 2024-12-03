/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.util.CollectionUtil;

public class MaximumCardinalityIterator<V, E>
extends AbstractGraphIterator<V, E> {
    private int maxCardinality;
    private int remainingVertices;
    private V current;
    private ArrayList<Set<V>> buckets;
    private Map<V, Integer> cardinalityMap;

    public MaximumCardinalityIterator(Graph<V, E> graph) {
        super(graph);
        this.remainingVertices = graph.vertexSet().size();
        if (this.remainingVertices > 0) {
            GraphTests.requireUndirected(graph);
            this.buckets = new ArrayList<Object>(Collections.nCopies(graph.vertexSet().size(), null));
            this.buckets.set(0, new LinkedHashSet<V>(graph.vertexSet()));
            this.cardinalityMap = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
            for (V v : graph.vertexSet()) {
                this.cardinalityMap.put((Integer)v, 0);
            }
            this.maxCardinality = 0;
        }
    }

    @Override
    public boolean hasNext() {
        if (this.current != null) {
            return true;
        }
        this.current = this.advance();
        if (this.current != null && this.nListeners != 0) {
            this.fireVertexTraversed(this.createVertexTraversalEvent(this.current));
        }
        return this.current != null;
    }

    @Override
    public V next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        V result = this.current;
        this.current = null;
        if (this.nListeners != 0) {
            this.fireVertexFinished(this.createVertexTraversalEvent(result));
        }
        return result;
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

    private V advance() {
        if (this.remainingVertices > 0) {
            Set<V> bucket = this.buckets.get(this.maxCardinality);
            V vertex = bucket.iterator().next();
            this.removeFromBucket(vertex);
            if (bucket.isEmpty()) {
                this.buckets.set(this.maxCardinality, null);
                do {
                    --this.maxCardinality;
                } while (this.maxCardinality >= 0 && this.buckets.get(this.maxCardinality) == null);
            }
            this.updateNeighbours(vertex);
            --this.remainingVertices;
            return vertex;
        }
        return null;
    }

    private int removeFromBucket(V vertex) {
        if (this.cardinalityMap.containsKey(vertex)) {
            int cardinality = this.cardinalityMap.get(vertex);
            this.buckets.get(cardinality).remove(vertex);
            this.cardinalityMap.remove(vertex);
            if (this.buckets.get(cardinality).isEmpty()) {
                this.buckets.set(cardinality, null);
            }
            return cardinality;
        }
        return -1;
    }

    private void addToBucket(V vertex, int cardinality) {
        this.cardinalityMap.put((Integer)vertex, cardinality);
        if (this.buckets.get(cardinality) == null) {
            this.buckets.set(cardinality, new LinkedHashSet());
        }
        this.buckets.get(cardinality).add(vertex);
    }

    private void updateNeighbours(V vertex) {
        HashSet<V> processed = new HashSet<V>();
        for (Object edge : this.graph.edgesOf(vertex)) {
            V opposite = Graphs.getOppositeVertex(this.graph, edge, vertex);
            if (!this.cardinalityMap.containsKey(opposite) || processed.contains(opposite)) continue;
            processed.add(opposite);
            this.addToBucket(opposite, this.removeFromBucket(opposite) + 1);
        }
        if (this.maxCardinality < this.graph.vertexSet().size() && this.maxCardinality >= 0 && this.buckets.get(this.maxCardinality + 1) != null) {
            ++this.maxCardinality;
        }
    }
}

