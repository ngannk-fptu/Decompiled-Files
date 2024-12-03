/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.server.provider;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.server.provider.ProviderArgumentsBuilder;

final class MessageProviderArgumentBuilder
extends ProviderArgumentsBuilder<Message> {
    private final SOAPVersion soapVersion;

    public MessageProviderArgumentBuilder(SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }

    @Override
    public Message getParameter(Packet packet) {
        return packet.getMessage();
    }

    @Override
    protected Message getResponseMessage(Message returnValue) {
        return returnValue;
    }

    @Override
    protected Message getResponseMessage(Exception e) {
        return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
    }
}

