/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.hazelcast.map.impl.tx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class TxnValueWrapper {
    Object value;
    Type type;

    TxnValueWrapper(@Nullable Object value, @Nonnull Type type) {
        assert (type != null);
        this.value = value;
        this.type = type;
    }

    static enum Type {
        NEW,
        UPDATED,
        REMOVED;

    }
}

