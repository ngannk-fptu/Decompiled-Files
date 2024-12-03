/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.protocol.soap;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.ExceptionHasMessage;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import javax.xml.namespace.QName;

public class MessageCreationException
extends ExceptionHasMessage {
    private final SOAPVersion soapVersion;

    public MessageCreationException(SOAPVersion soapVersion, Object ... args) {
        super("soap.msg.create.err", args);
        this.soapVersion = soapVersion;
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.soap";
    }

    @Override
    public Message getFaultMessage() {
        QName faultCode = this.soapVersion.faultCodeClient;
        return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.getLocalizedMessage(), faultCode);
    }
}

