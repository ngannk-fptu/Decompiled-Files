/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.util;

import org.springframework.lang.Nullable;

public class NullableWrapper {
    @Nullable
    private final Object value;

    public NullableWrapper(@Nullable Object value) {
        this.value = value;
    }

    public Class<?> getValueType() {
        Object value = this.value;
        return value == null ? Object.class : value.getClass();
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }
}

