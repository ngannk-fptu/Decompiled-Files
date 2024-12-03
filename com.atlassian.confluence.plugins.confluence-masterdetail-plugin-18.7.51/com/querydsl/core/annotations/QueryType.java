/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.annotations;

import com.querydsl.core.annotations.PropertyType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface QueryType {
    public PropertyType value();
}

