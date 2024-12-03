/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.annotation;

import java.lang.reflect.Method;
import java.util.Collection;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

public interface CacheAnnotationParser {
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> var1);

    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Method var1);
}

