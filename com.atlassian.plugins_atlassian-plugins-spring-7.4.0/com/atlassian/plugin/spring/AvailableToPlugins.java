/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy
 */
package com.atlassian.plugin.spring;

import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface AvailableToPlugins {
    public Class value() default Void.class;

    public Class[] interfaces() default {};

    public ContextClassLoaderStrategy contextClassLoaderStrategy() default ContextClassLoaderStrategy.USE_HOST;

    public boolean trackBundle() default false;
}

