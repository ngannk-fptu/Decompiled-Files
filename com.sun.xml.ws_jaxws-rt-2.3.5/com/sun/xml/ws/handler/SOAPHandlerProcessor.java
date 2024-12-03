/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.ProtocolException
 *  javax.xml.ws.handler.Handler
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.handler.HandlerProcessor;
import com.sun.xml.ws.handler.HandlerTube;
import com.sun.xml.ws.handler.MessageUpdatableContext;
import java.util.List;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;

final class SOAPHandlerProcessor<C extends MessageUpdatableContext>
extends HandlerProcessor<C> {
    public SOAPHandlerProcessor(boolean isClient, HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
        super(owner, binding, chain);
        this.isClient = isClient;
    }

    @Override
    final void insertFaultMessage(C context, ProtocolException exception) {
        try {
            if (!((MessageUpdatableContext)context).getPacketMessage().isFault()) {
                Message faultMessage = Messages.create(this.binding.getSOAPVersion(), exception, this.determineFaultCode(this.binding.getSOAPVersion()));
                ((MessageUpdatableContext)context).setPacketMessage(faultMessage);
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "exception while creating fault message in handler chain", e);
            throw new RuntimeException(e);
        }
    }

    private QName determineFaultCode(SOAPVersion soapVersion) {
        return this.isClient ? soapVersion.faultCodeClient : soapVersion.faultCodeServer;
    }
}

