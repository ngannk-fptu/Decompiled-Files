/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Counted {
    public String value() default "method.counted";

    public boolean recordFailuresOnly() default false;

    public String[] extraTags() default {};

    public String description() default "";
}

