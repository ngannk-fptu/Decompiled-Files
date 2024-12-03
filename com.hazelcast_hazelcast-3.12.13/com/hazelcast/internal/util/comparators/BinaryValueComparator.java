/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.comparators;

import com.hazelcast.internal.util.comparators.ValueComparator;
import com.hazelcast.spi.serialization.SerializationService;

final class BinaryValueComparator
implements ValueComparator {
    public static final ValueComparator INSTANCE = new BinaryValueComparator();

    private BinaryValueComparator() {
    }

    @Override
    public boolean isEqual(Object value1, Object value2, SerializationService ss) {
        if (value1 == value2) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        Object data1 = ss.toData(value1);
        Object data2 = ss.toData(value2);
        return data1.equals(data2);
    }
}

