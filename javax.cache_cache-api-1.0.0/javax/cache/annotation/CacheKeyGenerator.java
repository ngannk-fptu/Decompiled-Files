/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.annotation;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.GeneratedCacheKey;

public interface CacheKeyGenerator {
    public GeneratedCacheKey generateCacheKey(CacheKeyInvocationContext<? extends Annotation> var1);
}

