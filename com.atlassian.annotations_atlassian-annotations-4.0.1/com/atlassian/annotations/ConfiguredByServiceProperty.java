/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface ConfiguredByServiceProperty {
    public String group();

    public String property();
}

