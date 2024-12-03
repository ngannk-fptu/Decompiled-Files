/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.TypeUtil;

public class AhujaOrlinSharmaCyclicExchangeLocalAugmentation<V, E> {
    private Graph<V, E> graph;
    private Map<V, Integer> labelMap;
    private int lengthBound;
    private boolean bestImprovement;

    public AhujaOrlinSharmaCyclicExchangeLocalAugmentation(Graph<V, E> graph, int lengthBound, Map<V, Integer> labelMap, boolean bestImprovement) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (!graph.getType().isDirected()) {
            throw new IllegalArgumentException("The graph has to be directed.");
        }
        this.lengthBound = lengthBound;
        this.labelMap = Objects.requireNonNull(labelMap, "Labels cannot be null");
        for (V vertex : graph.vertexSet()) {
            if (labelMap.containsKey(vertex)) continue;
            throw new IllegalArgumentException("Every vertex has to be labeled, that is, every vertex needs an entry in labelMap.");
        }
        this.bestImprovement = bestImprovement;
    }

    public GraphWalk<V, E> getLocalAugmentationCycle() {
        int k = 1;
        LabeledPath bestCycle = new LabeledPath(new ArrayList(this.lengthBound), Double.MAX_VALUE, new HashSet<Integer>());
        LinkedHashMap<PathSetKey<V>, LabeledPath<V>> pathsLengthK = new LinkedHashMap<PathSetKey<V>, LabeledPath<V>>();
        LinkedHashMap<PathSetKey<V>, LabeledPath<V>> pathsLengthKplus1 = new LinkedHashMap<PathSetKey<V>, LabeledPath<V>>();
        for (E e : this.graph.edgeSet()) {
            V targetVertex;
            if (!(this.graph.getEdgeWeight(e) < 0.0)) continue;
            V sourceVertex = this.graph.getEdgeSource(e);
            if (sourceVertex == (targetVertex = this.graph.getEdgeTarget(e))) {
                ArrayList<V> vertices = new ArrayList<V>();
                vertices.add(sourceVertex);
                vertices.add(targetVertex);
                double currentEdgeWeight = this.graph.getEdgeWeight(e);
                double oppositeEdgeWeight = this.graph.getEdgeWeight(this.graph.getEdge(targetVertex, sourceVertex));
                if (this.bestImprovement) {
                    if (bestCycle.cost > currentEdgeWeight + oppositeEdgeWeight) {
                        HashSet<Integer> labelSet = new HashSet<Integer>();
                        labelSet.add(this.labelMap.get(sourceVertex));
                        bestCycle = new LabeledPath(vertices, currentEdgeWeight + oppositeEdgeWeight, labelSet);
                    }
                } else {
                    return new GraphWalk<V, E>(this.graph, vertices, currentEdgeWeight + oppositeEdgeWeight);
                }
            }
            if (this.labelMap.get(sourceVertex).equals(this.labelMap.get(targetVertex))) continue;
            ArrayList<V> pathVertices = new ArrayList<V>(this.lengthBound);
            HashSet<Integer> pathLabels = new HashSet<Integer>();
            pathVertices.add(sourceVertex);
            pathVertices.add(targetVertex);
            pathLabels.add(this.labelMap.get(sourceVertex));
            pathLabels.add(this.labelMap.get(targetVertex));
            LabeledPath path = new LabeledPath(pathVertices, this.graph.getEdgeWeight(e), pathLabels);
            this.updatePathIndex(pathsLengthK, path);
        }
        while (k < this.lengthBound) {
            for (LabeledPath path : pathsLengthK.values()) {
                double currentCost;
                Object head = path.getHead();
                Object tail = path.getTail();
                E currentEdge = this.graph.getEdge(tail, head);
                if (currentEdge != null && (currentCost = path.cost + this.graph.getEdgeWeight(currentEdge)) < bestCycle.cost) {
                    Object cycleResult = path.clone();
                    ((LabeledPath)cycleResult).addVertex(head, this.graph.getEdgeWeight(currentEdge), this.labelMap.get(head));
                    if (!this.bestImprovement && currentCost < 0.0) {
                        return new GraphWalk<V, E>(this.graph, ((LabeledPath)cycleResult).vertices, ((LabeledPath)cycleResult).cost);
                    }
                    bestCycle = cycleResult;
                }
                for (E e : this.graph.outgoingEdgesOf(tail)) {
                    V currentVertex = this.graph.getEdgeTarget(e);
                    double edgeWeight = this.graph.getEdgeWeight(e);
                    int currentLabel = this.labelMap.get(currentVertex);
                    if (path.labels.contains(currentLabel) || !(path.cost + edgeWeight < 0.0)) continue;
                    Object newPath = path.clone();
                    ((LabeledPath)newPath).addVertex(currentVertex, edgeWeight, currentLabel);
                    if (this.checkDominatedPathsOfLengthKplus1((LabeledPath<V>)newPath, (Map<PathSetKey<V>, LabeledPath<V>>)pathsLengthKplus1) || this.checkDominatedPathsOfLengthK((LabeledPath<V>)newPath, (Map<PathSetKey<V>, LabeledPath<V>>)pathsLengthK)) continue;
                    this.updatePathIndex((Map<PathSetKey<V>, LabeledPath<V>>)pathsLengthKplus1, (LabeledPath<V>)newPath);
                }
            }
            ++k;
            pathsLengthK = pathsLengthKplus1;
            pathsLengthKplus1 = new LinkedHashMap();
        }
        return new GraphWalk<V, E>(this.graph, bestCycle.vertices, bestCycle.cost);
    }

    private boolean checkDominatedPathsOfLengthKplus1(LabeledPath<V> path, Map<PathSetKey<V>, LabeledPath<V>> pathsLengthKplus1) {
        LabeledPath<V> pathToCheck = pathsLengthKplus1.get(new PathSetKey<V>(path.getHead(), path.getTail(), path.labels));
        if (pathToCheck != null) {
            return pathToCheck.cost < path.cost;
        }
        return false;
    }

    private boolean checkDominatedPathsOfLengthK(LabeledPath<V> path, Map<PathSetKey<V>, LabeledPath<V>> pathsLengthK) {
        HashSet<Integer> modifiableLabelSet = new HashSet<Integer>(path.labels);
        for (Integer label : path.labels) {
            modifiableLabelSet.remove(label);
            LabeledPath<V> pathToCheck = pathsLengthK.get(new PathSetKey<V>(path.getHead(), path.getTail(), modifiableLabelSet));
            if (pathToCheck != null && pathToCheck.cost < path.cost) {
                return true;
            }
            modifiableLabelSet.add(label);
        }
        return false;
    }

    private void updatePathIndex(Map<PathSetKey<V>, LabeledPath<V>> paths, LabeledPath<V> path) {
        PathSetKey<V> currentKey = new PathSetKey<V>(path.getHead(), path.getTail(), path.labels);
        paths.put(currentKey, path);
    }

    private class LabeledPath<V>
    implements Cloneable {
        public ArrayList<V> vertices;
        public HashSet<Integer> labels;
        public double cost;

        public LabeledPath(ArrayList<V> vertices, double cost, HashSet<Integer> labels) {
            this.vertices = vertices;
            this.cost = cost;
            this.labels = labels;
        }

        public void addVertex(V v, double edgeCost, int label) {
            this.vertices.add(v);
            this.cost += edgeCost;
            this.labels.add(label);
        }

        public V getHead() {
            return this.vertices.get(0);
        }

        public V getTail() {
            return this.vertices.get(this.vertices.size() - 1);
        }

        public boolean isEmpty() {
            return this.vertices.isEmpty();
        }

        public LabeledPath<V> clone() {
            try {
                LabeledPath newLabeledPath = (LabeledPath)TypeUtil.uncheckedCast(super.clone());
                newLabeledPath.vertices = (ArrayList)TypeUtil.uncheckedCast(this.vertices.clone());
                newLabeledPath.labels = (HashSet)TypeUtil.uncheckedCast(this.labels.clone());
                newLabeledPath.cost = this.cost;
                return newLabeledPath;
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

    private class PathSetKey<V> {
        private V head;
        private V tail;
        private Set<Integer> labels;

        private PathSetKey(V head, V tail, Set<Integer> labels) {
            this.head = head;
            this.tail = tail;
            this.labels = labels;
        }

        public int hashCode() {
            return Objects.hash(this.head, this.tail, this.labels);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PathSetKey)) {
                return false;
            }
            PathSetKey other = (PathSetKey)o;
            return Objects.equals(this.head, other.head) && Objects.equals(this.tail, other.tail) && Objects.equals(this.labels, other.labels);
        }
    }
}

