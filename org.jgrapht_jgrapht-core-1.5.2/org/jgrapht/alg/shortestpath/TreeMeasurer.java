/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.util.CollectionUtil;

public class TreeMeasurer<V, E> {
    private final Graph<V, E> graph;

    public TreeMeasurer(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    private V computeFarthestVertex(BreadthFirstIterator<V, E> bfs) {
        V farthest = null;
        int dist = Integer.MIN_VALUE;
        while (bfs.hasNext()) {
            Object v = bfs.next();
            int depth = bfs.getDepth(v);
            if (dist >= depth) continue;
            farthest = v;
            dist = depth;
        }
        return farthest;
    }

    public Set<V> getGraphCenter() {
        Set graphCenter;
        GraphTests.requireUndirected(this.graph);
        if (this.graph.vertexSet().isEmpty()) {
            return new LinkedHashSet();
        }
        V r = this.graph.vertexSet().iterator().next();
        V v1 = this.computeFarthestVertex(new BreadthFirstIterator<V, E>(this.graph, r));
        BreadthFirstIterator<V, E> bfs = new BreadthFirstIterator<V, E>(this.graph, v1);
        V v2 = this.computeFarthestVertex(bfs);
        ArrayList<V> diameterPath = new ArrayList<V>();
        do {
            diameterPath.add(v2);
        } while ((v2 = bfs.getParent(v2)) != null);
        if (diameterPath.size() % 2 == 1) {
            graphCenter = Collections.singleton(diameterPath.get(diameterPath.size() / 2));
        } else {
            graphCenter = CollectionUtil.newLinkedHashSetWithExpectedSize(2);
            graphCenter.add(diameterPath.get(diameterPath.size() / 2));
            graphCenter.add(diameterPath.get(diameterPath.size() / 2 - 1));
        }
        return graphCenter;
    }
}

