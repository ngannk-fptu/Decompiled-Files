/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.soap;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.Message;
import org.apache.axis.message.SOAPEnvelope;

public class MessageFactoryImpl
extends MessageFactory {
    public SOAPMessage createMessage() throws SOAPException {
        SOAPEnvelope env = new SOAPEnvelope();
        env.setSAAJEncodingCompliance(true);
        Message message = new Message(env);
        message.setMessageType("request");
        return message;
    }

    public SOAPMessage createMessage(MimeHeaders mimeheaders, InputStream inputstream) throws IOException, SOAPException {
        Message message = new Message(inputstream, false, mimeheaders);
        message.setMessageType("request");
        return message;
    }
}

