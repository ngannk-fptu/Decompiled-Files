/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.sizeof.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.TYPE, ElementType.PACKAGE})
public @interface IgnoreSizeOf {
    public boolean inherited() default false;
}

