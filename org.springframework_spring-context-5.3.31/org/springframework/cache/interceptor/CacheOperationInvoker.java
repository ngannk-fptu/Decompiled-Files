/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.interceptor;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface CacheOperationInvoker {
    @Nullable
    public Object invoke() throws ThrowableWrapper;

    public static class ThrowableWrapper
    extends RuntimeException {
        private final Throwable original;

        public ThrowableWrapper(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }
}

