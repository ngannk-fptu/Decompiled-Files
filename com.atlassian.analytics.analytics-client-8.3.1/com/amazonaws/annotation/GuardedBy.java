/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(value=RetentionPolicy.CLASS)
public @interface GuardedBy {
    public String value();
}

