/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.graph.IntrusiveEdgesSpecifics;
import org.jgrapht.graph.specifics.ArrayUnenforcedSetEdgeSetFactory;
import org.jgrapht.graph.specifics.Specifics;

public interface GraphSpecificsStrategy<V, E>
extends Serializable {
    public Function<GraphType, IntrusiveEdgesSpecifics<V, E>> getIntrusiveEdgesSpecificsFactory();

    public BiFunction<Graph<V, E>, GraphType, Specifics<V, E>> getSpecificsFactory();

    default public EdgeSetFactory<V, E> getEdgeSetFactory() {
        return new ArrayUnenforcedSetEdgeSetFactory();
    }
}

