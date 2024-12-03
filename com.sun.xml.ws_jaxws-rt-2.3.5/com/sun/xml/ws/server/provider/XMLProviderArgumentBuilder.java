/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.http.HTTPException
 */
package com.sun.xml.ws.server.provider;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.encoding.xml.XMLMessage;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.ws.server.provider.ProviderEndpointModel;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPException;

abstract class XMLProviderArgumentBuilder<T>
extends ProviderArgumentsBuilder<T> {
    XMLProviderArgumentBuilder() {
    }

    @Override
    protected Packet getResponse(Packet request, Exception e, WSDLPort port, WSBinding binding) {
        Packet response = super.getResponse(request, e, port, binding);
        if (e instanceof HTTPException && response.supports("javax.xml.ws.http.response.code")) {
            response.put("javax.xml.ws.http.response.code", ((HTTPException)e).getStatusCode());
        }
        return response;
    }

    static XMLProviderArgumentBuilder createBuilder(ProviderEndpointModel model, WSBinding binding) {
        if (model.mode == Service.Mode.PAYLOAD) {
            return new PayloadSource();
        }
        if (model.datatype == Source.class) {
            return new PayloadSource();
        }
        if (model.datatype == DataSource.class) {
            return new DataSourceParameter(binding);
        }
        throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(model.implClass, model.datatype));
    }

    private static final class DataSourceParameter
    extends XMLProviderArgumentBuilder<DataSource> {
        private final WSBinding binding;

        DataSourceParameter(WSBinding binding) {
            this.binding = binding;
        }

        @Override
        public DataSource getParameter(Packet packet) {
            Message msg = packet.getInternalMessage();
            return msg instanceof XMLMessage.MessageDataSource ? ((XMLMessage.MessageDataSource)((Object)msg)).getDataSource() : XMLMessage.getDataSource(msg, this.binding.getFeatures());
        }

        @Override
        public Message getResponseMessage(DataSource ds) {
            return XMLMessage.create(ds, this.binding.getFeatures());
        }

        @Override
        protected Message getResponseMessage(Exception e) {
            return XMLMessage.create(e);
        }
    }

    private static final class PayloadSource
    extends XMLProviderArgumentBuilder<Source> {
        private PayloadSource() {
        }

        @Override
        public Source getParameter(Packet packet) {
            return packet.getMessage().readPayloadAsSource();
        }

        @Override
        public Message getResponseMessage(Source source) {
            return Messages.createUsingPayload(source, SOAPVersion.SOAP_11);
        }

        @Override
        protected Message getResponseMessage(Exception e) {
            return XMLMessage.create(e);
        }
    }
}

