/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface RequiresDarkFeature {
    public String[] value();
}

