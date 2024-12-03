/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xwork;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface ActionViewData {
    public boolean injected() default false;
}

