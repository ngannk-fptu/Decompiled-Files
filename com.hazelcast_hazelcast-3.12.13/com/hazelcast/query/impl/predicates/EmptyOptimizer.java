/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.QueryOptimizer;

public class EmptyOptimizer
implements QueryOptimizer {
    @Override
    public <K, V> Predicate<K, V> optimize(Predicate<K, V> predicate, Indexes indexes) {
        return predicate;
    }
}

