/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.comparators;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.util.comparators.BinaryValueComparator;
import com.hazelcast.internal.util.comparators.ObjectValueComparator;
import com.hazelcast.internal.util.comparators.ValueComparator;

public final class ValueComparatorUtil {
    private ValueComparatorUtil() {
    }

    public static ValueComparator getValueComparatorOf(InMemoryFormat inMemoryFormat) {
        switch (inMemoryFormat) {
            case BINARY: {
                return BinaryValueComparator.INSTANCE;
            }
            case OBJECT: {
                return ObjectValueComparator.INSTANCE;
            }
            case NATIVE: {
                throw new IllegalArgumentException("Native storage format is supported in Hazelcast Enterprise only. Make sure you have Hazelcast Enterprise JARs on your classpath !");
            }
        }
        throw new IllegalArgumentException("Invalid storage format: " + (Object)((Object)inMemoryFormat));
    }
}

