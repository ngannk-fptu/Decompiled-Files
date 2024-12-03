/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;

public class DefaultGraphMapping<V, E>
implements GraphMapping<V, E> {
    private Map<V, V> graphMappingForward;
    private Map<V, V> graphMappingReverse;
    private Graph<V, E> graph1;
    private Graph<V, E> graph2;

    public DefaultGraphMapping(Map<V, V> g1ToG2, Map<V, V> g2ToG1, Graph<V, E> g1, Graph<V, E> g2) {
        this.graph1 = g1;
        this.graph2 = g2;
        this.graphMappingForward = g1ToG2;
        this.graphMappingReverse = g2ToG1;
    }

    @Override
    public E getEdgeCorrespondence(E currEdge, boolean forward) {
        Graph<V, E> targetGraph;
        Graph<V, E> sourceGraph;
        if (forward) {
            sourceGraph = this.graph1;
            targetGraph = this.graph2;
        } else {
            sourceGraph = this.graph2;
            targetGraph = this.graph1;
        }
        V mappedSourceVertex = this.getVertexCorrespondence(sourceGraph.getEdgeSource(currEdge), forward);
        V mappedTargetVertex = this.getVertexCorrespondence(sourceGraph.getEdgeTarget(currEdge), forward);
        if (mappedSourceVertex == null || mappedTargetVertex == null) {
            return null;
        }
        return targetGraph.getEdge(mappedSourceVertex, mappedTargetVertex);
    }

    @Override
    public V getVertexCorrespondence(V keyVertex, boolean forward) {
        Map<V, V> graphMapping = forward ? this.graphMappingForward : this.graphMappingReverse;
        return graphMapping.get(keyVertex);
    }
}

