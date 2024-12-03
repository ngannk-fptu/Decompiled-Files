/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.annotation;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResult;

public interface CacheResolverFactory {
    public CacheResolver getCacheResolver(CacheMethodDetails<? extends Annotation> var1);

    public CacheResolver getExceptionCacheResolver(CacheMethodDetails<CacheResult> var1);
}

