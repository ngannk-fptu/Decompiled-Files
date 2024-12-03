/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util.internal;

import java.util.function.Function;

public class ClassValueCache<R>
implements Function<Class<?>, R> {
    private final Function<Class<?>, R> ifAbsent;
    private final ClassValue<R> cache = new ClassValue<R>(){

        @Override
        protected R computeValue(Class<?> c) {
            return ClassValueCache.this.ifAbsent.apply(c);
        }
    };

    public ClassValueCache(Function<Class<?>, R> ifAbsent) {
        this.ifAbsent = ifAbsent;
    }

    @Override
    public R apply(Class<?> c) {
        return this.cache.get(c);
    }
}

