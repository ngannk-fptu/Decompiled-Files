/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.soap.MimeHeader
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server.provider;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.server.provider.MessageProviderArgumentBuilder;
import com.sun.xml.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.ws.server.provider.ProviderEndpointModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

abstract class SOAPProviderArgumentBuilder<T>
extends ProviderArgumentsBuilder<T> {
    protected final SOAPVersion soapVersion;

    private SOAPProviderArgumentBuilder(SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }

    static ProviderArgumentsBuilder create(ProviderEndpointModel model, SOAPVersion soapVersion) {
        if (model.mode == Service.Mode.PAYLOAD) {
            return new PayloadSource(soapVersion);
        }
        if (model.datatype == Source.class) {
            return new MessageSource(soapVersion);
        }
        if (model.datatype == SOAPMessage.class) {
            return new SOAPMessageParameter(soapVersion);
        }
        if (model.datatype == Message.class) {
            return new MessageProviderArgumentBuilder(soapVersion);
        }
        throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(model.implClass, model.datatype));
    }

    private static final class SOAPMessageParameter
    extends SOAPProviderArgumentBuilder<SOAPMessage> {
        SOAPMessageParameter(SOAPVersion soapVersion) {
            super(soapVersion);
        }

        @Override
        public SOAPMessage getParameter(Packet packet) {
            try {
                return packet.getMessage().readAsSOAPMessage(packet, true);
            }
            catch (SOAPException se) {
                throw new WebServiceException((Throwable)se);
            }
        }

        @Override
        protected Message getResponseMessage(SOAPMessage soapMsg) {
            return Messages.create(soapMsg);
        }

        @Override
        protected Message getResponseMessage(Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }

        @Override
        protected Packet getResponse(Packet request, @Nullable SOAPMessage returnValue, WSDLPort port, WSBinding binding) {
            Packet response = super.getResponse(request, returnValue, port, binding);
            if (returnValue != null && response.supports("com.sun.xml.ws.api.message.packet.outbound.transport.headers")) {
                MimeHeaders hdrs = returnValue.getMimeHeaders();
                HashMap<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
                Iterator i = hdrs.getAllHeaders();
                while (i.hasNext()) {
                    MimeHeader header = (MimeHeader)i.next();
                    if (header.getName().equalsIgnoreCase("SOAPAction")) continue;
                    ArrayList<String> list = (ArrayList<String>)headers.get(header.getName());
                    if (list == null) {
                        list = new ArrayList<String>();
                        headers.put(header.getName(), list);
                    }
                    list.add(header.getValue());
                }
                response.put("com.sun.xml.ws.api.message.packet.outbound.transport.headers", headers);
            }
            return response;
        }
    }

    private static final class MessageSource
    extends SOAPProviderArgumentBuilder<Source> {
        MessageSource(SOAPVersion soapVersion) {
            super(soapVersion);
        }

        @Override
        public Source getParameter(Packet packet) {
            return packet.getMessage().readEnvelopeAsSource();
        }

        @Override
        protected Message getResponseMessage(Source source) {
            return Messages.create(source, this.soapVersion);
        }

        @Override
        protected Message getResponseMessage(Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }
    }

    private static final class PayloadSource
    extends SOAPProviderArgumentBuilder<Source> {
        PayloadSource(SOAPVersion soapVersion) {
            super(soapVersion);
        }

        @Override
        public Source getParameter(Packet packet) {
            return packet.getMessage().readPayloadAsSource();
        }

        @Override
        protected Message getResponseMessage(Source source) {
            return Messages.createUsingPayload(source, this.soapVersion);
        }

        @Override
        protected Message getResponseMessage(Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }
    }
}

