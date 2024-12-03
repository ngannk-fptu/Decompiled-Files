/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.util.CollectionUtil;

public class BrownBacktrackColoring<V, E>
implements VertexColoringAlgorithm<V> {
    private final List<V> vertexList;
    private final int[][] neighbors;
    private final Map<V, Integer> indexMap;
    private int[] partialColorAssignment;
    private int[] colorCount;
    private BitSet[] allowedColors;
    private int chi;
    private int[] completeColorAssignment;
    private VertexColoringAlgorithm.Coloring<V> vertexColoring;

    public BrownBacktrackColoring(Graph<V, E> graph) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        int numVertices = graph.vertexSet().size();
        this.vertexList = new ArrayList<V>(numVertices);
        this.neighbors = new int[numVertices][];
        this.indexMap = CollectionUtil.newHashMapWithExpectedSize(numVertices);
        for (V vertex : graph.vertexSet()) {
            this.neighbors[this.vertexList.size()] = new int[graph.edgesOf(vertex).size()];
            this.indexMap.put((Integer)vertex, this.vertexList.size());
            this.vertexList.add(vertex);
        }
        for (int i = 0; i < numVertices; ++i) {
            int nbIndex = 0;
            V vertex = this.vertexList.get(i);
            for (E e : graph.edgesOf(vertex)) {
                this.neighbors[i][nbIndex++] = this.indexMap.get(Graphs.getOppositeVertex(graph, e, vertex));
            }
        }
    }

    private void recursiveColor(int pos) {
        int i;
        this.colorCount[pos] = this.colorCount[pos - 1];
        this.allowedColors[pos].set(0, this.colorCount[pos] + 1);
        for (i = 0; i < this.neighbors[pos].length; ++i) {
            int nb = this.neighbors[pos][i];
            if (this.partialColorAssignment[nb] <= 0) continue;
            this.allowedColors[pos].clear(this.partialColorAssignment[nb]);
        }
        for (i = 1; i <= this.colorCount[pos] && this.colorCount[pos] < this.chi; ++i) {
            if (!this.allowedColors[pos].get(i)) continue;
            this.partialColorAssignment[pos] = i;
            if (pos < this.neighbors.length - 1) {
                this.recursiveColor(pos + 1);
                continue;
            }
            this.chi = this.colorCount[pos];
            System.arraycopy(this.partialColorAssignment, 0, this.completeColorAssignment, 0, this.partialColorAssignment.length);
        }
        if (this.colorCount[pos] + 1 < this.chi) {
            int n = pos;
            this.colorCount[n] = this.colorCount[n] + 1;
            this.partialColorAssignment[pos] = this.colorCount[pos];
            if (pos < this.neighbors.length - 1) {
                this.recursiveColor(pos + 1);
            } else {
                this.chi = this.colorCount[pos];
                System.arraycopy(this.partialColorAssignment, 0, this.completeColorAssignment, 0, this.partialColorAssignment.length);
            }
        }
        this.partialColorAssignment[pos] = 0;
    }

    private void lazyComputeColoring() {
        if (this.vertexColoring != null) {
            return;
        }
        this.chi = this.neighbors.length + 1;
        this.partialColorAssignment = new int[this.neighbors.length];
        this.completeColorAssignment = new int[this.neighbors.length];
        this.partialColorAssignment[0] = 1;
        this.colorCount = new int[this.neighbors.length];
        this.colorCount[0] = 1;
        this.allowedColors = new BitSet[this.neighbors.length];
        for (int i = 0; i < this.neighbors.length; ++i) {
            this.allowedColors[i] = new BitSet(1);
        }
        this.recursiveColor(1);
        LinkedHashMap<V, Integer> colorMap = new LinkedHashMap<V, Integer>();
        for (int i = 0; i < this.vertexList.size(); ++i) {
            colorMap.put(this.vertexList.get(i), this.completeColorAssignment[i]);
        }
        this.vertexColoring = new VertexColoringAlgorithm.ColoringImpl(colorMap, this.chi);
    }

    public int getChromaticNumber() {
        this.lazyComputeColoring();
        return this.vertexColoring.getNumberColors();
    }

    @Override
    public VertexColoringAlgorithm.Coloring<V> getColoring() {
        this.lazyComputeColoring();
        return this.vertexColoring;
    }
}

