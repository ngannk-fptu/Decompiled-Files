/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.Map;
import org.jgrapht.Graph;

public interface GraphGenerator<V, E, T> {
    public void generateGraph(Graph<V, E> var1, Map<String, T> var2);

    default public void generateGraph(Graph<V, E> target) {
        this.generateGraph(target, null);
    }
}

