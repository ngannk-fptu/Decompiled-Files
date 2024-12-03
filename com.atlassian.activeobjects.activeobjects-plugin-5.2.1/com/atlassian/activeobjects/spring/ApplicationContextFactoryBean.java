/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.activeobjects.spring;

import com.google.common.base.Preconditions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class ApplicationContextFactoryBean
implements FactoryBean,
ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Object getObject() throws Exception {
        return this.applicationContext;
    }

    public Class getObjectType() {
        return ApplicationContext.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ApplicationContext)Preconditions.checkNotNull((Object)applicationContext);
    }
}

