/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.traverse.DepthFirstIterator;

public interface CapacitatedSpanningTreeAlgorithm<V, E> {
    public CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree();

    public static class CapacitatedSpanningTreeImpl<V, E>
    implements CapacitatedSpanningTree<V, E>,
    Serializable {
        private static final long serialVersionUID = 7088989899889893333L;
        private final Map<V, Integer> labels;
        private final Map<Integer, Pair<Set<V>, Double>> partition;
        private final double weight;
        private final Set<E> edges;

        public CapacitatedSpanningTreeImpl(Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition, Set<E> edges, double weight) {
            this.labels = labels;
            this.partition = partition;
            this.edges = edges;
            this.weight = weight;
        }

        @Override
        public boolean isCapacitatedSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> demands) {
            if (this.getEdges().size() != graph.vertexSet().size() - 1) {
                return false;
            }
            for (Pair<Set<V>, Double> pair : this.getPartition().values()) {
                for (Pair<Set<V>, Double> set2 : this.getPartition().values()) {
                    if (pair == set2 || Collections.disjoint((Collection)pair.getFirst(), (Collection)set2.getFirst())) continue;
                    return false;
                }
            }
            int numberOfNodesExplored = 0;
            for (Pair<Set<V>, Double> pair : this.getPartition().values()) {
                int currentCapacity = 0;
                for (V v : pair.getFirst()) {
                    currentCapacity = (int)((double)currentCapacity + demands.get(v));
                    ++numberOfNodesExplored;
                }
                if (!((double)currentCapacity > capacity)) continue;
                return false;
            }
            if (graph.vertexSet().size() - 1 != numberOfNodesExplored) {
                return false;
            }
            AsSubgraph<V, E> asSubgraph = new AsSubgraph<V, E>(graph, graph.vertexSet(), this.getEdges());
            DepthFirstIterator<V, E> depthFirstIterator = new DepthFirstIterator<V, E>(asSubgraph, root);
            if (depthFirstIterator.hasNext()) {
                depthFirstIterator.next();
            }
            int numberOfRootEdgesExplored = 0;
            HashSet currentSubtree = new HashSet();
            while (depthFirstIterator.hasNext()) {
                Object next = depthFirstIterator.next();
                if (asSubgraph.containsEdge(root, next)) {
                    if (!currentSubtree.isEmpty()) {
                        if (!currentSubtree.equals(this.getPartition().get(this.getLabels().get(currentSubtree.iterator().next())).getFirst())) {
                            return false;
                        }
                        currentSubtree = new HashSet();
                    }
                    ++numberOfRootEdgesExplored;
                }
                currentSubtree.add(next);
            }
            return numberOfRootEdgesExplored == asSubgraph.degreeOf(root);
        }

        @Override
        public Map<V, Integer> getLabels() {
            return this.labels;
        }

        @Override
        public Map<Integer, Pair<Set<V>, Double>> getPartition() {
            return this.partition;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public Set<E> getEdges() {
            return this.edges;
        }

        public String toString() {
            return "Capacitated Spanning-Tree [weight=" + this.weight + ", edges=" + this.edges + ", labels=" + this.labels + ", partition=" + this.partition + "]";
        }
    }

    public static interface CapacitatedSpanningTree<V, E>
    extends Iterable<E>,
    SpanningTreeAlgorithm.SpanningTree<E> {
        public boolean isCapacitatedSpanningTree(Graph<V, E> var1, V var2, double var3, Map<V, Double> var5);

        public Map<V, Integer> getLabels();

        public Map<Integer, Pair<Set<V>, Double>> getPartition();
    }
}

