/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.LinearGraphGenerator;

public class RingGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final int size;

    public RingGraphGenerator(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("must be non-negative");
        }
        this.size = size;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.size < 1) {
            return;
        }
        HashMap privateMap = new HashMap();
        new LinearGraphGenerator(this.size).generateGraph(target, privateMap);
        Object startVertex = privateMap.get("Start Vertex");
        Object endVertex = privateMap.get("End Vertex");
        target.addEdge(endVertex, startVertex);
    }
}

