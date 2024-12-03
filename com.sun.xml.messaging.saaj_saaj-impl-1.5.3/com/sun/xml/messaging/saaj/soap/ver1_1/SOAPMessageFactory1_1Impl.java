/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.MessageFactoryImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Message1_1Impl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class SOAPMessageFactory1_1Impl
extends MessageFactoryImpl {
    @Override
    public SOAPMessage createMessage() throws SOAPException {
        return new Message1_1Impl();
    }

    @Override
    public SOAPMessage createMessage(boolean isFastInfoset, boolean acceptFastInfoset) throws SOAPException {
        return new Message1_1Impl(isFastInfoset, acceptFastInfoset);
    }

    @Override
    public SOAPMessage createMessage(MimeHeaders headers, InputStream in) throws IOException, SOAPExceptionImpl {
        if (headers == null) {
            headers = new MimeHeaders();
        }
        if (SOAPMessageFactory1_1Impl.getContentType(headers) == null) {
            headers.setHeader("Content-Type", "text/xml");
        }
        Message1_1Impl msg = new Message1_1Impl(headers, in);
        msg.setLazyAttachments(this.lazyAttachments);
        return msg;
    }
}

