/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.handler;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;

public abstract class GenericHandler
implements Handler {
    protected GenericHandler() {
    }

    public boolean handleRequest(MessageContext context) {
        return true;
    }

    public boolean handleResponse(MessageContext context) {
        return true;
    }

    public boolean handleFault(MessageContext context) {
        return true;
    }

    public void init(HandlerInfo config) {
    }

    public void destroy() {
    }

    public abstract QName[] getHeaders();
}

