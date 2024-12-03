/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.JAXBRIContext
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.dispatch;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.client.dispatch.DispatchImpl;
import com.sun.xml.ws.message.jaxb.JAXBDispatchMessage;
import com.sun.xml.ws.spi.db.BindingContextFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class JAXBDispatch
extends DispatchImpl<Object> {
    private final JAXBContext jaxbcontext;
    private final boolean isContextSupported;

    @Deprecated
    public JAXBDispatch(QName port, JAXBContext jc, Service.Mode mode, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, service, pipe, binding, epr);
        this.jaxbcontext = jc;
        this.isContextSupported = BindingContextFactory.isContextSupported(jc);
    }

    public JAXBDispatch(WSPortInfo portInfo, JAXBContext jc, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        this.jaxbcontext = jc;
        this.isContextSupported = BindingContextFactory.isContextSupported(jc);
    }

    @Override
    Object toReturnValue(Packet response) {
        try {
            Unmarshaller unmarshaller = this.jaxbcontext.createUnmarshaller();
            Message msg = response.getMessage();
            switch (this.mode) {
                case PAYLOAD: {
                    return msg.readPayloadAsJAXB(unmarshaller);
                }
                case MESSAGE: {
                    Source result = msg.readEnvelopeAsSource();
                    return unmarshaller.unmarshal(result);
                }
            }
            throw new WebServiceException("Unrecognized dispatch mode");
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    Packet createPacket(Object msg) {
        assert (this.jaxbcontext != null);
        Message message = this.mode == Service.Mode.MESSAGE ? (this.isContextSupported ? new JAXBDispatchMessage(BindingContextFactory.create(this.jaxbcontext), msg, this.soapVersion) : new JAXBDispatchMessage(this.jaxbcontext, msg, this.soapVersion)) : (msg == null ? Messages.createEmpty(this.soapVersion) : (this.isContextSupported ? Messages.create(this.jaxbcontext, msg, this.soapVersion) : Messages.createRaw(this.jaxbcontext, msg, this.soapVersion)));
        return new Packet(message);
    }

    @Override
    public void setOutboundHeaders(Object ... headers) {
        if (headers == null) {
            throw new IllegalArgumentException();
        }
        Header[] hl = new Header[headers.length];
        for (int i = 0; i < hl.length; ++i) {
            if (headers[i] == null) {
                throw new IllegalArgumentException();
            }
            hl[i] = Headers.create((JAXBContext)((JAXBRIContext)this.jaxbcontext), headers[i]);
        }
        super.setOutboundHeaders(hl);
    }
}

