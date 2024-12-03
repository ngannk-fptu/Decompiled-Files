/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Comparator;
import java.util.Iterator;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.GraphType;
import org.jgrapht.alg.isomorphism.GraphOrdering;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;

public abstract class VF2AbstractIsomorphismInspector<V, E>
implements IsomorphismInspector<V, E> {
    protected Graph<V, E> graph1;
    protected Graph<V, E> graph2;
    protected Comparator<V> vertexComparator;
    protected Comparator<E> edgeComparator;
    protected GraphOrdering<V, E> ordering1;
    protected GraphOrdering<V, E> ordering2;

    public VF2AbstractIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2, Comparator<V> vertexComparator, Comparator<E> edgeComparator, boolean cacheEdges) {
        GraphType type1 = graph1.getType();
        GraphType type2 = graph2.getType();
        if (type1.isAllowingMultipleEdges() || type2.isAllowingMultipleEdges()) {
            throw new IllegalArgumentException("graphs with multiple (parallel) edges are not supported");
        }
        if (type1.isMixed() || type2.isMixed()) {
            throw new IllegalArgumentException("mixed graphs not supported");
        }
        if (type1.isUndirected() && type2.isDirected() || type1.isDirected() && type2.isUndirected()) {
            throw new IllegalArgumentException("can not match directed with undirected graphs");
        }
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.vertexComparator = vertexComparator;
        this.edgeComparator = edgeComparator;
        this.ordering1 = new GraphOrdering<V, E>(graph1, true, cacheEdges);
        this.ordering2 = new GraphOrdering<V, E>(graph2, true, cacheEdges);
    }

    public VF2AbstractIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2, Comparator<V> vertexComparator, Comparator<E> edgeComparator) {
        this(graph1, graph2, vertexComparator, edgeComparator, true);
    }

    public VF2AbstractIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2, boolean cacheEdges) {
        this(graph1, graph2, null, null, cacheEdges);
    }

    public VF2AbstractIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        this(graph1, graph2, true);
    }

    @Override
    public abstract Iterator<GraphMapping<V, E>> getMappings();

    @Override
    public boolean isomorphismExists() {
        Iterator<GraphMapping<V, E>> iter = this.getMappings();
        return iter.hasNext();
    }
}

