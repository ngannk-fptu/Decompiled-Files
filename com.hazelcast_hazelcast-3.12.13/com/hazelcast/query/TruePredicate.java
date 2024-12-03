/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.predicates.PredicateDataSerializerHook;
import java.io.IOException;
import java.util.Map;

@BinaryInterface
public class TruePredicate<K, V>
implements IdentifiedDataSerializable,
Predicate<K, V> {
    public static final TruePredicate INSTANCE = new TruePredicate();
    private static final long serialVersionUID = 1L;

    public static <K, V> TruePredicate<K, V> truePredicate() {
        return INSTANCE;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        return true;
    }

    public String toString() {
        return "TruePredicate{}";
    }

    @Override
    public int getFactoryId() {
        return PredicateDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 14;
    }

    public boolean equals(Object o) {
        return o instanceof TruePredicate;
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

