/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.annotation;

import java.lang.annotation.Annotation;
import javax.cache.Cache;
import javax.cache.annotation.CacheInvocationContext;

public interface CacheResolver {
    public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> var1);
}

