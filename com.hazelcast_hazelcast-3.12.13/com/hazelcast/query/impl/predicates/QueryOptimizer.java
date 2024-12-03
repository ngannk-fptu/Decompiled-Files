/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;

public interface QueryOptimizer {
    public <K, V> Predicate<K, V> optimize(Predicate<K, V> var1, Indexes var2);
}

