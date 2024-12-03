/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.UnsatisfiedDependencyException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.hostcontainer.HostContainer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ConfluenceHostContainer
implements HostContainer,
ApplicationContextAware {
    public static final Logger log = LoggerFactory.getLogger(ConfluenceHostContainer.class);
    private ApplicationContext applicationContext;

    public <T> T create(Class<T> tClass) throws IllegalArgumentException {
        try {
            return (T)this.applicationContext.getAutowireCapableBeanFactory().createBean(tClass, 4, false);
        }
        catch (UnsatisfiedDependencyException e) {
            try {
                return (T)this.applicationContext.getAutowireCapableBeanFactory().createBean(tClass, 1, false);
            }
            catch (BeansException e1) {
                if (log.isDebugEnabled()) {
                    log.debug("Fallback to autowire by name failed as well, logging BeansException and rethrowing original UnsatisfiedDependencyException", (Throwable)e1);
                }
                throw e;
            }
        }
    }

    public <T> T getInstance(Class<T> tClass) {
        Object[] componentNames = this.applicationContext.getBeanNamesForType(tClass);
        if (componentNames.length == 0) {
            return (T)this.applicationContext.getBean(componentNames[0]);
        }
        log.warn("Unable to determine best bean of type " + tClass.getName() + " from beans: " + Arrays.toString(componentNames));
        return null;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

