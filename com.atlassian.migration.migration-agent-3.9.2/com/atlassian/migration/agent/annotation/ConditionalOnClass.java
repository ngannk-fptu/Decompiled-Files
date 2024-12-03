/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Conditional
 */
package com.atlassian.migration.agent.annotation;

import com.atlassian.migration.agent.annotation.OnClassCondition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD})
@Conditional(value={OnClassCondition.class})
public @interface ConditionalOnClass {
    public String[] value() default {};
}

