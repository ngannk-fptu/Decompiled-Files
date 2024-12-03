/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.annotations.tenancy;

import com.atlassian.annotations.Internal;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Deprecated
@Internal
public @interface Tenantless {
    public String reason() default "";
}

