/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

public class KuhnMunkresMinimalWeightBipartitePerfectMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private Set<? extends V> partition1;
    private Set<? extends V> partition2;

    public KuhnMunkresMinimalWeightBipartitePerfectMatching(Graph<V, E> graph, Set<? extends V> partition1, Set<? extends V> partition2) {
        if (graph == null) {
            throw new IllegalArgumentException("Input graph cannot be null");
        }
        this.graph = graph;
        if (partition1 == null) {
            throw new IllegalArgumentException("Partition 1 cannot be null");
        }
        this.partition1 = partition1;
        if (partition2 == null) {
            throw new IllegalArgumentException("Partition 2 cannot be null");
        }
        this.partition2 = partition2;
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        if (this.partition1.size() != this.partition2.size()) {
            throw new IllegalArgumentException("Graph supplied isn't complete bipartite with equally sized partitions!");
        }
        if (!GraphTests.isBipartitePartition(this.graph, this.partition1, this.partition2)) {
            throw new IllegalArgumentException("Invalid bipartite partition provided");
        }
        int partition = this.partition1.size();
        int edges = this.graph.edgeSet().size();
        if (edges != partition * partition) {
            throw new IllegalArgumentException("Graph supplied isn't complete bipartite with equally sized partitions!");
        }
        if (!GraphTests.isSimple(this.graph)) {
            throw new IllegalArgumentException("Only simple graphs supported");
        }
        ArrayList<V> firstPartition = new ArrayList<V>(this.partition1);
        ArrayList<V> secondPartition = new ArrayList<V>(this.partition2);
        int[] matching = this.graph.vertexSet().isEmpty() ? new int[]{} : new KuhnMunkresMatrixImplementation<V, E>(this.graph, firstPartition, secondPartition).buildMatching();
        HashSet<E> edgeSet = new HashSet<E>();
        double weight = 0.0;
        for (int i = 0; i < matching.length; ++i) {
            E e = this.graph.getEdge(firstPartition.get(i), secondPartition.get(matching[i]));
            weight += this.graph.getEdgeWeight(e);
            edgeSet.add(e);
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, edgeSet, weight);
    }

    static class KuhnMunkresMatrixImplementation<V, E> {
        private double[][] costMatrix;
        private double[][] excessMatrix;
        boolean[] rowsCovered;
        boolean[] columnsCovered;
        private int[] columnMatched;
        private int[] rowMatched;

        public KuhnMunkresMatrixImplementation(Graph<V, E> g, List<? extends V> s, List<? extends V> t) {
            int partition = s.size();
            this.costMatrix = new double[partition][];
            for (int i = 0; i < s.size(); ++i) {
                V source = s.get(i);
                this.costMatrix[i] = new double[partition];
                for (int j = 0; j < t.size(); ++j) {
                    V target = t.get(j);
                    if (source.equals(target)) continue;
                    this.costMatrix[i][j] = g.getEdgeWeight(g.getEdge(source, target));
                }
            }
        }

        protected int[] buildMatching() {
            int height = this.costMatrix.length;
            int width = this.costMatrix[0].length;
            this.excessMatrix = this.makeExcessMatrix();
            this.rowsCovered = new boolean[height];
            this.columnsCovered = new boolean[width];
            this.columnMatched = new int[height];
            this.rowMatched = new int[width];
            Arrays.fill(this.columnMatched, -1);
            Arrays.fill(this.rowMatched, -1);
            while (this.buildMaximalMatching() < width) {
                this.buildVertexCoverage();
                this.extendEqualityGraph();
            }
            return Arrays.copyOf(this.columnMatched, height);
        }

        double[][] makeExcessMatrix() {
            int i;
            double[][] excessMatrix = new double[this.costMatrix.length][];
            for (i = 0; i < excessMatrix.length; ++i) {
                excessMatrix[i] = Arrays.copyOf(this.costMatrix[i], this.costMatrix[i].length);
            }
            for (i = 0; i < excessMatrix.length; ++i) {
                int j;
                double cheapestTaskCost = Double.MAX_VALUE;
                for (j = 0; j < excessMatrix[i].length; ++j) {
                    if (!(cheapestTaskCost > excessMatrix[i][j])) continue;
                    cheapestTaskCost = excessMatrix[i][j];
                }
                j = 0;
                while (j < excessMatrix[i].length) {
                    double[] dArray = excessMatrix[i];
                    int n = j++;
                    dArray[n] = dArray[n] - cheapestTaskCost;
                }
            }
            for (int j = 0; j < excessMatrix[0].length; ++j) {
                int i2;
                double cheapestWorkerCost = Double.MAX_VALUE;
                for (i2 = 0; i2 < excessMatrix.length; ++i2) {
                    if (!(cheapestWorkerCost > excessMatrix[i2][j])) continue;
                    cheapestWorkerCost = excessMatrix[i2][j];
                }
                for (i2 = 0; i2 < excessMatrix.length; ++i2) {
                    double[] dArray = excessMatrix[i2];
                    int n = j;
                    dArray[n] = dArray[n] - cheapestWorkerCost;
                }
            }
            return excessMatrix;
        }

        int buildMaximalMatching() {
            int matchingSizeLowerBound = 0;
            for (int i = 0; i < this.columnMatched.length; ++i) {
                if (this.columnMatched[i] == -1) continue;
                ++matchingSizeLowerBound;
            }
            block1: for (int j = 0; j < this.excessMatrix[0].length; ++j) {
                if (this.rowMatched[j] != -1) continue;
                for (int i = 0; i < this.excessMatrix.length; ++i) {
                    if (this.excessMatrix[i][j] != 0.0 || this.columnMatched[i] != -1) continue;
                    ++matchingSizeLowerBound;
                    this.columnMatched[i] = j;
                    this.rowMatched[j] = i;
                    continue block1;
                }
            }
            if (matchingSizeLowerBound == this.excessMatrix[0].length) {
                return matchingSizeLowerBound;
            }
            boolean[] rowsVisited = new boolean[this.excessMatrix.length];
            boolean[] colsVisited = new boolean[this.excessMatrix[0].length];
            int matchingSize = 0;
            boolean extending = true;
            while (extending && matchingSize < this.excessMatrix.length) {
                int j;
                Arrays.fill(rowsVisited, false);
                Arrays.fill(colsVisited, false);
                extending = false;
                for (j = 0; j < this.excessMatrix.length; ++j) {
                    if (this.rowMatched[j] != -1 || colsVisited[j]) continue;
                    extending |= new MatchExtender(rowsVisited, colsVisited).extend(j);
                }
                matchingSize = 0;
                for (j = 0; j < this.rowMatched.length; ++j) {
                    if (this.rowMatched[j] == -1) continue;
                    ++matchingSize;
                }
            }
            return matchingSize;
        }

        void buildVertexCoverage() {
            int i;
            int j;
            Arrays.fill(this.columnsCovered, false);
            Arrays.fill(this.rowsCovered, false);
            boolean[] invertible = new boolean[this.rowsCovered.length];
            block0: for (int i2 = 0; i2 < this.excessMatrix.length; ++i2) {
                if (this.columnMatched[i2] != -1) {
                    invertible[i2] = true;
                    continue;
                }
                for (j = 0; j < this.excessMatrix[i2].length; ++j) {
                    if (Double.compare(this.excessMatrix[i2][j], 0.0) != 0) continue;
                    invertible[i2] = true;
                    this.rowsCovered[i2] = true;
                    continue block0;
                }
            }
            boolean cont = true;
            while (cont) {
                for (i = 0; i < this.excessMatrix.length; ++i) {
                    if (!this.rowsCovered[i]) continue;
                    for (int j2 = 0; j2 < this.excessMatrix[i].length; ++j2) {
                        if (Double.compare(this.excessMatrix[i][j2], 0.0) != 0 || this.columnsCovered[j2]) continue;
                        this.columnsCovered[j2] = true;
                    }
                }
                cont = false;
                for (j = 0; j < this.columnsCovered.length; ++j) {
                    if (!this.columnsCovered[j] || this.rowMatched[j] == -1 || this.rowsCovered[this.rowMatched[j]]) continue;
                    cont = true;
                    this.rowsCovered[this.rowMatched[j]] = true;
                }
            }
            for (i = 0; i < this.rowsCovered.length; ++i) {
                if (!invertible[i]) continue;
                int n = i;
                this.rowsCovered[n] = this.rowsCovered[n] ^ true;
            }
            assert (KuhnMunkresMatrixImplementation.uncovered(this.excessMatrix, this.rowsCovered, this.columnsCovered) == 0);
            assert (KuhnMunkresMatrixImplementation.minimal(this.rowMatched, this.rowsCovered, this.columnsCovered));
        }

        void extendEqualityGraph() {
            int j;
            int i;
            double minExcess = Double.MAX_VALUE;
            for (i = 0; i < this.excessMatrix.length; ++i) {
                if (this.rowsCovered[i]) continue;
                for (j = 0; j < this.excessMatrix[i].length; ++j) {
                    if (this.columnsCovered[j] || !(minExcess > this.excessMatrix[i][j])) continue;
                    minExcess = this.excessMatrix[i][j];
                }
            }
            for (i = 0; i < this.excessMatrix.length; ++i) {
                if (!this.rowsCovered[i]) continue;
                j = 0;
                while (j < this.excessMatrix[i].length) {
                    double[] dArray = this.excessMatrix[i];
                    int n = j++;
                    dArray[n] = dArray[n] + minExcess;
                }
            }
            for (int j2 = 0; j2 < this.excessMatrix[0].length; ++j2) {
                if (this.columnsCovered[j2]) continue;
                for (int i2 = 0; i2 < this.excessMatrix.length; ++i2) {
                    double[] dArray = this.excessMatrix[i2];
                    int n = j2;
                    dArray[n] = dArray[n] - minExcess;
                }
            }
        }

        private static boolean minimal(int[] match, boolean[] rowsCovered, boolean[] colsCovered) {
            int matched = 0;
            for (int i = 0; i < match.length; ++i) {
                if (match[i] == -1) continue;
                ++matched;
            }
            int covered = 0;
            for (int i = 0; i < rowsCovered.length; ++i) {
                if (rowsCovered[i]) {
                    ++covered;
                }
                if (!colsCovered[i]) continue;
                ++covered;
            }
            return matched == covered;
        }

        private static int uncovered(double[][] excessMatrix, boolean[] rowsCovered, boolean[] colsCovered) {
            int uncoveredZero = 0;
            for (int i = 0; i < excessMatrix.length; ++i) {
                if (rowsCovered[i]) continue;
                for (int j = 0; j < excessMatrix[i].length; ++j) {
                    if (colsCovered[j] || Double.compare(excessMatrix[i][j], 0.0) != 0) continue;
                    ++uncoveredZero;
                }
            }
            return uncoveredZero;
        }

        protected class MatchExtender {
            private final boolean[] rowsVisited;
            private final boolean[] colsVisited;

            private MatchExtender(boolean[] rowsVisited, boolean[] colsVisited) {
                this.rowsVisited = rowsVisited;
                this.colsVisited = colsVisited;
            }

            public boolean extend(int initialCol) {
                return this.extendMatchingEL(initialCol);
            }

            private boolean extendMatchingOL(int pathTailRow, int pathTailCol) {
                if (KuhnMunkresMatrixImplementation.this.columnMatched[pathTailRow] == -1) {
                    KuhnMunkresMatrixImplementation.this.columnMatched[pathTailRow] = pathTailCol;
                    KuhnMunkresMatrixImplementation.this.rowMatched[pathTailCol] = pathTailRow;
                    return true;
                }
                this.rowsVisited[pathTailRow] = true;
                if (this.colsVisited[KuhnMunkresMatrixImplementation.this.columnMatched[pathTailRow]]) {
                    return false;
                }
                boolean extending = this.extendMatchingEL(KuhnMunkresMatrixImplementation.this.columnMatched[pathTailRow]);
                if (extending) {
                    KuhnMunkresMatrixImplementation.this.columnMatched[pathTailRow] = pathTailCol;
                    KuhnMunkresMatrixImplementation.this.rowMatched[pathTailCol] = pathTailRow;
                }
                return extending;
            }

            private boolean extendMatchingEL(int pathTailCol) {
                this.colsVisited[pathTailCol] = true;
                for (int i = 0; i < KuhnMunkresMatrixImplementation.this.excessMatrix.length; ++i) {
                    boolean extending;
                    if (KuhnMunkresMatrixImplementation.this.excessMatrix[i][pathTailCol] != 0.0 || this.rowsVisited[i] || !(extending = this.extendMatchingOL(i, pathTailCol))) continue;
                    return true;
                }
                return false;
            }
        }
    }
}

