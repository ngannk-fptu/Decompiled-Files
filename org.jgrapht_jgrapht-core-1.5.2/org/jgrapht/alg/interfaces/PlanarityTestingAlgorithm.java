/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

public interface PlanarityTestingAlgorithm<V, E> {
    public boolean isPlanar();

    public Embedding<V, E> getEmbedding();

    public Graph<V, E> getKuratowskiSubdivision();

    public static class EmbeddingImpl<V, E>
    implements Embedding<V, E> {
        private Graph<V, E> graph;
        private Map<V, List<E>> embeddingMap;

        public EmbeddingImpl(Graph<V, E> graph, Map<V, List<E>> embeddingMap) {
            this.graph = graph;
            this.embeddingMap = embeddingMap;
        }

        @Override
        public List<E> getEdgesAround(V vertex) {
            return this.embeddingMap.get(vertex);
        }

        @Override
        public Graph<V, E> getGraph() {
            return this.graph;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder("[");
            for (Map.Entry<V, List<E>> entry : this.embeddingMap.entrySet()) {
                builder.append(entry.getKey().toString()).append(" -> ").append(entry.getValue().stream().map(e -> Graphs.getOppositeVertex(this.graph, e, entry.getKey()).toString()).collect(Collectors.joining(", ", "[", "]"))).append(", ");
            }
            return builder.append("]").toString();
        }
    }

    public static interface Embedding<V, E> {
        public List<E> getEdgesAround(V var1);

        public Graph<V, E> getGraph();
    }
}

