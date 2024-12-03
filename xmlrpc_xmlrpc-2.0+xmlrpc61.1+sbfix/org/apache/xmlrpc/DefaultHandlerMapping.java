/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.util.Hashtable;
import org.apache.xmlrpc.AuthenticatedXmlRpcHandler;
import org.apache.xmlrpc.ContextXmlRpcHandler;
import org.apache.xmlrpc.Invoker;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcHandlerMapping;

public class DefaultHandlerMapping
implements XmlRpcHandlerMapping {
    private Hashtable handlers = new Hashtable();

    public void addHandler(String handlerName, Object handler) {
        if (handler instanceof XmlRpcHandler || handler instanceof AuthenticatedXmlRpcHandler || handler instanceof ContextXmlRpcHandler) {
            this.handlers.put(handlerName, handler);
        } else if (handler != null) {
            this.handlers.put(handlerName, new Invoker(handler));
        }
    }

    public void removeHandler(String handlerName) {
        this.handlers.remove(handlerName);
    }

    public Object getHandler(String methodName) throws Exception {
        Object handler = null;
        String handlerName = null;
        int dot = methodName.lastIndexOf(46);
        if (dot > -1) {
            handlerName = methodName.substring(0, dot);
            handler = this.handlers.get(handlerName);
        }
        if (handler == null && (handler = this.handlers.get("$default")) == null) {
            if (dot > -1) {
                throw new Exception("RPC handler object \"" + handlerName + "\" not found and no " + "default handler registered");
            }
            throw new Exception("RPC handler object not found for \"" + methodName + "\": No default handler registered");
        }
        return handler;
    }
}

