/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.osgi.spring;

import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.module.ContainerAccessor;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class DefaultSpringContainerAccessor
implements ContainerAccessor {
    private final Object nativeBeanFactory;
    private final Method nativeCreateBeanMethod;
    private final Method nativeAutowireBeanPropertiesMethod;
    private final Method nativeAutowireBeanMethod;
    private final Method nativeGetBeanMethod;
    private final Method nativeGetBeansOfTypeMethod;
    private static final String USE_LEGACY_WIRING_AUTODETECTION_MODE = "com.atlassian.plugin.legacy.wiring.autodetection.mode";
    private final boolean useLegacyWiringAutodetectionMode;

    public DefaultSpringContainerAccessor(Object applicationContext) {
        Object beanFactory = null;
        try {
            Method m = applicationContext.getClass().getMethod("getAutowireCapableBeanFactory", new Class[0]);
            beanFactory = m.invoke(applicationContext, new Object[0]);
        }
        catch (NoSuchMethodException e) {
            throw new PluginException("Cannot find getAutowireCapableBeanFactory method on " + applicationContext.getClass(), (Throwable)e);
        }
        catch (IllegalAccessException e) {
            throw new PluginException("Cannot access getAutowireCapableBeanFactory method", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            this.handleSpringMethodInvocationError(e);
        }
        this.nativeBeanFactory = beanFactory;
        try {
            this.nativeCreateBeanMethod = beanFactory.getClass().getMethod("createBean", Class.class, Integer.TYPE, Boolean.TYPE);
            this.nativeAutowireBeanPropertiesMethod = beanFactory.getClass().getMethod("autowireBeanProperties", Object.class, Integer.TYPE, Boolean.TYPE);
            this.nativeAutowireBeanMethod = beanFactory.getClass().getMethod("autowireBean", Object.class);
            this.nativeGetBeanMethod = beanFactory.getClass().getMethod("getBean", String.class);
            this.nativeGetBeansOfTypeMethod = beanFactory.getClass().getMethod("getBeansOfType", Class.class);
            Preconditions.checkState((boolean)Stream.of(this.nativeGetBeansOfTypeMethod, this.nativeAutowireBeanPropertiesMethod, this.nativeAutowireBeanMethod, this.nativeCreateBeanMethod, this.nativeGetBeanMethod).allMatch(Objects::nonNull));
        }
        catch (NoSuchMethodException e) {
            throw new PluginException("Cannot find one or more methods on registered bean factory: " + this.nativeBeanFactory, (Throwable)e);
        }
        this.useLegacyWiringAutodetectionMode = Boolean.getBoolean(USE_LEGACY_WIRING_AUTODETECTION_MODE);
    }

    private void handleSpringMethodInvocationError(InvocationTargetException e) {
        if (e.getCause() instanceof Error) {
            throw (Error)e.getCause();
        }
        if (e.getCause() instanceof RuntimeException) {
            throw (RuntimeException)e.getCause();
        }
        throw new PluginException("Unable to invoke createBean", e.getCause());
    }

    public <T> T createBean(Class<T> clazz) {
        try {
            int autowiringMode = this.useLegacyWiringAutodetectionMode ? DefaultSpringContainerAccessor.resolveAutowiringMode(clazz) : 3;
            return clazz.cast(this.nativeCreateBeanMethod.invoke(this.nativeBeanFactory, clazz, autowiringMode, false));
        }
        catch (IllegalAccessException e) {
            throw new PluginException("Unable to access createBean method", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            this.handleSpringMethodInvocationError(e);
            return null;
        }
    }

    public <T> T injectBean(T bean) {
        Method beanAutowiringMethod = null;
        try {
            if (this.useLegacyWiringAutodetectionMode) {
                beanAutowiringMethod = this.nativeAutowireBeanPropertiesMethod;
                this.nativeAutowireBeanPropertiesMethod.invoke(this.nativeBeanFactory, bean, 2, false);
            } else {
                beanAutowiringMethod = this.nativeAutowireBeanMethod;
                this.nativeAutowireBeanMethod.invoke(this.nativeBeanFactory, bean);
            }
        }
        catch (IllegalAccessException e) {
            throw new PluginException("Unable to access autowireBean method: " + beanAutowiringMethod, (Throwable)e);
        }
        catch (InvocationTargetException e) {
            this.handleSpringMethodInvocationError(e);
        }
        return bean;
    }

    public <T> Collection<T> getBeansOfType(Class<T> interfaceClass) {
        try {
            Map beans = (Map)this.nativeGetBeansOfTypeMethod.invoke(this.nativeBeanFactory, interfaceClass);
            return beans.values();
        }
        catch (IllegalAccessException e) {
            throw new PluginException("Unable to access getBeansOfType method", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            this.handleSpringMethodInvocationError(e);
            return null;
        }
    }

    public <T> T getBean(String id) {
        try {
            return (T)this.nativeGetBeanMethod.invoke(this.nativeBeanFactory, id);
        }
        catch (IllegalAccessException e) {
            throw new PluginException("Unable to access getBean method", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            this.handleSpringMethodInvocationError(e);
            return null;
        }
    }

    static int resolveAutowiringMode(Class<?> beanClass) {
        boolean hasNoArgConstructor = Arrays.stream(beanClass.getConstructors()).anyMatch(cons -> cons.getParameterCount() == 0);
        return hasNoArgConstructor ? 2 : 3;
    }

    @Deprecated
    public static enum AutowireStrategy {
        AUTOWIRE_NO,
        AUTOWIRE_BY_NAME,
        AUTOWIRE_BY_TYPE,
        AUTOWIRE_BY_CONSTRUCTOR,
        AUTOWIRE_AUTODETECT;

    }
}

