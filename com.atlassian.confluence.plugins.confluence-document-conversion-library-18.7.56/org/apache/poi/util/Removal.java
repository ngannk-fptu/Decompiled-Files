/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Removal {
    public String version() default "";
}

