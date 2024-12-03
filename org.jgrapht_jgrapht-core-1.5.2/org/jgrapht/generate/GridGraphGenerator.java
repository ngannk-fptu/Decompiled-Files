/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class GridGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    public static final String CORNER_VERTEX = "Corner Vertex";
    private final int rows;
    private final int cols;

    public GridGraphGenerator(int rows, int cols) {
        if (rows < 2) {
            throw new IllegalArgumentException("illegal number of rows (" + rows + "). there must be at least two.");
        }
        if (cols < 2) {
            throw new IllegalArgumentException("illegal number of columns (" + cols + "). there must be at least two.");
        }
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int i;
        ArrayList<V> vertices = new ArrayList<V>(this.rows * this.cols);
        int cornerCtr = 0;
        for (i = 0; i < this.rows * this.cols; ++i) {
            boolean isCorner;
            V vertex = target.addVertex();
            vertices.add(vertex);
            boolean bl = isCorner = i == 0 || i == this.cols - 1 || i == this.cols * (this.rows - 1) || i == this.rows * this.cols - 1;
            if (!isCorner || resultMap == null) continue;
            resultMap.put("Corner Vertex " + ++cornerCtr, vertex);
        }
        for (i = 1; i <= vertices.size(); ++i) {
            for (int j = 1; j <= vertices.size(); ++j) {
                if ((i % this.cols <= 0 || i + 1 != j) && i + this.cols != j) continue;
                target.addEdge(vertices.get(i - 1), vertices.get(j - 1));
                target.addEdge(vertices.get(j - 1), vertices.get(i - 1));
            }
        }
    }
}

