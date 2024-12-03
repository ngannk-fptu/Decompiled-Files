/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.comparators;

import com.hazelcast.internal.util.comparators.ValueComparator;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

final class ObjectValueComparator
implements ValueComparator {
    public static final ValueComparator INSTANCE = new ObjectValueComparator();

    private ObjectValueComparator() {
    }

    @Override
    public boolean isEqual(Object value1, Object value2, SerializationService ss) {
        Object v2;
        if (value1 == value2) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        Object v1 = value1 instanceof Data ? ss.toObject(value1) : value1;
        Object object = v2 = value2 instanceof Data ? ss.toObject(value2) : value2;
        return v1 != null ? v1.equals(v2) : v2 == null;
    }
}

