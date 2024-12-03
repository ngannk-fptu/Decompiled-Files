/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache;

import java.util.concurrent.Callable;
import org.springframework.lang.Nullable;

public interface Cache {
    public String getName();

    public Object getNativeCache();

    @Nullable
    public ValueWrapper get(Object var1);

    @Nullable
    public <T> T get(Object var1, @Nullable Class<T> var2);

    @Nullable
    public <T> T get(Object var1, Callable<T> var2);

    public void put(Object var1, @Nullable Object var2);

    @Nullable
    default public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        ValueWrapper existingValue = this.get(key);
        if (existingValue == null) {
            this.put(key, value);
        }
        return existingValue;
    }

    public void evict(Object var1);

    default public boolean evictIfPresent(Object key) {
        this.evict(key);
        return false;
    }

    public void clear();

    default public boolean invalidate() {
        this.clear();
        return false;
    }

    public static class ValueRetrievalException
    extends RuntimeException {
        @Nullable
        private final Object key;

        public ValueRetrievalException(@Nullable Object key, Callable<?> loader, Throwable ex) {
            super(String.format("Value for key '%s' could not be loaded using '%s'", key, loader), ex);
            this.key = key;
        }

        @Nullable
        public Object getKey() {
            return this.key;
        }
    }

    @FunctionalInterface
    public static interface ValueWrapper {
        @Nullable
        public Object get();
    }
}

