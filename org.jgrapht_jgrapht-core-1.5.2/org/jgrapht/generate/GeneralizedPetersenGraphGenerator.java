/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class GeneralizedPetersenGraphGenerator<V, E>
implements GraphGenerator<V, E, List<V>> {
    private final int n;
    private final int k;
    public static final String STAR = "star";
    public static final String REGULAR = "regular";

    public GeneralizedPetersenGraphGenerator(int n, int k) {
        if (n < 3) {
            throw new IllegalArgumentException("n must be larger or equal than 3");
        }
        if (k < 1 || (double)k > Math.floor((double)(n - 1) / 2.0)) {
            throw new IllegalArgumentException("k must be in the range [1, floor((n-1)/2.0)]");
        }
        this.n = n;
        this.k = k;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, List<V>> resultMap) {
        int i;
        ArrayList<V> verticesU = new ArrayList<V>(this.n);
        ArrayList<V> verticesV = new ArrayList<V>(this.n);
        for (i = 0; i < this.n; ++i) {
            verticesU.add(target.addVertex());
            verticesV.add(target.addVertex());
        }
        for (i = 0; i < this.n; ++i) {
            target.addEdge(verticesU.get(i), verticesU.get((i + 1) % this.n));
            target.addEdge(verticesU.get(i), verticesV.get(i));
            target.addEdge(verticesV.get(i), verticesV.get((i + this.k) % this.n));
        }
        if (resultMap != null) {
            resultMap.put(REGULAR, verticesU);
            resultMap.put(STAR, verticesV);
        }
    }
}

