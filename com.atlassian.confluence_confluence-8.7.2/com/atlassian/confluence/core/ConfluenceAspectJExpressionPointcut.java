/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.classloader.PluginClassLoader
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.aspectj.AspectJExpressionPointcut
 */
package com.atlassian.confluence.core;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.classloader.PluginClassLoader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

@SuppressFBWarnings(value={"EQ_DOESNT_OVERRIDE_EQUALS"})
final class ConfluenceAspectJExpressionPointcut
extends AspectJExpressionPointcut {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAspectJExpressionPointcut.class);
    private final Predicate<Class<?>> isFromPluginClassLoader;

    public ConfluenceAspectJExpressionPointcut() {
        this(clazz -> clazz.getClassLoader() instanceof PluginClassLoader);
    }

    @VisibleForTesting
    ConfluenceAspectJExpressionPointcut(Predicate<Class<?>> isFromPluginClassLoader) {
        this.isFromPluginClassLoader = isFromPluginClassLoader;
    }

    public boolean matches(Class<?> targetClass) {
        if (this.isFromPluginClassLoader(targetClass)) {
            ConfluenceAspectJExpressionPointcut.log(targetClass);
            return false;
        }
        return super.matches(targetClass);
    }

    public boolean matches(Method method, Class<?> targetClass, boolean hasIntroductions) {
        if (this.isFromPluginClassLoader(targetClass)) {
            ConfluenceAspectJExpressionPointcut.log(targetClass);
            return false;
        }
        return super.matches(method, targetClass, hasIntroductions);
    }

    public boolean matches(Method method, Class<?> targetClass, Object ... args) {
        if (this.isFromPluginClassLoader(targetClass)) {
            ConfluenceAspectJExpressionPointcut.log(targetClass);
            return false;
        }
        return super.matches(method, targetClass, args);
    }

    private boolean isFromPluginClassLoader(Class<?> targetClass) {
        return this.isFromPluginClassLoader.test(targetClass);
    }

    private static void log(Class<?> targetClass) {
        log.debug("Skipping pointcut matching for plugins class: {}", targetClass);
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }
}

