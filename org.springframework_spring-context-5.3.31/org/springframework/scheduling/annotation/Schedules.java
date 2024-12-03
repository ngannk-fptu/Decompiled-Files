/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.scheduling.annotation.Scheduled;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Schedules {
    public Scheduled[] value();
}

