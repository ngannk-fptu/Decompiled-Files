/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class EmptyGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final int size;

    public EmptyGraphGenerator(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
        this.size = size;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        for (int i = 0; i < this.size; ++i) {
            target.addVertex();
        }
    }
}

