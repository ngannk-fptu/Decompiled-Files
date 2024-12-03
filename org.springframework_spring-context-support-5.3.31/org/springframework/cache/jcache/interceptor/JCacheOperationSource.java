/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import org.springframework.cache.jcache.interceptor.JCacheOperation;
import org.springframework.lang.Nullable;

public interface JCacheOperationSource {
    @Nullable
    public JCacheOperation<?> getCacheOperation(Method var1, @Nullable Class<?> var2);
}

