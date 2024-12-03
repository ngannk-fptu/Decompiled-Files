/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;

public interface CompoundPredicate {
    public <K, V> Predicate<K, V>[] getPredicates();

    public <K, V> void setPredicates(Predicate<K, V>[] var1);
}

