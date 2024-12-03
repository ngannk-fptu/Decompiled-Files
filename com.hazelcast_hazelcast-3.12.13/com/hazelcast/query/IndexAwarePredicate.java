/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Set;

@BinaryInterface
public interface IndexAwarePredicate<K, V>
extends Predicate<K, V> {
    public Set<QueryableEntry<K, V>> filter(QueryContext var1);

    public boolean isIndexed(QueryContext var1);
}

