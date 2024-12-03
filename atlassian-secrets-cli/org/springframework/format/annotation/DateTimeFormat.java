/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface DateTimeFormat {
    public String style() default "SS";

    public ISO iso() default ISO.NONE;

    public String pattern() default "";

    public static enum ISO {
        DATE,
        TIME,
        DATE_TIME,
        NONE;

    }
}

