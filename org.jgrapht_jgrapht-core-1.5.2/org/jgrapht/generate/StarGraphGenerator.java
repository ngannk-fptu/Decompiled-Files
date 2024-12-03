/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class StarGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    public static final String CENTER_VERTEX = "Center Vertex";
    private final int order;

    public StarGraphGenerator(int order) {
        if (order < 0) {
            throw new IllegalArgumentException("Order must be non-negative");
        }
        this.order = order;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.order < 1) {
            return;
        }
        V centerVertex = target.addVertex();
        if (resultMap != null) {
            resultMap.put(CENTER_VERTEX, centerVertex);
        }
        for (int i = 0; i < this.order - 1; ++i) {
            target.addEdge(target.addVertex(), centerVertex);
        }
    }
}

