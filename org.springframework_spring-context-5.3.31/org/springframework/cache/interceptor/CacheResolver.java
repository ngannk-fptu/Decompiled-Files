/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.util.Collection;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;

@FunctionalInterface
public interface CacheResolver {
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> var1);
}

