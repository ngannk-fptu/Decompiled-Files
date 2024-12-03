/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.builder;

import org.jgrapht.Graph;
import org.jgrapht.graph.builder.AbstractGraphBuilder;

public class GraphBuilder<V, E, G extends Graph<V, E>>
extends AbstractGraphBuilder<V, E, G, GraphBuilder<V, E, G>> {
    public GraphBuilder(G baseGraph) {
        super(baseGraph);
    }

    @Override
    protected GraphBuilder<V, E, G> self() {
        return this;
    }
}

