/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.support;

import org.springframework.cache.Cache;
import org.springframework.cache.support.NullValue;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;

public abstract class AbstractValueAdaptingCache
implements Cache {
    private final boolean allowNullValues;

    protected AbstractValueAdaptingCache(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public final boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    @Override
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        return this.toValueWrapper(this.lookup(key));
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        Object value = this.fromStoreValue(this.lookup(key));
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T)value;
    }

    @Nullable
    protected abstract Object lookup(Object var1);

    @Nullable
    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(@Nullable Object userValue) {
        if (userValue == null) {
            if (this.allowNullValues) {
                return NullValue.INSTANCE;
            }
            throw new IllegalArgumentException("Cache '" + this.getName() + "' is configured to not allow null values but null was provided");
        }
        return userValue;
    }

    @Nullable
    protected Cache.ValueWrapper toValueWrapper(@Nullable Object storeValue) {
        return storeValue != null ? new SimpleValueWrapper(this.fromStoreValue(storeValue)) : null;
    }
}

