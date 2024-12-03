/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.annotation.NoOpValueResolver
 *  io.micrometer.common.annotation.ValueResolver
 */
package io.micrometer.core.aop;

import io.micrometer.common.annotation.NoOpValueResolver;
import io.micrometer.common.annotation.ValueResolver;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Target(value={ElementType.PARAMETER})
public @interface MeterTag {
    public String value() default "";

    public String key() default "";

    public String expression() default "";

    public Class<? extends ValueResolver> resolver() default NoOpValueResolver.class;
}

