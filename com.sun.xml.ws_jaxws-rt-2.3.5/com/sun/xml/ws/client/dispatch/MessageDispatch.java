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
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.client.dispatch.DispatchImpl;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class MessageDispatch
extends DispatchImpl<Message> {
    @Deprecated
    public MessageDispatch(QName port, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, Service.Mode.MESSAGE, service, pipe, binding, epr);
    }

    public MessageDispatch(WSPortInfo portInfo, BindingImpl binding, WSEndpointReference epr) {
        super(portInfo, Service.Mode.MESSAGE, binding, epr, true);
    }

    @Override
    Message toReturnValue(Packet response) {
        return response.getMessage();
    }

    @Override
    Packet createPacket(Message msg) {
        return new Packet(msg);
    }
}

