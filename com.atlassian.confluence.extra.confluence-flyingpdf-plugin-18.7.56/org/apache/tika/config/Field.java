/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Field {
    public String name() default "#default";

    public boolean required() default false;
}

