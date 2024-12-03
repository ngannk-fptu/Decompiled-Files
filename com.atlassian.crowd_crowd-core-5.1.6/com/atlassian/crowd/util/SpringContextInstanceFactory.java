/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.util.InstanceFactory;
import com.google.common.base.Preconditions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextInstanceFactory
implements InstanceFactory,
ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Object getInstance(String className) throws ClassNotFoundException {
        return this.getInstance(className, this.getClass().getClassLoader());
    }

    public Object getInstance(String className, ClassLoader classLoader) throws ClassNotFoundException {
        return this.getInstance(classLoader.loadClass(className));
    }

    public <T> T getInstance(Class<T> clazz) {
        return clazz.cast(this.applicationContext.getAutowireCapableBeanFactory().createBean(clazz, 3, false));
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ApplicationContext)Preconditions.checkNotNull((Object)applicationContext);
    }
}

