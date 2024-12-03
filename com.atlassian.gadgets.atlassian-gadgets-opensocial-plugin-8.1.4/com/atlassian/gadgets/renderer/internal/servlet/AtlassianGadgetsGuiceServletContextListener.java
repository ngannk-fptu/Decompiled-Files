/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Injector
 *  com.google.inject.Provider
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 */
package com.atlassian.gadgets.renderer.internal.servlet;

import com.google.inject.Injector;
import com.google.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AtlassianGadgetsGuiceServletContextListener
implements ServletContextListener {
    private final Injector injector;

    public AtlassianGadgetsGuiceServletContextListener(Provider<Injector> provider) {
        this.injector = (Injector)provider.get();
    }

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        context.setAttribute("guice-injector", (Object)this.injector);
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        context.removeAttribute("guice-injector");
    }
}

