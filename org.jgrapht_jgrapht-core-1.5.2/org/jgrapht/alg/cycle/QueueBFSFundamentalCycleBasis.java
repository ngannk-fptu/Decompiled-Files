/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.AbstractFundamentalCycleBasis;

public class QueueBFSFundamentalCycleBasis<V, E>
extends AbstractFundamentalCycleBasis<V, E> {
    public QueueBFSFundamentalCycleBasis(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    protected Map<V, E> computeSpanningForest() {
        HashMap pred = new HashMap();
        ArrayDeque<Object> queue = new ArrayDeque<Object>();
        for (Object s : this.graph.vertexSet()) {
            if (pred.containsKey(s)) continue;
            pred.put(s, null);
            queue.addLast(s);
            while (!queue.isEmpty()) {
                Object v = queue.removeFirst();
                for (Object e : this.graph.edgesOf(v)) {
                    Object u = Graphs.getOppositeVertex(this.graph, e, v);
                    if (pred.containsKey(u)) continue;
                    pred.put(u, e);
                    queue.addLast(u);
                }
            }
        }
        return pred;
    }
}

