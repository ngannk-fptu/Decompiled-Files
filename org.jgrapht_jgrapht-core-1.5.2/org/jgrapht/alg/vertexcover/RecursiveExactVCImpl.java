/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.vertexcover;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.alg.vertexcover.ClarksonTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;

public class RecursiveExactVCImpl<V, E>
implements VertexCoverAlgorithm<V> {
    private Graph<V, E> graph;
    private int n;
    private NeighborCache<V, E> neighborCache;
    private Map<BitSet, BitSetCover> memo;
    private List<V> vertices;
    private Map<V, Integer> vertexIDDictionary;
    private double upperBoundOnVertexCoverWeight;
    private boolean weighted;
    private Map<V, Double> vertexWeightMap = null;

    public RecursiveExactVCImpl(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
        this.vertexWeightMap = graph.vertexSet().stream().collect(Collectors.toMap(Function.identity(), vertex -> 1.0));
        this.weighted = false;
    }

    public RecursiveExactVCImpl(Graph<V, E> graph, Map<V, Double> vertexWeightMap) {
        this.graph = GraphTests.requireUndirected(graph);
        this.vertexWeightMap = Objects.requireNonNull(vertexWeightMap);
        this.weighted = true;
    }

    @Override
    public VertexCoverAlgorithm.VertexCover<V> getVertexCover() {
        this.graph = GraphTests.requireUndirected(this.graph);
        this.memo = new HashMap<BitSet, BitSetCover>();
        this.vertices = new ArrayList<V>(this.graph.vertexSet());
        this.neighborCache = new NeighborCache<V, E>(this.graph);
        this.vertexIDDictionary = new HashMap<V, Integer>();
        this.n = this.vertices.size();
        this.vertices.sort(Comparator.comparingDouble(v -> this.vertexWeightMap.get(v) / (double)this.graph.degreeOf(v)));
        for (int i = 0; i < this.vertices.size(); ++i) {
            this.vertexIDDictionary.put((Integer)this.vertices.get(i), i);
        }
        this.upperBoundOnVertexCoverWeight = this.calculateUpperBound();
        BitSetCover vertexCover = this.calculateCoverRecursively(0, new BitSet(this.n), 0.0);
        LinkedHashSet<V> verticesInCover = new LinkedHashSet<V>();
        int i = vertexCover.bitSetCover.nextSetBit(0);
        while (i >= 0 && i < this.n) {
            verticesInCover.add(this.vertices.get(i));
            i = vertexCover.bitSetCover.nextSetBit(i + 1);
        }
        return new VertexCoverAlgorithm.VertexCoverImpl(verticesInCover, vertexCover.weight);
    }

    private BitSetCover calculateCoverRecursively(int indexNextCandidate, BitSet visited, double accumulatedWeight) {
        if (this.memo.containsKey(visited)) {
            return this.memo.get(visited).copy();
        }
        int indexNextVertex = -1;
        Set neighbors = Collections.emptySet();
        int index = visited.nextClearBit(indexNextCandidate);
        while (index >= 0 && index < this.n) {
            neighbors = new LinkedHashSet<V>(this.neighborCache.neighborsOf(this.vertices.get(index)));
            Iterator it = neighbors.iterator();
            while (it.hasNext()) {
                if (!visited.get(this.vertexIDDictionary.get(it.next()))) continue;
                it.remove();
            }
            if (!neighbors.isEmpty()) {
                indexNextVertex = index;
                break;
            }
            index = visited.nextClearBit(index + 1);
        }
        if (indexNextVertex == -1) {
            BitSetCover vertexCover = new BitSetCover(this.n, 0);
            if (accumulatedWeight <= this.upperBoundOnVertexCoverWeight) {
                this.upperBoundOnVertexCoverWeight = accumulatedWeight - 1.0;
            }
            return vertexCover;
        }
        if (accumulatedWeight >= this.upperBoundOnVertexCoverWeight) {
            return new BitSetCover(this.n, this.n);
        }
        BitSet visitedRightBranch = (BitSet)visited.clone();
        visitedRightBranch.set(indexNextVertex);
        for (Object v : neighbors) {
            visitedRightBranch.set(this.vertexIDDictionary.get(v));
        }
        double weight = this.getWeight(neighbors);
        BitSetCover rightCover = this.calculateCoverRecursively(indexNextVertex + 1, visitedRightBranch, accumulatedWeight + weight);
        List<Integer> neighborsIndices = neighbors.stream().map(this.vertexIDDictionary::get).collect(Collectors.toList());
        rightCover.addAllVertices(neighborsIndices, weight);
        BitSet visitedLeftBranch = (BitSet)visited.clone();
        visitedLeftBranch.set(indexNextVertex);
        weight = this.vertexWeightMap.get(this.vertices.get(indexNextVertex));
        BitSetCover leftCover = this.calculateCoverRecursively(indexNextVertex + 1, visitedLeftBranch, accumulatedWeight + weight);
        leftCover.addVertex(indexNextVertex, weight);
        if (leftCover.weight <= rightCover.weight) {
            this.memo.put(visited, leftCover.copy());
            return leftCover;
        }
        this.memo.put(visited, rightCover.copy());
        return rightCover;
    }

    private double getWeight(Collection<V> vertices) {
        if (this.weighted) {
            return vertices.stream().map(this.vertexWeightMap::get).reduce(0.0, Double::sum);
        }
        return vertices.size();
    }

    private double calculateUpperBound() {
        return Math.min(new GreedyVCImpl<V, E>(this.graph, this.vertexWeightMap).getVertexCover().getWeight(), new ClarksonTwoApproxVCImpl<V, E>(this.graph, this.vertexWeightMap).getVertexCover().getWeight());
    }

    protected class BitSetCover {
        protected BitSet bitSetCover;
        protected double weight;

        protected BitSetCover(int size, int initialWeight) {
            this.bitSetCover = new BitSet(size);
            this.weight = initialWeight;
        }

        protected BitSetCover(BitSetCover vertexCover) {
            this.bitSetCover = (BitSet)vertexCover.bitSetCover.clone();
            this.weight = vertexCover.weight;
        }

        protected BitSetCover copy() {
            return new BitSetCover(this);
        }

        protected void addVertex(int vertexIndex, double weight) {
            this.bitSetCover.set(vertexIndex);
            this.weight += weight;
        }

        protected void addAllVertices(List<Integer> vertexIndices, double totalWeight) {
            vertexIndices.forEach(this.bitSetCover::set);
            this.weight += totalWeight;
        }
    }
}

