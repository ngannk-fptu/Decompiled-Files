/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 */
package org.apache.tomcat.websocket.server;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.tomcat.websocket.server.WsServerContainer;

public class WsSessionListener
implements HttpSessionListener {
    private final WsServerContainer wsServerContainer;

    public WsSessionListener(WsServerContainer wsServerContainer) {
        this.wsServerContainer = wsServerContainer;
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        this.wsServerContainer.closeAuthenticatedSession(se.getSession().getId());
    }
}

