/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg;

import java.util.ArrayList;
import java.util.BitSet;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;

public class TransitiveReduction {
    public static final TransitiveReduction INSTANCE = new TransitiveReduction();

    private TransitiveReduction() {
    }

    static void transformToPathMatrix(BitSet[] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                if (i == j || !matrix[j].get(i)) continue;
                for (int k = 0; k < matrix.length; ++k) {
                    if (matrix[j].get(k)) continue;
                    matrix[j].set(k, matrix[i].get(k));
                }
            }
        }
    }

    static void transitiveReduction(BitSet[] pathMatrix) {
        for (int j = 0; j < pathMatrix.length; ++j) {
            for (int i = 0; i < pathMatrix.length; ++i) {
                if (!pathMatrix[i].get(j)) continue;
                for (int k = 0; k < pathMatrix.length; ++k) {
                    if (!pathMatrix[j].get(k)) continue;
                    pathMatrix[i].set(k, false);
                }
            }
        }
    }

    public <V, E> void reduce(Graph<V, E> directedGraph) {
        GraphTests.requireDirected(directedGraph, "Graph must be directed");
        ArrayList<V> vertices = new ArrayList<V>(directedGraph.vertexSet());
        int n = vertices.size();
        BitSet[] originalMatrix = new BitSet[n];
        for (int i = 0; i < originalMatrix.length; ++i) {
            originalMatrix[i] = new BitSet(n);
        }
        for (E edge : directedGraph.edgeSet()) {
            V v1 = directedGraph.getEdgeSource(edge);
            V v2 = directedGraph.getEdgeTarget(edge);
            int i1 = vertices.indexOf(v1);
            int i2 = vertices.indexOf(v2);
            originalMatrix[i1].set(i2);
        }
        BitSet[] pathMatrix = originalMatrix;
        TransitiveReduction.transformToPathMatrix(pathMatrix);
        BitSet[] transitivelyReducedMatrix = pathMatrix;
        TransitiveReduction.transitiveReduction(transitivelyReducedMatrix);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (transitivelyReducedMatrix[i].get(j)) continue;
                directedGraph.removeEdge(directedGraph.getEdge(vertices.get(i), vertices.get(j)));
            }
        }
    }
}

