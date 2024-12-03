/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.handler.Handler
 */
package com.sun.xml.ws.api.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public interface MessageHandler<C extends MessageHandlerContext>
extends Handler<C> {
    public Set<QName> getHeaders();
}

