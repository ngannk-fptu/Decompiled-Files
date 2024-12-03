/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class SimpleWeightedGraphMatrixGenerator<V, E>
implements GraphGenerator<V, E, V> {
    protected List<V> vertices;
    protected double[][] weights;

    public SimpleWeightedGraphMatrixGenerator<V, E> vertices(List<V> vertices) {
        this.vertices = vertices;
        return this;
    }

    public SimpleWeightedGraphMatrixGenerator<V, E> weights(double[][] weights) {
        this.weights = weights;
        return this;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.weights == null) {
            throw new IllegalArgumentException("Graph may not be constructed without weight-matrix specified");
        }
        if (this.vertices == null) {
            throw new IllegalArgumentException("Graph may not be constructed without vertex-set specified");
        }
        assert (this.vertices.size() == this.weights.length);
        for (V vertex : this.vertices) {
            target.addVertex(vertex);
        }
        for (int i = 0; i < this.vertices.size(); ++i) {
            assert (this.vertices.size() == this.weights[i].length);
            for (int j = 0; j < this.vertices.size(); ++j) {
                if (i == j) continue;
                target.setEdgeWeight(target.addEdge(this.vertices.get(i), this.vertices.get(j)), this.weights[i][j]);
            }
        }
    }
}

