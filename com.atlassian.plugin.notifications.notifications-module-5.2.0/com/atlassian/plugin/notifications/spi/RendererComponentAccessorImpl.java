/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.spi.NotificationRenderer;
import com.atlassian.plugin.notifications.spi.NotificationRendererServiceFactory;
import com.atlassian.plugin.notifications.spi.RendererComponentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class RendererComponentAccessorImpl
implements RendererComponentAccessor {
    private static final Logger log = LoggerFactory.getLogger(RendererComponentAccessorImpl.class);
    private final ApplicationContext applicationContext;
    private NotificationRenderer renderer;

    public RendererComponentAccessorImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized NotificationRenderer getRenderer() {
        if (this.renderer == null) {
            this.initRendererServiceFactory();
        }
        return this.renderer;
    }

    private void initRendererServiceFactory() {
        try {
            this.renderer = (NotificationRenderer)((NotificationRendererServiceFactory)this.applicationContext.getAutowireCapableBeanFactory().createBean(NotificationRendererServiceFactory.class, 3, false)).getService();
        }
        catch (Exception e) {
            log.debug("Could not create NotificationRendererServiceFactory", (Throwable)e);
        }
    }
}

