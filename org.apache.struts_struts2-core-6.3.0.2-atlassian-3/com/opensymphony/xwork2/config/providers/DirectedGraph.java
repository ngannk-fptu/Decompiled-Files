/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.providers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class DirectedGraph<T>
implements Iterable<T> {
    private final Map<T, Set<T>> mGraph = new HashMap<T, Set<T>>();

    public boolean addNode(T node) {
        if (this.mGraph.containsKey(node)) {
            return false;
        }
        this.mGraph.put(node, new HashSet());
        return true;
    }

    public void addEdge(T start, T dest) {
        if (!this.mGraph.containsKey(start)) {
            throw new NoSuchElementException("The start node does not exist in the graph.");
        }
        if (!this.mGraph.containsKey(dest)) {
            throw new NoSuchElementException("The destination node does not exist in the graph.");
        }
        this.mGraph.get(start).add(dest);
    }

    public void removeEdge(T start, T dest) {
        if (!this.mGraph.containsKey(start)) {
            throw new NoSuchElementException("The start node does not exist in the graph.");
        }
        if (!this.mGraph.containsKey(dest)) {
            throw new NoSuchElementException("The destination node does not exist in the graph.");
        }
        this.mGraph.get(start).remove(dest);
    }

    public boolean edgeExists(T start, T end) {
        if (!this.mGraph.containsKey(start)) {
            throw new NoSuchElementException("The start node does not exist in the graph.");
        }
        if (!this.mGraph.containsKey(end)) {
            throw new NoSuchElementException("The end node does not exist in the graph.");
        }
        return this.mGraph.get(start).contains(end);
    }

    public Set<T> edgesFrom(T node) {
        Set<T> arcs = this.mGraph.get(node);
        if (arcs == null) {
            throw new NoSuchElementException("Source node does not exist.");
        }
        return Collections.unmodifiableSet(arcs);
    }

    @Override
    public Iterator<T> iterator() {
        return this.mGraph.keySet().iterator();
    }

    public int size() {
        return this.mGraph.size();
    }

    public boolean isEmpty() {
        return this.mGraph.isEmpty();
    }
}

