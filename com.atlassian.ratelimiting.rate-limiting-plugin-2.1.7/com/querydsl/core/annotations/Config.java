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
@Target(value={ElementType.PACKAGE, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Config {
    public boolean entityAccessors() default false;

    public boolean listAccessors() default false;

    public boolean mapAccessors() default false;

    public boolean createDefaultVariable() default true;

    public String defaultVariableName() default "";
}

