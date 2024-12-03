/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.GraphDelegator;

public class ParanoidGraph<V, E>
extends GraphDelegator<V, E> {
    private static final long serialVersionUID = 5075284167422166539L;

    public ParanoidGraph(Graph<V, E> g) {
        super(g);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        ParanoidGraph.verifyAdd(this.edgeSet(), e);
        return super.addEdge(sourceVertex, targetVertex, e);
    }

    @Override
    public boolean addVertex(V v) {
        ParanoidGraph.verifyAdd(this.vertexSet(), v);
        return super.addVertex(v);
    }

    private static <T> void verifyAdd(Set<T> set, T t) {
        for (T o : set) {
            if (o == t || !o.equals(t) || o.hashCode() == t.hashCode()) continue;
            throw new IllegalArgumentException("ParanoidGraph detected objects o1 (hashCode=" + o.hashCode() + ") and o2 (hashCode=" + t.hashCode() + ") where o1.equals(o2) but o1.hashCode() != o2.hashCode()");
        }
    }
}

