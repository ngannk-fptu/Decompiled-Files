/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface QueryEntities {
    public Class<?>[] value();
}

