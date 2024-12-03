/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 */
package org.apache.struts2.dispatcher.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.listener.ListenerHostConfig;

public class StrutsListener
implements ServletContextListener {
    private PrepareOperations prepare;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void contextInitialized(ServletContextEvent sce) {
        InitOperations init = new InitOperations();
        Dispatcher dispatcher = null;
        try {
            ListenerHostConfig config = new ListenerHostConfig(sce.getServletContext());
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);
            this.prepare = new PrepareOperations(dispatcher);
        }
        finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        this.prepare.cleanupDispatcher();
    }
}

