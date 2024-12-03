/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.LinkedList;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class HyperCubeGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private int dim;

    public HyperCubeGraphGenerator(int dim) {
        this.dim = dim;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        int i;
        int order = (int)Math.pow(2.0, this.dim);
        LinkedList<V> vertices = new LinkedList<V>();
        for (i = 0; i < order; ++i) {
            V newVertex = target.addVertex();
            vertices.add(newVertex);
            if (resultMap == null) continue;
            StringBuilder s = new StringBuilder(Integer.toBinaryString(i));
            while (s.length() < this.dim) {
                s.insert(0, "0");
            }
            resultMap.put(s.toString(), newVertex);
        }
        for (i = 0; i < order; ++i) {
            block3: for (int j = i + 1; j < order; ++j) {
                for (int z = 0; z < this.dim; ++z) {
                    if ((j ^ i) != 1 << z) continue;
                    target.addEdge(vertices.get(i), vertices.get(j));
                    continue block3;
                }
            }
        }
    }
}

