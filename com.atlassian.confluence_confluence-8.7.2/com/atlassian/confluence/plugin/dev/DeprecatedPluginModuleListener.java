/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.descriptors.ConditionalDescriptor
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugin.dev;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.descriptors.ConditionalDescriptor;
import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DeprecatedPluginModuleListener
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DeprecatedPluginModuleListener.class);
    private final EventPublisher eventPublisher;

    public DeprecatedPluginModuleListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (ConfluenceSystemProperties.isDevMode()) {
            DeprecatedPluginModuleListener.findAndLogDeprecations(event.getModule());
        }
    }

    private static void findAndLogDeprecations(ModuleDescriptor<?> moduleDescriptor) {
        for (Class<?> clazz : DeprecatedPluginModuleListener.getClassesToCheckForDeprecation(moduleDescriptor)) {
            Class<?> deprecatedType = DeprecatedPluginModuleListener.findAnnotatedType(clazz, Deprecated.class);
            if (deprecatedType == clazz) {
                log.warn("Class {} of module '{}' is deprecated", (Object)clazz.getName(), (Object)moduleDescriptor.getCompleteKey());
                continue;
            }
            if (deprecatedType != null) {
                log.warn("Type {} inherited by class {} of module '{}' is deprecated", new Object[]{deprecatedType.getName(), clazz.getName(), moduleDescriptor.getCompleteKey()});
                continue;
            }
            log.debug("Class {} of module '{}' is OK", (Object)clazz.getName(), (Object)moduleDescriptor.getCompleteKey());
        }
    }

    private static List<Class<?>> getClassesToCheckForDeprecation(ModuleDescriptor<?> moduleDescriptor) {
        Condition condition;
        ArrayList classes = Lists.newArrayList((Object[])new Class[]{moduleDescriptor.getModuleClass()});
        if (moduleDescriptor instanceof ConditionalDescriptor && (condition = ((ConditionalDescriptor)moduleDescriptor).getCondition()) != null) {
            classes.add(condition.getClass());
        }
        return classes;
    }

    private static Class<?> findAnnotatedType(Class<?> clazz, Class<? extends Annotation> annotationType) {
        if (clazz.getAnnotation(annotationType) != null) {
            return clazz;
        }
        for (Class<?> interfaceType : clazz.getInterfaces()) {
            Class<?> deprecatedType = DeprecatedPluginModuleListener.findAnnotatedType(interfaceType, annotationType);
            if (deprecatedType == null) continue;
            return deprecatedType;
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null || superClass == Object.class) {
            return null;
        }
        return DeprecatedPluginModuleListener.findAnnotatedType(superClass, annotationType);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }
}

