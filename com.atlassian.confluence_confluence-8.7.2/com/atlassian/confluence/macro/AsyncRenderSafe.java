/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface AsyncRenderSafe {
    public boolean value() default true;
}

