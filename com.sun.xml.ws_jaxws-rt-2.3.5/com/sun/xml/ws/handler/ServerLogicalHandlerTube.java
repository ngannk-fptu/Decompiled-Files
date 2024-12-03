/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.handler.LogicalHandler
 *  javax.xml.ws.handler.MessageContext
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.handler.HandlerProcessor;
import com.sun.xml.ws.handler.HandlerTube;
import com.sun.xml.ws.handler.LogicalMessageContextImpl;
import com.sun.xml.ws.handler.MessageUpdatableContext;
import com.sun.xml.ws.handler.SOAPHandlerProcessor;
import com.sun.xml.ws.handler.XMLHandlerProcessor;
import com.sun.xml.ws.message.DataHandlerAttachment;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.MessageContext;

public class ServerLogicalHandlerTube
extends HandlerTube {
    private SEIModel seiModel;

    public ServerLogicalHandlerTube(WSBinding binding, SEIModel seiModel, WSDLPort port, Tube next) {
        super(next, port, binding);
        this.seiModel = seiModel;
        this.setUpHandlersOnce();
    }

    public ServerLogicalHandlerTube(WSBinding binding, SEIModel seiModel, Tube next, HandlerTube cousinTube) {
        super(next, cousinTube, binding);
        this.seiModel = seiModel;
        this.setUpHandlersOnce();
    }

    private ServerLogicalHandlerTube(ServerLogicalHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
        this.handlers = that.handlers;
    }

    @Override
    protected void initiateClosing(MessageContext mc) {
        if (this.getBinding().getSOAPVersion() != null) {
            super.initiateClosing(mc);
        } else {
            this.close(mc);
            super.initiateClosing(mc);
        }
    }

    @Override
    public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ServerLogicalHandlerTube(this, cloner);
    }

    private void setUpHandlersOnce() {
        this.handlers = new ArrayList();
        List<LogicalHandler> logicalSnapShot = ((BindingImpl)this.getBinding()).getHandlerConfig().getLogicalHandlers();
        if (!logicalSnapShot.isEmpty()) {
            this.handlers.addAll(logicalSnapShot);
        }
    }

    @Override
    protected void resetProcessor() {
        this.processor = null;
    }

    @Override
    void setUpProcessor() {
        if (!this.handlers.isEmpty() && this.processor == null) {
            this.processor = this.getBinding().getSOAPVersion() == null ? new XMLHandlerProcessor(this, this.getBinding(), this.handlers) : new SOAPHandlerProcessor(false, this, this.getBinding(), this.handlers);
        }
    }

    @Override
    MessageUpdatableContext getContext(Packet packet) {
        return new LogicalMessageContextImpl(this.getBinding(), this.getBindingContext(), packet);
    }

    private BindingContext getBindingContext() {
        return this.seiModel != null && this.seiModel instanceof AbstractSEIModelImpl ? ((AbstractSEIModelImpl)this.seiModel).getBindingContext() : null;
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
    void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
        Map atts = (Map)context.get("javax.xml.ws.binding.attachments.outbound");
        AttachmentSet attSet = context.packet.getMessage().getAttachments();
        for (Map.Entry entry : atts.entrySet()) {
            String cid = (String)entry.getKey();
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
    void closeHandlers(MessageContext mc) {
        this.closeServersideHandlers(mc);
    }
}

