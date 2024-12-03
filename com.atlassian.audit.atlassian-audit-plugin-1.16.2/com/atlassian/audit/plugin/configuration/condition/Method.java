/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.plugin.configuration.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Method {
    public Class of();

    public String method();
}

