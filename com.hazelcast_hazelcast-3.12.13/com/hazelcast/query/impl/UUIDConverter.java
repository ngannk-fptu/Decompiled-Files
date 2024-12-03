/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.TypeConverters;
import java.util.UUID;

final class UUIDConverter
extends TypeConverters.BaseTypeConverter {
    UUIDConverter() {
    }

    @Override
    Comparable convertInternal(Comparable value) {
        if (value instanceof UUID) {
            return value;
        }
        if (value instanceof String) {
            return UUID.fromString((String)((Object)value));
        }
        throw new IllegalArgumentException("Cannot convert [" + value + "] to java.util.UUID");
    }
}

