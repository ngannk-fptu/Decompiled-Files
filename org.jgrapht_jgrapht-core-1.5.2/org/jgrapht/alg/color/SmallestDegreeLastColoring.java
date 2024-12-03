/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.color.GreedyColoring;
import org.jgrapht.util.CollectionUtil;

public class SmallestDegreeLastColoring<V, E>
extends GreedyColoring<V, E> {
    public SmallestDegreeLastColoring(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    protected Iterable<V> getVertexOrdering() {
        int n = this.graph.vertexSet().size();
        int maxDegree = 0;
        HashMap<Object, Integer> degree = CollectionUtil.newHashMapWithExpectedSize(n);
        for (Object v : this.graph.vertexSet()) {
            int d = this.graph.edgesOf(v).size();
            degree.put(v, d);
            if (d <= maxDegree) continue;
            maxDegree = d;
        }
        Set[] buckets = (Set[])Array.newInstance(Set.class, maxDegree + 1);
        for (int i = 0; i <= maxDegree; ++i) {
            buckets[i] = new HashSet();
        }
        for (Object v : this.graph.vertexSet()) {
            buckets[(Integer)degree.get(v)].add(v);
        }
        ArrayDeque order = new ArrayDeque();
        for (int i = 0; i <= maxDegree; ++i) {
            while (buckets[i].size() > 0) {
                Object v = buckets[i].iterator().next();
                buckets[i].remove(v);
                order.addFirst(v);
                degree.remove(v);
                for (Object e : this.graph.edgesOf(v)) {
                    Object u = Graphs.getOppositeVertex(this.graph, e, v);
                    if (v.equals(u)) {
                        throw new IllegalArgumentException("Self-loops not allowed");
                    }
                    Integer d = (Integer)degree.get(u);
                    if (d == null || d <= 0) continue;
                    buckets[d].remove(u);
                    Integer n2 = d;
                    d = d - 1;
                    degree.put(u, d);
                    buckets[d].add(u);
                    if (d >= i) continue;
                    i = d;
                }
            }
        }
        return order;
    }
}

