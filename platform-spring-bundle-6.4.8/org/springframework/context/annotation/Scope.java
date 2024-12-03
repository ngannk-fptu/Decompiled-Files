/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    @AliasFor(value="scopeName")
    public String value() default "";

    @AliasFor(value="value")
    public String scopeName() default "";

    public ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
}

