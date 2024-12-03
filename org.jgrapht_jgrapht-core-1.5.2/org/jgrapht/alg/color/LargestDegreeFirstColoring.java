/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.color.GreedyColoring;
import org.jgrapht.util.CollectionUtil;

public class LargestDegreeFirstColoring<V, E>
extends GreedyColoring<V, E> {
    public LargestDegreeFirstColoring(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    protected Iterable<V> getVertexOrdering() {
        ArrayList<Object> nodes;
        int n = this.graph.vertexSet().size();
        int maxDegree = 0;
        HashMap degree = CollectionUtil.newHashMapWithExpectedSize(n);
        for (Object v2 : this.graph.vertexSet()) {
            int d = this.graph.edgesOf(v2).size();
            degree.put(v2, d);
            if (d <= maxDegree) continue;
            maxDegree = d;
        }
        if (maxDegree > 3 * n) {
            nodes = new ArrayList(this.graph.vertexSet());
            nodes.sort((u, v) -> -1 * Integer.compare((Integer)degree.get(u), (Integer)degree.get(v)));
            return nodes;
        }
        nodes = new ArrayList(n);
        Set[] buckets = (Set[])Array.newInstance(Set.class, maxDegree + 1);
        for (int i = 0; i <= maxDegree; ++i) {
            buckets[i] = new HashSet();
        }
        for (Object v3 : this.graph.vertexSet()) {
            buckets[(Integer)degree.get(v3)].add(v3);
        }
        for (int i = maxDegree; i >= 0; --i) {
            nodes.addAll(buckets[i]);
        }
        return nodes;
    }
}

