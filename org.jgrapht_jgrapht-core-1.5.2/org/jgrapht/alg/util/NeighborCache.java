/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.util.ModifiableInteger;

public class NeighborCache<V, E>
implements GraphListener<V, E> {
    private Map<V, Neighbors<V>> successors = new HashMap<V, Neighbors<V>>();
    private Map<V, Neighbors<V>> predecessors = new HashMap<V, Neighbors<V>>();
    private Map<V, Neighbors<V>> neighbors = new HashMap<V, Neighbors<V>>();
    private Graph<V, E> graph;

    public NeighborCache(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    public Set<V> predecessorsOf(V v) {
        return this.fetch(v, this.predecessors, k -> new Neighbors<Object>(Graphs.predecessorListOf(this.graph, v)));
    }

    public Set<V> successorsOf(V v) {
        return this.fetch(v, this.successors, k -> new Neighbors<Object>(Graphs.successorListOf(this.graph, v)));
    }

    public Set<V> neighborsOf(V v) {
        return this.fetch(v, this.neighbors, k -> new Neighbors<Object>(Graphs.neighborListOf(this.graph, v)));
    }

    public List<V> neighborListOf(V v) {
        Neighbors<V> nbrs = this.neighbors.get(v);
        if (nbrs == null) {
            nbrs = new Neighbors<V>(Graphs.neighborListOf(this.graph, v));
            this.neighbors.put((Neighbors<V>)v, (Neighbors<Neighbors<V>>)nbrs);
        }
        return nbrs.getNeighborList();
    }

    private Set<V> fetch(V vertex, Map<V, Neighbors<V>> map, Function<V, Neighbors<V>> func) {
        return map.computeIfAbsent((Neighbors<V>)vertex, (Function<Neighbors<V>, Neighbors<Neighbors<V>>>)func).getNeighbors();
    }

    @Override
    public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
        assert (e.getSource() == this.graph) : "This NeighborCache is added as a listener to a graph other than the one specified during the construction of this NeighborCache!";
        V source = e.getEdgeSource();
        V target = e.getEdgeTarget();
        if (this.successors.containsKey(source)) {
            this.successors.get(source).addNeighbor(target);
        }
        if (this.predecessors.containsKey(target)) {
            this.predecessors.get(target).addNeighbor(source);
        }
        if (this.neighbors.containsKey(source)) {
            this.neighbors.get(source).addNeighbor(target);
        }
        if (this.neighbors.containsKey(target)) {
            this.neighbors.get(target).addNeighbor(source);
        }
    }

    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
        assert (e.getSource() == this.graph) : "This NeighborCache is added as a listener to a graph other than the one specified during the construction of this NeighborCache!";
        V source = e.getEdgeSource();
        V target = e.getEdgeTarget();
        if (this.successors.containsKey(source)) {
            this.successors.get(source).removeNeighbor(target);
        }
        if (this.predecessors.containsKey(target)) {
            this.predecessors.get(target).removeNeighbor(source);
        }
        if (this.neighbors.containsKey(source)) {
            this.neighbors.get(source).removeNeighbor(target);
        }
        if (this.neighbors.containsKey(target)) {
            this.neighbors.get(target).removeNeighbor(source);
        }
    }

    @Override
    public void vertexAdded(GraphVertexChangeEvent<V> e) {
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<V> e) {
        assert (e.getSource() == this.graph) : "This NeighborCache is added as a listener to a graph other than the one specified during the construction of this NeighborCache!";
        this.successors.remove(e.getVertex());
        this.predecessors.remove(e.getVertex());
        this.neighbors.remove(e.getVertex());
    }

    static class Neighbors<V> {
        private Map<V, ModifiableInteger> neighborCounts = new LinkedHashMap<V, ModifiableInteger>();
        private Set<V> neighborSet = Collections.unmodifiableSet(this.neighborCounts.keySet());

        public Neighbors(Collection<V> neighbors) {
            for (V neighbor : neighbors) {
                this.addNeighbor(neighbor);
            }
        }

        public void addNeighbor(V v) {
            ModifiableInteger count = this.neighborCounts.get(v);
            if (count == null) {
                count = new ModifiableInteger(1);
                this.neighborCounts.put((ModifiableInteger)v, count);
            } else {
                count.increment();
            }
        }

        public void removeNeighbor(V v) {
            ModifiableInteger count = this.neighborCounts.get(v);
            if (count == null) {
                throw new IllegalArgumentException("Attempting to remove a neighbor that wasn't present");
            }
            count.decrement();
            if (count.getValue() == 0) {
                this.neighborCounts.remove(v);
            }
        }

        public Set<V> getNeighbors() {
            return this.neighborSet;
        }

        public List<V> getNeighborList() {
            ArrayList<V> neighbors = new ArrayList<V>();
            for (Map.Entry<V, ModifiableInteger> entry : this.neighborCounts.entrySet()) {
                V v = entry.getKey();
                int count = entry.getValue().intValue();
                for (int i = 0; i < count; ++i) {
                    neighbors.add(v);
                }
            }
            return neighbors;
        }

        public String toString() {
            return this.neighborSet.toString();
        }
    }
}

