/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.server.provider;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.server.provider.ProviderEndpointModel;
import com.sun.xml.ws.server.provider.SOAPProviderArgumentBuilder;
import com.sun.xml.ws.server.provider.XMLProviderArgumentBuilder;
import javax.xml.ws.soap.SOAPBinding;

public abstract class ProviderArgumentsBuilder<T> {
    protected abstract Message getResponseMessage(Exception var1);

    protected Packet getResponse(Packet request, Exception e, WSDLPort port, WSBinding binding) {
        Message message = this.getResponseMessage(e);
        Packet response = request.createServerResponse(message, port, null, binding);
        return response;
    }

    public abstract T getParameter(Packet var1);

    protected abstract Message getResponseMessage(T var1);

    protected Packet getResponse(Packet request, @Nullable T returnValue, WSDLPort port, WSBinding binding) {
        Message message = null;
        if (returnValue != null) {
            message = this.getResponseMessage(returnValue);
        }
        Packet response = request.createServerResponse(message, port, null, binding);
        return response;
    }

    public static ProviderArgumentsBuilder<?> create(ProviderEndpointModel model, WSBinding binding) {
        if (model.datatype == Packet.class) {
            return new PacketProviderArgumentsBuilder(binding.getSOAPVersion());
        }
        return binding instanceof SOAPBinding ? SOAPProviderArgumentBuilder.create(model, binding.getSOAPVersion()) : XMLProviderArgumentBuilder.createBuilder(model, binding);
    }

    private static class PacketProviderArgumentsBuilder
    extends ProviderArgumentsBuilder<Packet> {
        private final SOAPVersion soapVersion;

        public PacketProviderArgumentsBuilder(SOAPVersion soapVersion) {
            this.soapVersion = soapVersion;
        }

        @Override
        protected Message getResponseMessage(Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }

        @Override
        public Packet getParameter(Packet packet) {
            return packet;
        }

        @Override
        protected Message getResponseMessage(Packet returnValue) {
            throw new IllegalStateException();
        }

        @Override
        protected Packet getResponse(Packet request, @Nullable Packet returnValue, WSDLPort port, WSBinding binding) {
            return returnValue;
        }
    }
}

