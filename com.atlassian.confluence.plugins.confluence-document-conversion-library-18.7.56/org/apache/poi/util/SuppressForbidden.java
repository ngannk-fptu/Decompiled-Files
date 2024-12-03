/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface SuppressForbidden {
    public String value() default "";
}

