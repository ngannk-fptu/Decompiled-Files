/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@BinaryInterface
public class FalsePredicate<K, V>
implements IdentifiedDataSerializable,
Predicate<K, V>,
IndexAwarePredicate<K, V> {
    public static final FalsePredicate INSTANCE = new FalsePredicate();
    private static final long serialVersionUID = 1L;

    @Override
    public boolean apply(Map.Entry<K, V> mapEntry) {
        return false;
    }

    public String toString() {
        return "FalsePredicate{}";
    }

    @Override
    public Set<QueryableEntry<K, V>> filter(QueryContext queryContext) {
        return Collections.emptySet();
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        return true;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    @Override
    public int getId() {
        return 13;
    }

    public boolean equals(Object o) {
        return o instanceof FalsePredicate;
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

