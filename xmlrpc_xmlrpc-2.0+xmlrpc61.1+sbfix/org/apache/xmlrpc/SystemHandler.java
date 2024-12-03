/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.util.Vector;
import org.apache.xmlrpc.ContextXmlRpcHandler;
import org.apache.xmlrpc.DefaultHandlerMapping;
import org.apache.xmlrpc.MultiCall;
import org.apache.xmlrpc.XmlRpcContext;
import org.apache.xmlrpc.XmlRpcHandlerMapping;
import org.apache.xmlrpc.XmlRpcServer;

public class SystemHandler
implements ContextXmlRpcHandler {
    private DefaultHandlerMapping systemMapping = new DefaultHandlerMapping();

    public SystemHandler() {
    }

    public SystemHandler(XmlRpcHandlerMapping handlerMapping) {
        this();
        if (handlerMapping != null) {
            this.addDefaultSystemHandlers();
        }
    }

    protected SystemHandler(XmlRpcServer server) {
        this(server.getHandlerMapping());
    }

    public void addDefaultSystemHandlers() {
        this.addSystemHandler("multicall", new MultiCall());
    }

    public void addSystemHandler(String handlerName, ContextXmlRpcHandler handler) {
        this.systemMapping.addHandler(handlerName, handler);
    }

    public void removeSystemHandler(String handlerName) {
        this.systemMapping.removeHandler(handlerName);
    }

    public Object execute(String method, Vector params, XmlRpcContext context) throws Exception {
        Object handler = null;
        String systemMethod = null;
        int dot = method.lastIndexOf(46);
        if (dot > -1 && (handler = this.systemMapping.getHandler((systemMethod = method.substring(dot + 1)) + ".")) != null) {
            return ((ContextXmlRpcHandler)handler).execute(systemMethod, params, context);
        }
        throw new NoSuchMethodException("No method '" + method + "' registered.");
    }
}

