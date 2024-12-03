/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface SuppressAjWarnings {
    public String[] value() default {""};
}

