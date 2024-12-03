/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class LinearGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    public static final String START_VERTEX = "Start Vertex";
    public static final String END_VERTEX = "End Vertex";
    private int size;

    public LinearGraphGenerator(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("must be non-negative");
        }
        this.size = size;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        Object lastVertex = null;
        for (int i = 0; i < this.size; ++i) {
            V newVertex = target.addVertex();
            if (lastVertex == null) {
                if (resultMap != null) {
                    resultMap.put(START_VERTEX, newVertex);
                }
            } else {
                target.addEdge(lastVertex, newVertex);
            }
            lastVertex = newVertex;
        }
        if (resultMap != null && lastVertex != null) {
            resultMap.put(END_VERTEX, lastVertex);
        }
    }
}

