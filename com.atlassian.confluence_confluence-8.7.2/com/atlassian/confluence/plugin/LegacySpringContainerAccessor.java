/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginException
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.util.OsgiUtils;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class LegacySpringContainerAccessor {
    private static final Logger log = LoggerFactory.getLogger(LegacySpringContainerAccessor.class);
    private static final int AUTOWIRE_MODE = 4;
    private static final Cache<Class<?>, Boolean> shouldWarnAboutClassSetterInjection = CacheBuilder.newBuilder().weakKeys().maximumSize(10000L).recordStats().expireAfterAccess(1L, TimeUnit.HOURS).build();

    public static <T> T createBean(Plugin plugin, Class<T> clazz) {
        try {
            Object applicationContext = OsgiUtils.findApplicationContextInOsgiBundle(plugin).orElseThrow(() -> new PluginException("Can't create a bean because ApplicationContext is not found in OSGi bundle."));
            Method beanFactoryMethod = applicationContext.getClass().getMethod("getAutowireCapableBeanFactory", new Class[0]);
            Object beanFactory = beanFactoryMethod.invoke(applicationContext, new Object[0]);
            if (ConfluenceSystemProperties.isDevMode()) {
                LegacySpringContainerAccessor.checkAndWarnAboutSetterInjection(plugin, clazz);
            }
            Method createBeanMethod = beanFactory.getClass().getMethod("createBean", Class.class, Integer.TYPE, Boolean.TYPE);
            return clazz.cast(createBeanMethod.invoke(beanFactory, clazz, 4, false));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new PluginException("Unable to invoke createBean", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            LegacySpringContainerAccessor.handleSpringMethodInvocationError(e);
            return null;
        }
    }

    private static <T> void checkAndWarnAboutSetterInjection(Plugin plugin, Class<T> clazz) {
        if (shouldWarnAboutClassSetterInjection.getIfPresent(clazz) != null) {
            return;
        }
        boolean hasNoArgsConstructor = Stream.of(clazz.getConstructors()).anyMatch(c -> c.getParameterCount() == 0);
        boolean hasAtLeastOneSetter = Stream.of(clazz.getMethods()).anyMatch(m -> m.getName().startsWith("set") && m.getParameterCount() > 0);
        if (hasNoArgsConstructor && hasAtLeastOneSetter) {
            log.warn("The bean class {} of plugin \"{}\" has public no-arg constructor which causes Spring to use setter injection. This will be deprecated soon and only constructor injection will be available. The code should be refactored to use constructor injection and public no-arg constructor should be removed.", (Object)clazz.getName(), (Object)plugin.getName());
        }
        shouldWarnAboutClassSetterInjection.put(clazz, (Object)Boolean.TRUE);
    }

    private static void handleSpringMethodInvocationError(InvocationTargetException e) {
        if (e.getCause() instanceof Error) {
            throw (Error)e.getCause();
        }
        if (e.getCause() instanceof RuntimeException) {
            throw (RuntimeException)e.getCause();
        }
        throw new PluginException("Unable to invoke createBean", e.getCause());
    }
}

