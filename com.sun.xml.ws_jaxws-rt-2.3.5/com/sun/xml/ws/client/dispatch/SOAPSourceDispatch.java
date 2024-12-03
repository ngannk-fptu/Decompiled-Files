/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
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
import com.sun.xml.ws.message.source.PayloadSourceMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

final class SOAPSourceDispatch
extends DispatchImpl<Source> {
    @Deprecated
    public SOAPSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
        assert (!SOAPSourceDispatch.isXMLHttp(binding));
    }

    public SOAPSourceDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        assert (!SOAPSourceDispatch.isXMLHttp(binding));
    }

    @Override
    Source toReturnValue(Packet response) {
        Message msg = response.getMessage();
        switch (this.mode) {
            case PAYLOAD: {
                return msg.readPayloadAsSource();
            }
            case MESSAGE: {
                return msg.readEnvelopeAsSource();
            }
        }
        throw new WebServiceException("Unrecognized dispatch mode");
    }

    @Override
    Packet createPacket(Source msg) {
        Message message;
        if (msg == null) {
            message = Messages.createEmpty(this.soapVersion);
        } else {
            switch (this.mode) {
                case PAYLOAD: {
                    message = new PayloadSourceMessage(null, msg, this.setOutboundAttachments(), this.soapVersion);
                    break;
                }
                case MESSAGE: {
                    message = Messages.create(msg, this.soapVersion);
                    break;
                }
                default: {
                    throw new WebServiceException("Unrecognized message mode");
                }
            }
        }
        return new Packet(message);
    }
}

