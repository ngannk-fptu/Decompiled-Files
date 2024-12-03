/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface RequestType {
    public Class<?> value();

    public Class<?>[] genericTypes() default {};
}

