/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class SimpleWeightedBipartiteGraphMatrixGenerator<V, E>
implements GraphGenerator<V, E, V> {
    protected List<V> first;
    protected List<V> second;
    protected double[][] weights;

    public SimpleWeightedBipartiteGraphMatrixGenerator<V, E> first(List<? extends V> first) {
        this.first = new ArrayList<V>(first);
        return this;
    }

    public SimpleWeightedBipartiteGraphMatrixGenerator<V, E> second(List<? extends V> second) {
        this.second = new ArrayList<V>(second);
        return this;
    }

    public SimpleWeightedBipartiteGraphMatrixGenerator<V, E> weights(double[][] weights) {
        this.weights = weights;
        return this;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.weights == null) {
            throw new IllegalArgumentException("Graph may not be constructed without weight-matrix specified");
        }
        if (this.first == null || this.second == null) {
            throw new IllegalArgumentException("Graph may not be constructed without either of vertex-set partitions specified");
        }
        assert (this.second.size() == this.weights.length);
        for (V vertex : this.first) {
            target.addVertex(vertex);
        }
        for (V vertex : this.second) {
            target.addVertex(vertex);
        }
        for (int i = 0; i < this.first.size(); ++i) {
            assert (this.first.size() == this.weights[i].length);
            for (int j = 0; j < this.second.size(); ++j) {
                target.setEdgeWeight(target.addEdge(this.first.get(i), this.second.get(j)), this.weights[i][j]);
            }
        }
    }
}

