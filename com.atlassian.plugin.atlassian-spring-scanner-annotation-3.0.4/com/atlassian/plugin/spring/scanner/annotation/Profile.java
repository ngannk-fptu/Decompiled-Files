/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.spring.scanner.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Profile {
    public String[] value() default {"default"};
}

