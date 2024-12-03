/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.server.provider;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.ws.server.provider.ProviderInvokerTube;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncProviderInvokerTube<T>
extends ProviderInvokerTube<T> {
    private static final Logger LOGGER = Logger.getLogger("com.sun.xml.ws.server.SyncProviderInvokerTube");

    public SyncProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker, argsBuilder);
    }

    @Override
    public NextAction processRequest(Packet request) {
        Packet response;
        ThrowableContainerPropertySet tc;
        Object returnValue;
        WSDLPort port = this.getEndpoint().getPort();
        WSBinding binding = this.getEndpoint().getBinding();
        Object param = this.argsBuilder.getParameter(request);
        LOGGER.fine("Invoking Provider Endpoint");
        try {
            returnValue = this.getInvoker(request).invokeProvider(request, param);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            Packet response2 = this.argsBuilder.getResponse(request, e, port, binding);
            return this.doReturnWith(response2);
        }
        if (returnValue == null && request.transportBackChannel != null) {
            request.transportBackChannel.close();
        }
        Throwable t = (tc = (response = this.argsBuilder.getResponse(request, returnValue, port, binding)).getSatellite(ThrowableContainerPropertySet.class)) != null ? tc.getThrowable() : null;
        return t != null ? this.doThrow(response, t) : this.doReturnWith(response);
    }

    @Override
    @NotNull
    public NextAction processResponse(@NotNull Packet response) {
        return this.doReturnWith(response);
    }

    @Override
    @NotNull
    public NextAction processException(@NotNull Throwable t) {
        return this.doThrow(t);
    }
}

