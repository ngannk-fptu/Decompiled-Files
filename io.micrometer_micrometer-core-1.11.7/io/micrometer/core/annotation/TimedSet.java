/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.annotation;

import io.micrometer.core.annotation.Timed;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface TimedSet {
    public Timed[] value();
}

