/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.alg.util.Pair;

public interface LowestCommonAncestorAlgorithm<V> {
    public V getLCA(V var1, V var2);

    default public List<V> getBatchLCA(List<Pair<V, V>> queries) {
        return queries.stream().map(p -> this.getLCA(p.getFirst(), p.getSecond())).collect(Collectors.toList());
    }

    public Set<V> getLCASet(V var1, V var2);

    default public List<Set<V>> getBatchLCASet(List<Pair<V, V>> queries) {
        return queries.stream().map(p -> this.getLCASet(p.getFirst(), p.getSecond())).collect(Collectors.toList());
    }
}

