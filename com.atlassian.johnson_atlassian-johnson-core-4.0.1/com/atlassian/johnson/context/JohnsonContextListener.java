/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 */
package com.atlassian.johnson.context;

import com.atlassian.johnson.Johnson;
import java.util.EventListener;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class JohnsonContextListener
implements ServletContextListener {
    public static final String ATTR_REGISTERED = JohnsonContextListener.class.getName() + ":Registered";

    public static void register(@Nonnull ServletContext context) {
        if (context.getAttribute(ATTR_REGISTERED) == null) {
            context.addListener((EventListener)((Object)new JohnsonContextListener()));
            context.setAttribute(ATTR_REGISTERED, (Object)Boolean.TRUE);
        }
    }

    public void contextDestroyed(@Nonnull ServletContextEvent event) {
        Johnson.terminate(event.getServletContext());
    }

    public void contextInitialized(@Nonnull ServletContextEvent event) {
        Johnson.initialize(event.getServletContext());
    }
}

