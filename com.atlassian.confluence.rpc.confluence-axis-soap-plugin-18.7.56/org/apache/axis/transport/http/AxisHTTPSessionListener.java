/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.xml.rpc.server.ServiceLifecycle;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

public class AxisHTTPSessionListener
implements HttpSessionListener {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$AxisHTTPSessionListener == null ? (class$org$apache$axis$transport$http$AxisHTTPSessionListener = AxisHTTPSessionListener.class$("org.apache.axis.transport.http.AxisHTTPSessionListener")) : class$org$apache$axis$transport$http$AxisHTTPSessionListener).getName());
    static /* synthetic */ Class class$org$apache$axis$transport$http$AxisHTTPSessionListener;

    static void destroySession(HttpSession session) {
        if (session.getAttribute("axis.isAxisSession") == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Got destroySession event : " + session));
        }
        Enumeration e = session.getAttributeNames();
        while (e.hasMoreElements()) {
            Object next = e.nextElement();
            if (!(next instanceof ServiceLifecycle)) continue;
            ((ServiceLifecycle)next).destroy();
        }
    }

    public void sessionCreated(HttpSessionEvent event) {
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        AxisHTTPSessionListener.destroySession(session);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

