/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ozymandias;

import com.atlassian.ozymandias.error.ErrorUtils;
import com.atlassian.ozymandias.error.ThrowableLogger;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import io.atlassian.fugue.Option;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SafeBeanInstantiator {
    private static final Logger log = LoggerFactory.getLogger(SafeBeanInstantiator.class);

    public static <T> Option<T> load(Class<T> beanClass, Plugin toInstantiateFrom) {
        Objects.requireNonNull(beanClass, "beanClass is required");
        Objects.requireNonNull(toInstantiateFrom, "toInstantiateFrom is required");
        log.debug("Attempting to instantiate a bean of type '{}' from plugin with key '{}'", beanClass, (Object)toInstantiateFrom.getKey());
        if (SafeBeanInstantiator.isNotContainerManagedPlugin(toInstantiateFrom)) {
            return Option.none();
        }
        try {
            return Option.some(SafeBeanInstantiator.loadBean(beanClass, toInstantiateFrom));
        }
        catch (Throwable t) {
            ErrorUtils.handleThrowable(t, String.format("Unable to instantiate a bean of type '%s' from plugin with key '%s' because of '%s - %s'.", beanClass, toInstantiateFrom.getKey(), ThrowableLogger.getClassName(t), t.getMessage()), log);
            return Option.none();
        }
    }

    public static <T> Option<T> load(String className, Class<T> expectedType, Class<?> callingClass, Plugin toInstantiateFrom) {
        Objects.requireNonNull(className, "beanClass is required");
        Objects.requireNonNull(expectedType, "expectedType is required");
        Objects.requireNonNull(callingClass, "callingClass is required");
        Objects.requireNonNull(toInstantiateFrom, "toInstantiateFrom is required");
        log.debug("Attempting to instantiate a bean with class name '" + className + "' with expected supertype '{}' from plugin with key '{}'", expectedType, (Object)toInstantiateFrom.getKey());
        if (SafeBeanInstantiator.isNotContainerManagedPlugin(toInstantiateFrom)) {
            return Option.none();
        }
        try {
            Class actualClass = toInstantiateFrom.loadClass(className, callingClass);
            T actualBean = SafeBeanInstantiator.loadBean(actualClass, toInstantiateFrom);
            if (!expectedType.isAssignableFrom(actualBean.getClass())) {
                log.warn("Instantiated bean of type '{}', but expected type was '{}'. Returning absent result.", actualBean.getClass(), expectedType);
                return Option.none();
            }
            return Option.some(actualBean);
        }
        catch (Throwable t) {
            ErrorUtils.handleThrowable(t, String.format("Unable to instantiate a bean with class name '%s' with expected supertype '%s' from plugin with key '%s' because of '%s - %s'.", className, expectedType, toInstantiateFrom.getKey(), ThrowableLogger.getClassName(t), t.getMessage()), log);
            return Option.none();
        }
    }

    private static boolean isNotContainerManagedPlugin(Plugin plugin) {
        boolean isNot;
        boolean bl = isNot = !(plugin instanceof ContainerManagedPlugin);
        if (isNot) {
            log.warn("Cannot instantiate bean for plugin with key '{}'. Plugin is not an instance of ContainerManagedPlugin", (Object)plugin.getKey());
        }
        return isNot;
    }

    private static <T> T loadBean(Class<T> beanClass, Plugin toLoadFrom) {
        return (T)((ContainerManagedPlugin)toLoadFrom).getContainerAccessor().createBean(beanClass);
    }
}

