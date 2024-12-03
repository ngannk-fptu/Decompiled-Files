/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface DeclareMixin {
    public String value();

    public Class[] interfaces() default {Object.class};
}

