/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.handler;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;

public interface Handler {
    public boolean handleRequest(MessageContext var1);

    public boolean handleResponse(MessageContext var1);

    public boolean handleFault(MessageContext var1);

    public void init(HandlerInfo var1);

    public void destroy();

    public QName[] getHeaders();
}

