/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.annotation;

import io.micrometer.core.annotation.TimedSet;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
@Repeatable(value=TimedSet.class)
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Timed {
    public String value() default "";

    public String[] extraTags() default {};

    public boolean longTask() default false;

    public double[] percentiles() default {};

    public boolean histogram() default false;

    public String description() default "";
}

