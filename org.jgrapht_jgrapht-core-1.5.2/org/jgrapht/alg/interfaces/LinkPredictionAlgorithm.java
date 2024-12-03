/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.ArrayList;
import java.util.List;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;

public interface LinkPredictionAlgorithm<V, E> {
    default public List<Triple<V, V, Double>> predict(List<Pair<V, V>> queries) {
        ArrayList<Triple<V, V, Double>> result = new ArrayList<Triple<V, V, Double>>();
        for (Pair<V, V> q : queries) {
            result.add(Triple.of(q.getFirst(), q.getSecond(), this.predict(q.getFirst(), q.getSecond())));
        }
        return result;
    }

    public double predict(V var1, V var2);
}

