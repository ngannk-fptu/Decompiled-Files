/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.transport;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.developer.HttpConfigFeature;
import javax.xml.ws.WebServiceFeature;

public final class DeferredTransportPipe
extends AbstractTubeImpl {
    private Tube transport;
    private EndpointAddress address;
    private final ClassLoader classLoader;
    private final ClientTubeAssemblerContext context;

    public DeferredTransportPipe(ClassLoader classLoader, ClientTubeAssemblerContext context) {
        this.classLoader = classLoader;
        this.context = context;
        if (context.getBinding().getFeature(HttpConfigFeature.class) == null) {
            context.getBinding().getFeatures().mergeFeatures(new WebServiceFeature[]{new HttpConfigFeature()}, false);
        }
        try {
            this.transport = TransportTubeFactory.create(classLoader, context);
            this.address = context.getAddress();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public DeferredTransportPipe(DeferredTransportPipe that, TubeCloner cloner) {
        super(that, cloner);
        this.classLoader = that.classLoader;
        this.context = that.context;
        if (that.transport != null) {
            this.transport = cloner.copy(that.transport);
            this.address = that.address;
        }
    }

    @Override
    public NextAction processException(@NotNull Throwable t) {
        return this.transport.processException(t);
    }

    @Override
    public NextAction processRequest(@NotNull Packet request) {
        if (request.endpointAddress == this.address) {
            return this.transport.processRequest(request);
        }
        if (this.transport != null) {
            this.transport.preDestroy();
            this.transport = null;
            this.address = null;
        }
        ClientTubeAssemblerContext newContext = new ClientTubeAssemblerContext(request.endpointAddress, this.context.getWsdlModel(), this.context.getBindingProvider(), this.context.getBinding(), this.context.getContainer(), this.context.getCodec().copy(), this.context.getSEIModel(), this.context.getSEI());
        this.address = request.endpointAddress;
        this.transport = TransportTubeFactory.create(this.classLoader, newContext);
        assert (this.transport != null);
        return this.transport.processRequest(request);
    }

    @Override
    public NextAction processResponse(@NotNull Packet response) {
        if (this.transport != null) {
            return this.transport.processResponse(response);
        }
        return this.doReturnWith(response);
    }

    @Override
    public void preDestroy() {
        if (this.transport != null) {
            this.transport.preDestroy();
            this.transport = null;
            this.address = null;
        }
    }

    @Override
    public DeferredTransportPipe copy(TubeCloner cloner) {
        return new DeferredTransportPipe(this, cloner);
    }
}

