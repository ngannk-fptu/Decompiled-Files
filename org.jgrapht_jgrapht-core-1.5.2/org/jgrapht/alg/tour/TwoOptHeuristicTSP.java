/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.HamiltonianCycleAlgorithm;
import org.jgrapht.alg.interfaces.HamiltonianCycleImprovementAlgorithm;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.alg.tour.RandomTourTSP;
import org.jgrapht.util.ArrayUtil;
import org.jgrapht.util.VertexToIntegerMapping;

public class TwoOptHeuristicTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E>
implements HamiltonianCycleImprovementAlgorithm<V, E> {
    private final int passes;
    private final HamiltonianCycleAlgorithm<V, E> initializer;
    private final double minCostImprovement;
    private Graph<V, E> graph;
    private int n;
    private double[][] dist;
    private Map<V, Integer> index;
    private List<V> revIndex;

    public TwoOptHeuristicTSP() {
        this(1, new Random());
    }

    public TwoOptHeuristicTSP(int passes) {
        this(passes, new Random());
    }

    public TwoOptHeuristicTSP(int passes, long seed) {
        this(passes, new Random(seed));
    }

    public TwoOptHeuristicTSP(int passes, Random rng) {
        this(passes, new RandomTourTSP(rng));
    }

    public TwoOptHeuristicTSP(int passes, Random rng, double minCostImprovement) {
        this(passes, new RandomTourTSP(rng), minCostImprovement);
    }

    public TwoOptHeuristicTSP(HamiltonianCycleAlgorithm<V, E> initializer) {
        this(1, initializer);
    }

    public TwoOptHeuristicTSP(int passes, HamiltonianCycleAlgorithm<V, E> initializer) {
        this(passes, initializer, 1.0E-8);
    }

    public TwoOptHeuristicTSP(int passes, HamiltonianCycleAlgorithm<V, E> initializer, double minCostImprovement) {
        if (passes < 1) {
            throw new IllegalArgumentException("passes must be at least one");
        }
        this.passes = passes;
        this.initializer = Objects.requireNonNull(initializer, "Initial solver algorithm cannot be null");
        this.minCostImprovement = Math.abs(minCostImprovement);
    }

    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.checkGraph(graph);
        if (graph.vertexSet().size() == 1) {
            return this.getSingletonTour(graph);
        }
        this.init(graph);
        GraphPath<V, E> best = this.tourToPath(this.improve(this.createInitialTour()));
        for (int i = 1; i < this.passes; ++i) {
            GraphPath<V, E> other = this.tourToPath(this.improve(this.createInitialTour()));
            if (!(other.getWeight() < best.getWeight())) continue;
            best = other;
        }
        return best;
    }

    @Override
    public GraphPath<V, E> improveTour(GraphPath<V, E> tour) {
        this.init(tour.getGraph());
        return this.tourToPath(this.improve(this.pathToTour(tour)));
    }

    private void init(Graph<V, E> graph) {
        this.graph = graph;
        this.n = graph.vertexSet().size();
        this.dist = new double[this.n][this.n];
        VertexToIntegerMapping<V> vertex2index = new VertexToIntegerMapping<V>(graph.vertexSet());
        this.index = vertex2index.getVertexMap();
        this.revIndex = vertex2index.getIndexList();
        for (E e : graph.edgeSet()) {
            double weight;
            V s = graph.getEdgeSource(e);
            int si = this.index.get(s);
            V t = graph.getEdgeTarget(e);
            int ti = this.index.get(t);
            this.dist[si][ti] = weight = graph.getEdgeWeight(e);
            this.dist[ti][si] = weight;
        }
    }

    private int[] createInitialTour() {
        return this.pathToTour(this.initializer.getTour(this.graph));
    }

    private int[] improve(int[] tour) {
        while (true) {
            double minChange = -this.minCostImprovement;
            int mini = -1;
            int minj = -1;
            for (int i = 0; i < this.n - 2; ++i) {
                for (int j = i + 2; j < this.n; ++j) {
                    int ci = tour[i];
                    int cj = tour[j];
                    int ci1 = tour[i + 1];
                    int cj1 = tour[j + 1];
                    double change = this.dist[ci][cj] + this.dist[ci1][cj1] - this.dist[ci][ci1] - this.dist[cj][cj1];
                    if (!(change < minChange)) continue;
                    minChange = change;
                    mini = i;
                    minj = j;
                }
            }
            if (mini == -1 || minj == -1) break;
            ArrayUtil.reverse(tour, mini + 1, minj);
        }
        return tour;
    }

    private GraphPath<V, E> tourToPath(int[] tour) {
        ArrayList<V> tourVertices = new ArrayList<V>(this.n + 1);
        for (int vi : tour) {
            V v = this.revIndex.get(vi);
            tourVertices.add(v);
        }
        return this.closedVertexListToTour(tourVertices, this.graph);
    }

    private int[] pathToTour(GraphPath<V, E> path) {
        boolean[] visited = new boolean[this.n];
        List<V> vertexList = path.getVertexList();
        if (vertexList.size() != this.n + 1) {
            throw new IllegalArgumentException("Not a valid tour");
        }
        int[] tour = new int[this.n + 1];
        for (int i = 0; i < this.n; ++i) {
            int vi = this.index.get(vertexList.get(i));
            if (visited[vi]) {
                throw new IllegalArgumentException("Not a valid tour");
            }
            visited[vi] = true;
            tour[i] = vi;
        }
        tour[this.n] = tour[0];
        return tour;
    }
}

