/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  javax.annotation.Nonnull
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.plugin.osgi.bridge.external;

import com.atlassian.plugin.hostcontainer.HostContainer;
import javax.annotation.Nonnull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringHostContainer
implements HostContainer,
ApplicationContextAware {
    private ApplicationContext applicationContext;

    public <T> T create(Class<T> moduleClass) {
        if (this.applicationContext == null) {
            throw new IllegalStateException("Application context missing");
        }
        return (T)this.applicationContext.getAutowireCapableBeanFactory().createBean(moduleClass, 3, false);
    }

    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}

