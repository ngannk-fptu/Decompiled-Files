/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.oracle.webservices.api.databinding.JavaCallInfo;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.client.RequestContext;
import com.sun.xml.ws.client.ResponseContextReceiver;
import com.sun.xml.ws.client.sei.MethodHandler;
import com.sun.xml.ws.client.sei.SEIStub;
import com.sun.xml.ws.client.sei.ValueGetterFactory;
import com.sun.xml.ws.encoding.soap.DeserializationException;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.resources.DispatchMessages;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

final class SyncMethodHandler
extends MethodHandler {
    final boolean isVoid;
    final boolean isOneway;
    final JavaMethodImpl javaMethod;

    SyncMethodHandler(SEIStub owner, JavaMethodImpl jm) {
        super(owner, jm.getMethod());
        this.javaMethod = jm;
        this.isVoid = Void.TYPE.equals(jm.getMethod().getReturnType());
        this.isOneway = jm.getMEP().isOneWay();
    }

    @Override
    Object invoke(Object proxy, Object[] args) throws Throwable {
        return this.invoke(proxy, args, this.owner.requestContext, this.owner);
    }

    Object invoke(Object proxy, Object[] args, RequestContext rc, ResponseContextReceiver receiver) throws Throwable {
        JavaCallInfo call = this.owner.databinding.createJavaCallInfo(this.method, args);
        Packet req = (Packet)this.owner.databinding.serializeRequest(call);
        Packet reply = this.owner.doProcess(req, rc, receiver);
        Message msg = reply.getMessage();
        if (msg == null) {
            if (!this.isOneway || !this.isVoid) {
                throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
            }
            return null;
        }
        try {
            call = this.owner.databinding.deserializeResponse(reply, call);
            if (call.getException() != null) {
                throw call.getException();
            }
            Object object = call.getReturnValue();
            return object;
        }
        catch (JAXBException e) {
            throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[]{e});
        }
        catch (XMLStreamException e) {
            throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), e);
        }
        finally {
            if (reply.transportBackChannel != null) {
                reply.transportBackChannel.close();
            }
        }
    }

    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.SYNC;
    }
}

