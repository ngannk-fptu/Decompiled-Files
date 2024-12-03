/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.support;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

public class SimpleValueWrapper
implements Cache.ValueWrapper {
    @Nullable
    private final Object value;

    public SimpleValueWrapper(@Nullable Object value) {
        this.value = value;
    }

    @Override
    @Nullable
    public Object get() {
        return this.value;
    }
}

