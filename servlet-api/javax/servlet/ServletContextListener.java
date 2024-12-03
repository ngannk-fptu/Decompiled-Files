/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.util.EventListener;
import javax.servlet.ServletContextEvent;

public interface ServletContextListener
extends EventListener {
    default public void contextInitialized(ServletContextEvent sce) {
    }

    default public void contextDestroyed(ServletContextEvent sce) {
    }
}

