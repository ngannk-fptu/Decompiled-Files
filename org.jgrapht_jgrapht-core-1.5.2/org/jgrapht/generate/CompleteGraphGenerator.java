/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.generate.GraphGenerator;

public class CompleteGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final int size;

    public CompleteGraphGenerator(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
        this.size = size;
    }

    public CompleteGraphGenerator() {
        this.size = 0;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        GraphTests.requireDirectedOrUndirected(target);
        boolean isDirected = target.getType().isDirected();
        for (int i = 0; i < this.size; ++i) {
            target.addVertex();
        }
        ArrayList<V> nodes = new ArrayList<V>(target.vertexSet());
        for (int i = 0; i < nodes.size(); ++i) {
            for (int j = i + 1; j < nodes.size(); ++j) {
                Object v = nodes.get(i);
                Object u = nodes.get(j);
                target.addEdge(v, u);
                if (!isDirected) continue;
                target.addEdge(u, v);
            }
        }
    }
}

