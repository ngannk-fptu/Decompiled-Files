/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Comparator;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2AbstractIsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphMappingIterator;

public class VF2SubgraphIsomorphismInspector<V, E>
extends VF2AbstractIsomorphismInspector<V, E> {
    public VF2SubgraphIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2, Comparator<V> vertexComparator, Comparator<E> edgeComparator, boolean cacheEdges) {
        super(graph1, graph2, vertexComparator, edgeComparator, cacheEdges);
    }

    public VF2SubgraphIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2, Comparator<V> vertexComparator, Comparator<E> edgeComparator) {
        super(graph1, graph2, vertexComparator, edgeComparator, true);
    }

    public VF2SubgraphIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2, boolean cacheEdges) {
        super(graph1, graph2, null, null, cacheEdges);
    }

    public VF2SubgraphIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2, true);
    }

    public VF2SubgraphMappingIterator<V, E> getMappings() {
        return new VF2SubgraphMappingIterator(this.ordering1, this.ordering2, this.vertexComparator, this.edgeComparator);
    }
}

