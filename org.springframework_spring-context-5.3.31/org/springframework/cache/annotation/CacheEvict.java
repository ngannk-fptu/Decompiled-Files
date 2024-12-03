/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AliasFor
 */
package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {
    @AliasFor(value="cacheNames")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] cacheNames() default {};

    public String key() default "";

    public String keyGenerator() default "";

    public String cacheManager() default "";

    public String cacheResolver() default "";

    public String condition() default "";

    public boolean allEntries() default false;

    public boolean beforeInvocation() default false;
}

