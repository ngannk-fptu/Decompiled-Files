/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm;

public class PatonCycleBase<V, E>
implements CycleBasisAlgorithm<V, E> {
    private Graph<V, E> graph;

    public PatonCycleBase(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
    }

    @Override
    public CycleBasisAlgorithm.CycleBasis<V, E> getCycleBasis() {
        GraphTests.requireUndirected(this.graph);
        if (GraphTests.hasMultipleEdges(this.graph)) {
            throw new IllegalArgumentException("Graphs with multiple edges not supported");
        }
        HashMap<V, Map<Object, Object>> used = new HashMap<V, Map<Object, Object>>();
        HashMap<V, E> parent = new HashMap<V, E>();
        ArrayDeque<V> stack = new ArrayDeque<V>();
        LinkedHashSet cycles = new LinkedHashSet();
        int totalLength = 0;
        double totalWeight = 0.0;
        for (V root : this.graph.vertexSet()) {
            if (parent.containsKey(root)) continue;
            used.clear();
            parent.put(root, null);
            used.put(root, new HashMap());
            stack.push(root);
            while (!stack.isEmpty()) {
                Object current = stack.pop();
                Map currentUsed = (Map)used.get(current);
                for (E e : this.graph.edgesOf(current)) {
                    Map neighbourUsed;
                    V neighbor = Graphs.getOppositeVertex(this.graph, e, current);
                    if (!used.containsKey(neighbor)) {
                        parent.put(neighbor, e);
                        neighbourUsed = new HashMap();
                        neighbourUsed.put(current, e);
                        used.put(neighbor, neighbourUsed);
                        stack.push(neighbor);
                        continue;
                    }
                    if (neighbor.equals(current)) {
                        ArrayList<E> cycle = new ArrayList<E>();
                        cycle.add(e);
                        totalWeight += this.graph.getEdgeWeight(e);
                        ++totalLength;
                        cycles.add(cycle);
                        continue;
                    }
                    if (currentUsed.containsKey(neighbor)) continue;
                    neighbourUsed = (Map)used.get(neighbor);
                    double weight = 0.0;
                    ArrayList<Object> cycle = new ArrayList<Object>();
                    cycle.add(e);
                    weight += this.graph.getEdgeWeight(e);
                    Object v = current;
                    while (!neighbourUsed.containsKey(v)) {
                        Object p = parent.get(v);
                        cycle.add(p);
                        weight += this.graph.getEdgeWeight(p);
                        v = Graphs.getOppositeVertex(this.graph, p, v);
                    }
                    Object a = neighbourUsed.get(v);
                    cycle.add(a);
                    weight += this.graph.getEdgeWeight(a);
                    neighbourUsed.put(current, e);
                    cycles.add(cycle);
                    totalLength += cycle.size();
                    totalWeight += weight;
                }
            }
        }
        return new CycleBasisAlgorithm.CycleBasisImpl<V, E>(this.graph, cycles, totalLength, totalWeight);
    }
}

