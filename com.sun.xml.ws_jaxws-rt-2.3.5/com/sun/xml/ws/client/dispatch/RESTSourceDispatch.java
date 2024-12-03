/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Service$Mode
 */
package com.sun.xml.ws.client.dispatch;

import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.client.dispatch.DispatchImpl;
import com.sun.xml.ws.encoding.xml.XMLMessage;
import com.sun.xml.ws.message.source.PayloadSourceMessage;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;

final class RESTSourceDispatch
extends DispatchImpl<Source> {
    @Deprecated
    public RESTSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
        assert (RESTSourceDispatch.isXMLHttp(binding));
    }

    public RESTSourceDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        assert (RESTSourceDispatch.isXMLHttp(binding));
    }

    @Override
    Source toReturnValue(Packet response) {
        Message msg = response.getMessage();
        try {
            return new StreamSource(XMLMessage.getDataSource(msg, this.binding.getFeatures()).getInputStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    Packet createPacket(Source msg) {
        Message message = msg == null ? Messages.createEmpty(this.soapVersion) : new PayloadSourceMessage(null, msg, this.setOutboundAttachments(), this.soapVersion);
        return new Packet(message);
    }
}

