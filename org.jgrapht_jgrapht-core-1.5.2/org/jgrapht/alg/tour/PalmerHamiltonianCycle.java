/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.Arrays;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.util.ArrayUtil;

public class PalmerHamiltonianCycle<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        if (!GraphTests.hasOreProperty(graph)) {
            throw new IllegalArgumentException("Graph doesn't have Ore's property");
        }
        Set<V> vertices = graph.vertexSet();
        int n = vertices.size();
        Object[] tour = vertices.toArray(new Object[n + 1]);
        while (PalmerHamiltonianCycle.searchAndCloseGap(tour, n, graph)) {
        }
        tour[n] = tour[0];
        return this.closedVertexListToTour(Arrays.asList(tour), graph);
    }

    private static <V, E> boolean searchAndCloseGap(V[] tour, int n, Graph<V, E> graph) {
        V v = tour[n - 1];
        for (int i = 0; i < n; ++i) {
            V vN = tour[i];
            if (!graph.containsEdge(v, vN)) {
                V u = tour[n - 1];
                for (int j = 0; j < n; ++j) {
                    boolean distinct;
                    V uN = tour[j];
                    boolean bl = distinct = v != u && vN != u && v != uN;
                    if (distinct && graph.containsEdge(v, u) && graph.containsEdge(vN, uN)) {
                        PalmerHamiltonianCycle.reverseInCircle(tour, i, j - 1);
                        return true;
                    }
                    u = uN;
                }
                throw new IllegalStateException("Found a gap but no mean to close it");
            }
            v = vN;
        }
        return false;
    }

    private static <V> void reverseInCircle(V[] array, int start, int end) {
        if (start < end) {
            ArrayUtil.reverse(array, start, end);
        } else {
            ArrayUtil.reverse(array, end + 1, start - 1);
        }
    }
}

