/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

public interface CacheOperationSource {
    default public boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    @Nullable
    public Collection<CacheOperation> getCacheOperations(Method var1, @Nullable Class<?> var2);
}

