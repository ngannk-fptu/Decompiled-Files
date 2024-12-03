/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

public final class PageRank<V, E>
implements VertexScoringAlgorithm<V, Double> {
    public static final int MAX_ITERATIONS_DEFAULT = 100;
    public static final double TOLERANCE_DEFAULT = 1.0E-4;
    public static final double DAMPING_FACTOR_DEFAULT = 0.85;
    private final Graph<V, E> graph;
    private final double dampingFactor;
    private final int maxIterations;
    private final double tolerance;
    private Map<V, Double> scores;

    public PageRank(Graph<V, E> graph) {
        this(graph, 0.85, 100, 1.0E-4);
    }

    public PageRank(Graph<V, E> graph, double dampingFactor) {
        this(graph, dampingFactor, 100, 1.0E-4);
    }

    public PageRank(Graph<V, E> graph, double dampingFactor, int maxIterations) {
        this(graph, dampingFactor, maxIterations, 1.0E-4);
    }

    public PageRank(Graph<V, E> graph, double dampingFactor, int maxIterations, double tolerance) {
        this.graph = graph;
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("Maximum iterations must be positive");
        }
        this.maxIterations = maxIterations;
        if (dampingFactor < 0.0 || dampingFactor > 1.0) {
            throw new IllegalArgumentException("Damping factor not valid");
        }
        this.dampingFactor = dampingFactor;
        if (tolerance <= 0.0) {
            throw new IllegalArgumentException("Tolerance not valid, must be positive");
        }
        this.tolerance = tolerance;
    }

    @Override
    public Map<V, Double> getScores() {
        if (this.scores == null) {
            this.scores = Collections.unmodifiableMap(new Algorithm().getScores());
        }
        return this.scores;
    }

    @Override
    public Double getVertexScore(V v) {
        if (!this.graph.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        return this.getScores().get(v);
    }

    private class Algorithm {
        private int totalVertices;
        private boolean isWeighted;
        private Map<V, Integer> vertexIndexMap;
        private V[] vertexMap;
        private double[] weightSum;
        private double[] curScore;
        private double[] nextScore;
        private int[] outDegree;
        private ArrayList<int[]> adjList;
        private ArrayList<double[]> weightsList;

        public Algorithm() {
            int[] inNeighbors;
            Object v;
            this.totalVertices = PageRank.this.graph.vertexSet().size();
            this.isWeighted = PageRank.this.graph.getType().isWeighted();
            this.curScore = new double[this.totalVertices];
            this.nextScore = new double[this.totalVertices];
            this.vertexIndexMap = new HashMap();
            this.vertexMap = new Object[this.totalVertices];
            this.outDegree = new int[this.totalVertices];
            this.adjList = new ArrayList(this.totalVertices);
            double initScore = 1.0 / (double)this.totalVertices;
            int i = 0;
            for (Object v2 : PageRank.this.graph.vertexSet()) {
                this.vertexIndexMap.put((Integer)v2, i);
                this.vertexMap[i] = v2;
                this.outDegree[i] = PageRank.this.graph.outDegreeOf(v2);
                this.curScore[i] = initScore;
                ++i;
            }
            if (this.isWeighted) {
                this.weightSum = new double[this.totalVertices];
                this.weightsList = new ArrayList(this.totalVertices);
                for (i = 0; i < this.totalVertices; ++i) {
                    v = this.vertexMap[i];
                    inNeighbors = new int[PageRank.this.graph.inDegreeOf(v)];
                    double[] edgeWeights = new double[PageRank.this.graph.inDegreeOf(v)];
                    int j = 0;
                    for (Object e : PageRank.this.graph.incomingEdgesOf(v)) {
                        Object w = Graphs.getOppositeVertex(PageRank.this.graph, e, v);
                        Integer mappedVertexId = this.vertexIndexMap.get(w);
                        inNeighbors[j] = mappedVertexId;
                        double edgeWeight = PageRank.this.graph.getEdgeWeight(e);
                        int n = j++;
                        edgeWeights[n] = edgeWeights[n] + edgeWeight;
                        int n2 = mappedVertexId;
                        this.weightSum[n2] = this.weightSum[n2] + edgeWeight;
                    }
                    this.weightsList.add(edgeWeights);
                    this.adjList.add(inNeighbors);
                }
            } else {
                for (i = 0; i < this.totalVertices; ++i) {
                    v = this.vertexMap[i];
                    inNeighbors = new int[PageRank.this.graph.inDegreeOf(v)];
                    int j = 0;
                    for (Object e : PageRank.this.graph.incomingEdgesOf(v)) {
                        Object w = Graphs.getOppositeVertex(PageRank.this.graph, e, v);
                        inNeighbors[j++] = this.vertexIndexMap.get(w);
                    }
                    this.adjList.add(inNeighbors);
                }
            }
        }

        public Map<V, Double> getScores() {
            if (this.isWeighted) {
                this.runWeighted();
            } else {
                this.run();
            }
            HashMap scores = new HashMap();
            for (int i = 0; i < this.totalVertices; ++i) {
                Object v = this.vertexMap[i];
                scores.put(v, this.curScore[i]);
            }
            return scores;
        }

        private void run() {
            double maxChange = PageRank.this.tolerance;
            for (int iterations = PageRank.this.maxIterations; iterations > 0 && maxChange >= PageRank.this.tolerance; --iterations) {
                double r = this.teleProp();
                maxChange = 0.0;
                for (int i = 0; i < this.totalVertices; ++i) {
                    double contribution = 0.0;
                    for (int w : this.adjList.get(i)) {
                        contribution += PageRank.this.dampingFactor * this.curScore[w] / (double)this.outDegree[w];
                    }
                    double vOldValue = this.curScore[i];
                    double vNewValue = r + contribution;
                    maxChange = Math.max(maxChange, Math.abs(vNewValue - vOldValue));
                    this.nextScore[i] = vNewValue;
                }
                this.swapScores();
            }
        }

        private void runWeighted() {
            double maxChange = PageRank.this.tolerance;
            for (int iterations = PageRank.this.maxIterations; iterations > 0 && maxChange >= PageRank.this.tolerance; --iterations) {
                double r = this.teleProp();
                maxChange = 0.0;
                for (int i = 0; i < this.totalVertices; ++i) {
                    double contribution = 0.0;
                    int[] neighbors = this.adjList.get(i);
                    double[] weights = this.weightsList.get(i);
                    int getLength = neighbors.length;
                    for (int j = 0; j < getLength; ++j) {
                        int w = neighbors[j];
                        contribution += PageRank.this.dampingFactor * this.curScore[w] * weights[j] / this.weightSum[w];
                    }
                    double vOldValue = this.curScore[i];
                    double vNewValue = r + contribution;
                    maxChange = Math.max(maxChange, Math.abs(vNewValue - vOldValue));
                    this.nextScore[i] = vNewValue;
                }
                this.swapScores();
            }
        }

        private double teleProp() {
            double r = 0.0;
            for (int i = 0; i < this.totalVertices; ++i) {
                if (this.outDegree[i] > 0) {
                    r += (1.0 - PageRank.this.dampingFactor) * this.curScore[i];
                    continue;
                }
                r += this.curScore[i];
            }
            return r /= (double)this.totalVertices;
        }

        private void swapScores() {
            double[] tmp = this.curScore;
            this.curScore = this.nextScore;
            this.nextScore = tmp;
        }
    }
}

