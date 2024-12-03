/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphIterables;

public class DefaultGraphIterables<V, E>
implements GraphIterables<V, E> {
    protected Graph<V, E> graph;

    public DefaultGraphIterables() {
        this(null);
    }

    public DefaultGraphIterables(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public Graph<V, E> getGraph() {
        return this.graph;
    }
}

