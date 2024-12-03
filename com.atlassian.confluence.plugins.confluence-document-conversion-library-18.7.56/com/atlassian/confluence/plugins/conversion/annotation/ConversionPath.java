/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.annotation;

import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ConversionPath {
    public ConversionType value();
}

