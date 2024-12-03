/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.handler.LogicalHandler
 *  javax.xml.ws.handler.MessageContext
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.WSBinding;
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
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.MessageContext;

public class ClientLogicalHandlerTube
extends HandlerTube {
    private SEIModel seiModel;

    public ClientLogicalHandlerTube(WSBinding binding, SEIModel seiModel, WSDLPort port, Tube next) {
        super(next, port, binding);
        this.seiModel = seiModel;
    }

    public ClientLogicalHandlerTube(WSBinding binding, SEIModel seiModel, Tube next, HandlerTube cousinTube) {
        super(next, cousinTube, binding);
        this.seiModel = seiModel;
    }

    private ClientLogicalHandlerTube(ClientLogicalHandlerTube that, TubeCloner cloner) {
        super(that, cloner);
        this.seiModel = that.seiModel;
    }

    @Override
    protected void initiateClosing(MessageContext mc) {
        this.close(mc);
        super.initiateClosing(mc);
    }

    @Override
    public AbstractFilterTubeImpl copy(TubeCloner cloner) {
        return new ClientLogicalHandlerTube(this, cloner);
    }

    @Override
    void setUpProcessor() {
        if (this.handlers == null) {
            this.handlers = new ArrayList();
            WSBinding binding = this.getBinding();
            List<LogicalHandler> logicalSnapShot = ((BindingImpl)binding).getHandlerConfig().getLogicalHandlers();
            if (!logicalSnapShot.isEmpty()) {
                this.handlers.addAll(logicalSnapShot);
                this.processor = binding.getSOAPVersion() == null ? new XMLHandlerProcessor(this, binding, this.handlers) : new SOAPHandlerProcessor(true, this, binding, this.handlers);
            }
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
    void closeHandlers(MessageContext mc) {
        this.closeClientsideHandlers(mc);
    }
}

