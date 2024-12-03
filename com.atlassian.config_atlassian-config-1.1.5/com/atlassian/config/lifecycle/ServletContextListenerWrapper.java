/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 */
package com.atlassian.config.lifecycle;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextListenerWrapper
implements LifecycleItem {
    private final ServletContextListener listener;

    public ServletContextListenerWrapper(ServletContextListener listener) {
        this.listener = listener;
    }

    @Override
    public void startup(LifecycleContext context) {
        this.listener.contextInitialized(new ServletContextEvent(context.getServletContext()));
    }

    @Override
    public void shutdown(LifecycleContext context) {
        this.listener.contextDestroyed(new ServletContextEvent(context.getServletContext()));
    }

    public ServletContextListener getWrappedListener() {
        return this.listener;
    }
}

