/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.util.Nonbinding
 */
package javax.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheResolverFactory;
import javax.enterprise.util.Nonbinding;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CacheRemove {
    @Nonbinding
    public String cacheName() default "";

    @Nonbinding
    public boolean afterInvocation() default true;

    @Nonbinding
    public Class<? extends CacheResolverFactory> cacheResolverFactory() default CacheResolverFactory.class;

    @Nonbinding
    public Class<? extends CacheKeyGenerator> cacheKeyGenerator() default CacheKeyGenerator.class;

    @Nonbinding
    public Class<? extends Throwable>[] evictFor() default {};

    @Nonbinding
    public Class<? extends Throwable>[] noEvictFor() default {};
}

