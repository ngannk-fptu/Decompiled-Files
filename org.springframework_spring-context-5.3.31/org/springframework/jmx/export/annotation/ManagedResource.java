/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AliasFor
 */
package org.springframework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ManagedResource {
    @AliasFor(value="objectName")
    public String value() default "";

    @AliasFor(value="value")
    public String objectName() default "";

    public String description() default "";

    public int currencyTimeLimit() default -1;

    public boolean log() default false;

    public String logFile() default "";

    public String persistPolicy() default "";

    public int persistPeriod() default -1;

    public String persistName() default "";

    public String persistLocation() default "";
}

