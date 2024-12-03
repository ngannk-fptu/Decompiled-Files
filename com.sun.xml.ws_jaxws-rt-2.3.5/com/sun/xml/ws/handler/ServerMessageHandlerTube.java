/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.handler.MessageContext
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.handler.MessageHandler;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.HandlerConfiguration;
import com.sun.xml.ws.handler.HandlerProcessor;
import com.sun.xml.ws.handler.HandlerTube;
import com.sun.xml.ws.handler.MessageHandlerContextImpl;
import com.sun.xml.ws.handler.MessageUpdatableContext;
import com.sun.xml.ws.handler.SOAPHandlerProcessor;
import com.sun.xml.ws.message.DataHandlerAttachment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ServerMessageHandlerTube
extends HandlerTube {
    private SEIModel seiModel;
    private Set<String> roles;

    public ServerMessageHandlerTube(SEIModel seiModel, WSBinding binding, Tube next, HandlerTube cousinTube) {
        super(next, cousinTube, binding);
        this.seiModel = seiModel;
        this.setUpHandlersOnce();
    }

    private ServerMessageHandlerTube(ServerMessageHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
        this.handlers = that.handlers;
        this.roles = that.roles;
    }

    private void setUpHandlersOnce() {
        this.handlers = new ArrayList();
        HandlerConfiguration handlerConfig = ((BindingImpl)this.getBinding()).getHandlerConfig();
        List<MessageHandler> msgHandlersSnapShot = handlerConfig.getMessageHandlers();
        if (!msgHandlersSnapShot.isEmpty()) {
            this.handlers.addAll(msgHandlersSnapShot);
            this.roles = new HashSet<String>();
            this.roles.addAll(handlerConfig.getRoles());
        }
    }

    @Override
    void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
        Map atts = (Map)context.get("javax.xml.ws.binding.attachments.outbound");
        AttachmentSet attSet = context.packet.getMessage().getAttachments();
        for (Map.Entry entry : atts.entrySet()) {
            String cid = (String)entry.getKey();
            if (attSet.get(cid) != null) continue;
            DataHandlerAttachment att = new DataHandlerAttachment(cid, (DataHandler)atts.get(cid));
            attSet.add(att);
        }
        try {
            this.processor.callHandlersResponse(HandlerProcessor.Direction.OUTBOUND, context, handleFault);
        }
        catch (WebServiceException wse) {
            throw wse;
        }
        catch (RuntimeException re) {
            throw re;
        }
    }

    @Override
    boolean callHandlersOnRequest(MessageUpdatableContext context, boolean isOneWay) {
        boolean handlerResult;
        try {
            handlerResult = this.processor.callHandlersRequest(HandlerProcessor.Direction.INBOUND, context, !isOneWay);
        }
        catch (RuntimeException re) {
            this.remedyActionTaken = true;
            throw re;
        }
        if (!handlerResult) {
            this.remedyActionTaken = true;
        }
        return handlerResult;
    }

    @Override
    protected void resetProcessor() {
        this.processor = null;
    }

    @Override
    void setUpProcessor() {
        if (!this.handlers.isEmpty() && this.processor == null) {
            this.processor = new SOAPHandlerProcessor(false, this, this.getBinding(), this.handlers);
        }
    }

    @Override
    void closeHandlers(MessageContext mc) {
        this.closeServersideHandlers(mc);
    }

    @Override
    MessageUpdatableContext getContext(Packet packet) {
        MessageHandlerContextImpl context = new MessageHandlerContextImpl(this.seiModel, this.getBinding(), this.port, packet, this.roles);
        return context;
    }

    @Override
    protected void initiateClosing(MessageContext mc) {
        this.close(mc);
        super.initiateClosing(mc);
    }

    @Override
    public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ServerMessageHandlerTube(this, cloner);
    }
}

