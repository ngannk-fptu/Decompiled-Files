/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.activation.DataHandler
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.handler.MessageContext
 */
package com.sun.xml.ws.handler;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.handler.MessageHandler;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
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

public class ClientMessageHandlerTube
extends HandlerTube {
    private SEIModel seiModel;
    private Set<String> roles;

    public ClientMessageHandlerTube(@Nullable SEIModel seiModel, WSBinding binding, WSDLPort port, Tube next) {
        super(next, port, binding);
        this.seiModel = seiModel;
    }

    private ClientMessageHandlerTube(ClientMessageHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
    }

    @Override
    public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ClientMessageHandlerTube(this, cloner);
    }

    @Override
    void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
        try {
            this.processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, context, handleFault);
        }
        catch (WebServiceException wse) {
            throw wse;
        }
        catch (RuntimeException re) {
            throw new WebServiceException((Throwable)re);
        }
    }

    @Override
    boolean callHandlersOnRequest(MessageUpdatableContext context, boolean isOneWay) {
        boolean handlerResult;
        Map atts = (Map)context.get("javax.xml.ws.binding.attachments.outbound");
        AttachmentSet attSet = context.packet.getMessage().getAttachments();
        for (Map.Entry entry : atts.entrySet()) {
            String cid = (String)entry.getKey();
            if (attSet.get(cid) != null) continue;
            DataHandlerAttachment att = new DataHandlerAttachment(cid, (DataHandler)atts.get(cid));
            attSet.add(att);
        }
        try {
            handlerResult = this.processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, context, !isOneWay);
        }
        catch (WebServiceException wse) {
            this.remedyActionTaken = true;
            throw wse;
        }
        catch (RuntimeException re) {
            this.remedyActionTaken = true;
            throw new WebServiceException((Throwable)re);
        }
        if (!handlerResult) {
            this.remedyActionTaken = true;
        }
        return handlerResult;
    }

    @Override
    void closeHandlers(MessageContext mc) {
        this.closeClientsideHandlers(mc);
    }

    @Override
    void setUpProcessor() {
        if (this.handlers == null) {
            this.handlers = new ArrayList();
            HandlerConfiguration handlerConfig = ((BindingImpl)this.getBinding()).getHandlerConfig();
            List<MessageHandler> msgHandlersSnapShot = handlerConfig.getMessageHandlers();
            if (!msgHandlersSnapShot.isEmpty()) {
                this.handlers.addAll(msgHandlersSnapShot);
                this.roles = new HashSet<String>();
                this.roles.addAll(handlerConfig.getRoles());
                this.processor = new SOAPHandlerProcessor(true, this, this.getBinding(), this.handlers);
            }
        }
    }

    @Override
    MessageUpdatableContext getContext(Packet p) {
        MessageHandlerContextImpl context = new MessageHandlerContextImpl(this.seiModel, this.getBinding(), this.port, p, this.roles);
        return context;
    }
}

