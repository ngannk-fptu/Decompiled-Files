/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.restapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE})
public @interface LimitRequestSize {
    public long value() default 65536L;
}

