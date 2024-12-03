/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface CacheConfig {
    public String[] cacheNames() default {};

    public String keyGenerator() default "";

    public String cacheManager() default "";

    public String cacheResolver() default "";
}

