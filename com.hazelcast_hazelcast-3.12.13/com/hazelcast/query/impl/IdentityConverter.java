/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.TypeConverters;

class IdentityConverter
extends TypeConverters.BaseTypeConverter {
    IdentityConverter() {
    }

    @Override
    Comparable convertInternal(Comparable value) {
        return value;
    }
}

