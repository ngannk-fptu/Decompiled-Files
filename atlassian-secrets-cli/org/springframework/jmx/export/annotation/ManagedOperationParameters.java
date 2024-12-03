/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedOperationParameters {
    public ManagedOperationParameter[] value() default {};
}

