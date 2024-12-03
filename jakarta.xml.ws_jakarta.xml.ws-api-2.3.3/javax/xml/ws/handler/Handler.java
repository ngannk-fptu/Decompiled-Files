/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.handler;

import javax.xml.ws.handler.MessageContext;

public interface Handler<C extends MessageContext> {
    public boolean handleMessage(C var1);

    public boolean handleFault(C var1);

    public void close(MessageContext var1);
}

