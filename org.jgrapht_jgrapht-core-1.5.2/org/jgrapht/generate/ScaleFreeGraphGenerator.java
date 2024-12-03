/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class ScaleFreeGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final int size;
    private final Random rng;

    public ScaleFreeGraphGenerator(int size) {
        this(size, new Random());
    }

    public ScaleFreeGraphGenerator(int size, long seed) {
        this(size, new Random(seed));
    }

    public ScaleFreeGraphGenerator(int size, Random rng) {
        if (size < 0) {
            throw new IllegalArgumentException("invalid size: " + size + " (must be non-negative)");
        }
        this.size = size;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        ArrayList<V> vertexList = new ArrayList<V>();
        ArrayList<Integer> degrees = new ArrayList<Integer>();
        int degreeSum = 0;
        for (int i = 0; i < this.size; ++i) {
            V newVertex = target.addVertex();
            int newDegree = 0;
            while (newDegree == 0 && i != 0) {
                for (int j = 0; j < vertexList.size(); ++j) {
                    if (degreeSum != 0 && this.rng.nextInt(degreeSum) >= (Integer)degrees.get(j)) continue;
                    degrees.set(j, (Integer)degrees.get(j) + 1);
                    ++newDegree;
                    degreeSum += 2;
                    if (this.rng.nextBoolean()) {
                        target.addEdge(vertexList.get(j), newVertex);
                        continue;
                    }
                    target.addEdge(newVertex, vertexList.get(j));
                }
            }
            vertexList.add(newVertex);
            degrees.add(newDegree);
        }
    }
}

