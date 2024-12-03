/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

@FunctionalInterface
public interface CacheOperationInvoker {
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

