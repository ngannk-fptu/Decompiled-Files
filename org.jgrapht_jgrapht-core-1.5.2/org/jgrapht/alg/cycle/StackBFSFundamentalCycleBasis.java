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

public class StackBFSFundamentalCycleBasis<V, E>
extends AbstractFundamentalCycleBasis<V, E> {
    public StackBFSFundamentalCycleBasis(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    protected Map<V, E> computeSpanningForest() {
        HashMap pred = new HashMap();
        ArrayDeque<Object> stack = new ArrayDeque<Object>();
        for (Object s : this.graph.vertexSet()) {
            if (pred.containsKey(s)) continue;
            pred.put(s, null);
            stack.push(s);
            while (!stack.isEmpty()) {
                Object v = stack.pop();
                for (Object e : this.graph.edgesOf(v)) {
                    Object u = Graphs.getOppositeVertex(this.graph, e, v);
                    if (pred.containsKey(u)) continue;
                    pred.put(u, e);
                    stack.push(u);
                }
            }
        }
        return pred;
    }
}

