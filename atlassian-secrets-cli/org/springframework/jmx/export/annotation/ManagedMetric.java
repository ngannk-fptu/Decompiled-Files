/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.jmx.support.MetricType;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedMetric {
    public String category() default "";

    public int currencyTimeLimit() default -1;

    public String description() default "";

    public String displayName() default "";

    public MetricType metricType() default MetricType.GAUGE;

    public int persistPeriod() default -1;

    public String persistPolicy() default "";

    public String unit() default "";
}

