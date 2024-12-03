/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol;

import com.amazonaws.protocol.MarshallingType;

final class DefaultMarshallingType<T>
implements MarshallingType<T> {
    private final Class<T> type;

    protected DefaultMarshallingType(Class<T> type) {
        this.type = type;
    }

    @Override
    public boolean isDefaultMarshallerForType(Class<?> type) {
        return this.type.isAssignableFrom(type);
    }
}

