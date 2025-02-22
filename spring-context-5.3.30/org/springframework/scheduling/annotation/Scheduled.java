/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Schedules;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value=Schedules.class)
public @interface Scheduled {
    public static final String CRON_DISABLED = "-";

    public String cron() default "";

    public String zone() default "";

    public long fixedDelay() default -1L;

    public String fixedDelayString() default "";

    public long fixedRate() default -1L;

    public String fixedRateString() default "";

    public long initialDelay() default -1L;

    public String initialDelayString() default "";

    public TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}

