/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;

public class WindmillGraphsGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Mode mode;
    private final int m;
    private final int n;

    public WindmillGraphsGenerator(Mode mode, int m, int n) {
        if (m < 2) {
            throw new IllegalArgumentException("m must be larger or equal than 2");
        }
        if (n < 3) {
            throw new IllegalArgumentException("n must be larger or equal than 3");
        }
        this.mode = mode;
        this.m = m;
        this.n = n;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        V center = target.addVertex();
        ArrayList<V> sub = new ArrayList<V>(this.n);
        if (this.mode == Mode.DUTCHWINDMILL) {
            for (int i = 0; i < this.m; ++i) {
                sub.clear();
                sub.add(center);
                for (int j = 1; j < this.n; ++j) {
                    sub.add(target.addVertex());
                }
                for (int r = 0; r < sub.size(); ++r) {
                    target.addEdge(sub.get(r), sub.get((r + 1) % this.n));
                }
            }
        } else {
            for (int i = 0; i < this.m; ++i) {
                sub.clear();
                sub.add(center);
                for (int j = 1; j < this.n; ++j) {
                    sub.add(target.addVertex());
                }
                for (int r = 0; r < sub.size() - 1; ++r) {
                    for (int s = r + 1; s < sub.size(); ++s) {
                        target.addEdge(sub.get(r), sub.get(s));
                    }
                }
            }
        }
    }

    public static enum Mode {
        WINDMILL,
        DUTCHWINDMILL;

    }
}

