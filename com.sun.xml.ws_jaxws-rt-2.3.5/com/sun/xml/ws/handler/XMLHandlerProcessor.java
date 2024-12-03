/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.ProtocolException
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.http.HTTPException
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.handler.HandlerProcessor;
import com.sun.xml.ws.handler.HandlerTube;
import com.sun.xml.ws.handler.MessageUpdatableContext;
import java.util.List;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.http.HTTPException;

final class XMLHandlerProcessor<C extends MessageUpdatableContext>
extends HandlerProcessor<C> {
    public XMLHandlerProcessor(HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
        super(owner, binding, chain);
    }

    @Override
    final void insertFaultMessage(C context, ProtocolException exception) {
        if (exception instanceof HTTPException) {
            ((MessageUpdatableContext)context).put("javax.xml.ws.http.response.code", (Object)((HTTPException)exception).getStatusCode());
        }
        if (context != null) {
            ((MessageUpdatableContext)context).setPacketMessage(Messages.createEmpty(this.binding.getSOAPVersion()));
        }
    }
}

