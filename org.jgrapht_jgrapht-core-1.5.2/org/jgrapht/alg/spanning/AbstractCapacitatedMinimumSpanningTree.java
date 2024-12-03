/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.spanning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.util.TypeUtil;

public abstract class AbstractCapacitatedMinimumSpanningTree<V, E>
implements CapacitatedSpanningTreeAlgorithm<V, E> {
    protected final Graph<V, E> graph;
    protected final V root;
    protected final double capacity;
    protected final Map<V, Double> demands;
    protected CapacitatedSpanningTreeSolutionRepresentation bestSolution;

    protected AbstractCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> demands) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (!graph.getType().isUndirected()) {
            throw new IllegalArgumentException("Graph must be undirected");
        }
        if (!new ConnectivityInspector<V, E>(graph).isConnected()) {
            throw new IllegalArgumentException("Graph must be connected. Otherwise, there is no capacitated minimum spanning tree.");
        }
        this.root = Objects.requireNonNull(root, "Root cannot be null");
        this.capacity = capacity;
        this.demands = Objects.requireNonNull(demands, "Demands cannot be null");
        for (V vertex : graph.vertexSet()) {
            if (vertex == root) continue;
            Double demand = demands.get(vertex);
            if (demand == null) {
                throw new IllegalArgumentException("Demands does not provide a demand for every vertex.");
            }
            if (!(demand > capacity)) continue;
            throw new IllegalArgumentException("Demands must not be greater than the capacity. Otherwise, there is no capacitated minimum spanning tree.");
        }
        this.bestSolution = new CapacitatedSpanningTreeSolutionRepresentation();
    }

    @Override
    public abstract CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree();

    protected class CapacitatedSpanningTreeSolutionRepresentation
    implements Cloneable {
        private Map<V, Integer> labels;
        private Map<Integer, Pair<Set<V>, Double>> partition;
        private int nextFreeLabel;

        public CapacitatedSpanningTreeSolutionRepresentation() {
            this(new HashMap(), new HashMap());
        }

        public CapacitatedSpanningTreeSolutionRepresentation(Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition) {
            for (Integer i : labels.values()) {
                if (i >= 0) continue;
                throw new IllegalArgumentException("Labels are not non-negative");
            }
            for (Integer i : partition.keySet()) {
                if (i >= 0) continue;
                throw new IllegalArgumentException("Labels are not non-negative");
            }
            this.labels = labels;
            this.partition = partition;
            this.getNextFreeLabel();
        }

        public CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> calculateResultingSpanningTree() {
            HashSet spanningTreeEdges = new HashSet();
            double weight = 0.0;
            for (Pair part : this.partition.values()) {
                Set set = part.getFirst();
                set.add(AbstractCapacitatedMinimumSpanningTree.this.root);
                SpanningTreeAlgorithm.SpanningTree subtree = new PrimMinimumSpanningTree(new AsSubgraph(AbstractCapacitatedMinimumSpanningTree.this.graph, set, AbstractCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
                set.remove(AbstractCapacitatedMinimumSpanningTree.this.root);
                spanningTreeEdges.addAll(subtree.getEdges());
                weight += subtree.getWeight();
            }
            return new CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTreeImpl(this.labels, this.partition, spanningTreeEdges, weight);
        }

        public void moveVertex(V vertex, Integer fromLabel, Integer toLabel) {
            this.labels.put((Integer)vertex, toLabel);
            Set oldPart = this.partition.get(fromLabel).getFirst();
            oldPart.remove(vertex);
            this.partition.put(fromLabel, Pair.of(oldPart, this.partition.get(fromLabel).getSecond() - AbstractCapacitatedMinimumSpanningTree.this.demands.get(vertex)));
            if (!this.partition.keySet().contains(toLabel)) {
                this.partition.put(toLabel, Pair.of(new HashSet(), 0.0));
            }
            Set newPart = this.partition.get(toLabel).getFirst();
            newPart.add(vertex);
            this.partition.put(toLabel, Pair.of(newPart, this.partition.get(toLabel).getSecond() + AbstractCapacitatedMinimumSpanningTree.this.demands.get(vertex)));
        }

        public void moveVertices(Set<V> vertices, Integer fromLabel, Integer toLabel) {
            double weightOfVertices = 0.0;
            for (Object v : vertices) {
                weightOfVertices += AbstractCapacitatedMinimumSpanningTree.this.demands.get(v).doubleValue();
                this.labels.put((Integer)v, toLabel);
            }
            if (!this.partition.keySet().contains(toLabel)) {
                this.partition.put(toLabel, Pair.of(new HashSet(), 0.0));
            }
            Set newPart = this.partition.get(toLabel).getFirst();
            newPart.addAll(vertices);
            this.partition.put(toLabel, Pair.of(newPart, this.partition.get(toLabel).getSecond() + weightOfVertices));
            Set oldPart = this.partition.get(fromLabel).getFirst();
            oldPart.removeAll(vertices);
            this.partition.put(fromLabel, Pair.of(oldPart, this.partition.get(fromLabel).getSecond() - weightOfVertices));
        }

        public Set<Integer> partitionSubtreesOfSubset(Set<V> vertexSubset, int label) {
            LinkedList subtreesOfSubset = new LinkedList();
            if (vertexSubset.isEmpty()) {
                return new HashSet<Integer>();
            }
            vertexSubset.add(AbstractCapacitatedMinimumSpanningTree.this.root);
            SpanningTreeAlgorithm.SpanningTree spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AbstractCapacitatedMinimumSpanningTree.this.graph, vertexSubset, AbstractCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
            AsSubgraph spanningTreeGraph = new AsSubgraph(AbstractCapacitatedMinimumSpanningTree.this.graph, vertexSubset, spanningTree.getEdges());
            int degreeOfRoot = spanningTreeGraph.degreeOf(AbstractCapacitatedMinimumSpanningTree.this.root);
            if (degreeOfRoot == 1) {
                vertexSubset.remove(AbstractCapacitatedMinimumSpanningTree.this.root);
                return new HashSet<Integer>();
            }
            HashSet<Integer> affectedLabels = new HashSet<Integer>();
            DepthFirstIterator depthFirstIterator = new DepthFirstIterator(spanningTreeGraph, AbstractCapacitatedMinimumSpanningTree.this.root);
            if (depthFirstIterator.hasNext()) {
                depthFirstIterator.next();
            }
            int numberOfRootEdgesExplored = 0;
            HashSet currentSubtree = new HashSet();
            while (depthFirstIterator.hasNext()) {
                Object next = depthFirstIterator.next();
                if (spanningTreeGraph.containsEdge(AbstractCapacitatedMinimumSpanningTree.this.root, next)) {
                    if (!currentSubtree.isEmpty()) {
                        subtreesOfSubset.add(currentSubtree);
                        currentSubtree = new HashSet();
                    }
                    if (++numberOfRootEdgesExplored == degreeOfRoot) break;
                }
                currentSubtree.add(next);
            }
            for (Set set : subtreesOfSubset) {
                int nextLabel = this.getNextFreeLabel();
                this.moveVertices(set, label, nextLabel);
                affectedLabels.add(nextLabel);
            }
            vertexSubset.remove(AbstractCapacitatedMinimumSpanningTree.this.root);
            return affectedLabels;
        }

        public void cleanUp() {
            this.partition.entrySet().removeIf(entry -> ((Set)((Pair)entry.getValue()).getFirst()).isEmpty());
        }

        public int getNextFreeLabel() {
            int freeLabel = this.nextFreeLabel++;
            while (this.partition.keySet().contains(this.nextFreeLabel)) {
                ++this.nextFreeLabel;
            }
            return freeLabel;
        }

        public int getLabel(V vertex) {
            return this.labels.get(vertex);
        }

        public Set<Integer> getLabels() {
            return this.partition.keySet();
        }

        public Set<V> getPartitionSet(Integer label) {
            return this.partition.get(label).getFirst();
        }

        public double getPartitionWeight(Integer label) {
            return this.partition.get(label).getSecond();
        }

        public CapacitatedSpanningTreeSolutionRepresentation clone() {
            try {
                CapacitatedSpanningTreeSolutionRepresentation capacitatedSpanningTreeSolutionRepresentation = (CapacitatedSpanningTreeSolutionRepresentation)TypeUtil.uncheckedCast(super.clone());
                capacitatedSpanningTreeSolutionRepresentation.labels = new HashMap<Integer, Integer>(this.labels);
                capacitatedSpanningTreeSolutionRepresentation.partition = new HashMap();
                for (Map.Entry entry : this.partition.entrySet()) {
                    capacitatedSpanningTreeSolutionRepresentation.partition.put(entry.getKey(), Pair.of(new HashSet(entry.getValue().getFirst()), entry.getValue().getSecond()));
                }
                capacitatedSpanningTreeSolutionRepresentation.nextFreeLabel = this.nextFreeLabel;
                return capacitatedSpanningTreeSolutionRepresentation;
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
}

