/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 */
package org.apache.tomcat.websocket.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.tomcat.websocket.server.WsSci;
import org.apache.tomcat.websocket.server.WsServerContainer;

public class WsContextListener
implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        if (sc.getAttribute("javax.websocket.server.ServerContainer") == null) {
            WsSci.init(sce.getServletContext(), false);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        Object obj = sc.getAttribute("javax.websocket.server.ServerContainer");
        if (obj instanceof WsServerContainer) {
            ((WsServerContainer)obj).destroy();
        }
    }
}

