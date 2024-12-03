/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MimeHeader
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.dispatch;

import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.client.dispatch.DispatchImpl;
import com.sun.xml.ws.resources.DispatchMessages;
import com.sun.xml.ws.transport.Headers;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class SOAPMessageDispatch
extends DispatchImpl<SOAPMessage> {
    @Deprecated
    public SOAPMessageDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
    }

    public SOAPMessageDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
    }

    @Override
    Packet createPacket(SOAPMessage arg) {
        Iterator iter = arg.getMimeHeaders().getAllHeaders();
        Headers ch = new Headers();
        while (iter.hasNext()) {
            MimeHeader mh = (MimeHeader)iter.next();
            ch.add(mh.getName(), mh.getValue());
        }
        Packet packet = new Packet(SAAJFactory.create(arg));
        packet.invocationProperties.put("javax.xml.ws.http.request.headers", ch);
        return packet;
    }

    @Override
    SOAPMessage toReturnValue(Packet response) {
        try {
            if (response == null || response.getMessage() == null) {
                throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
            }
            return response.getMessage().readAsSOAPMessage();
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }
}

