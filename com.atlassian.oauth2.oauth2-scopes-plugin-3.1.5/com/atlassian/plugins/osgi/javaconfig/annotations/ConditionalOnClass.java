/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Conditional
 */
package com.atlassian.plugins.osgi.javaconfig.annotations;

import com.atlassian.plugins.osgi.javaconfig.annotations.ConditionalOnClassCondition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD})
@Conditional(value={ConditionalOnClassCondition.class})
public @interface ConditionalOnClass {
    public Class[] value();
}

