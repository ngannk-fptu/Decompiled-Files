/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface StrongConnectivityAlgorithm<V, E> {
    public Graph<V, E> getGraph();

    public boolean isStronglyConnected();

    public List<Set<V>> stronglyConnectedSets();

    public List<Graph<V, E>> getStronglyConnectedComponents();

    public Graph<Graph<V, E>, DefaultEdge> getCondensation();
}

