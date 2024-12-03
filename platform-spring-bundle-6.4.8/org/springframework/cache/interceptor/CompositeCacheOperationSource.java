/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class CompositeCacheOperationSource
implements CacheOperationSource,
Serializable {
    private final CacheOperationSource[] cacheOperationSources;

    public CompositeCacheOperationSource(CacheOperationSource ... cacheOperationSources) {
        Assert.notEmpty((Object[])cacheOperationSources, "CacheOperationSource array must not be empty");
        this.cacheOperationSources = cacheOperationSources;
    }

    public final CacheOperationSource[] getCacheOperationSources() {
        return this.cacheOperationSources;
    }

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        for (CacheOperationSource source : this.cacheOperationSources) {
            if (!source.isCandidateClass(targetClass)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
        ArrayList<CacheOperation> ops = null;
        for (CacheOperationSource source : this.cacheOperationSources) {
            Collection<CacheOperation> cacheOperations = source.getCacheOperations(method, targetClass);
            if (cacheOperations == null) continue;
            if (ops == null) {
                ops = new ArrayList<CacheOperation>();
            }
            ops.addAll(cacheOperations);
        }
        return ops;
    }
}

