/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPConstants
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.soap.SOAPPart
 */
package com.sun.xml.messaging.saaj.soap.ver1_2;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.soap.MessageImpl;
import com.sun.xml.messaging.saaj.soap.ver1_2.SOAPPart1_2Impl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamReader;

public class Message1_2Impl
extends MessageImpl
implements SOAPConstants {
    public Message1_2Impl() {
    }

    public Message1_2Impl(SOAPMessage msg) {
        super(msg);
    }

    public Message1_2Impl(boolean isFastInfoset, boolean acceptFastInfoset) {
        super(isFastInfoset, acceptFastInfoset);
    }

    public Message1_2Impl(MimeHeaders headers, InputStream in) throws IOException, SOAPExceptionImpl {
        super(headers, in);
    }

    public Message1_2Impl(MimeHeaders headers, ContentType ct, int stat, InputStream in) throws SOAPExceptionImpl {
        super(headers, ct, stat, in);
    }

    public Message1_2Impl(MimeHeaders headers, ContentType ct, int stat, XMLStreamReader reader) throws SOAPExceptionImpl {
        super(headers, ct, stat, reader);
    }

    @Override
    public SOAPPart getSOAPPart() {
        if (this.soapPartImpl == null) {
            this.soapPartImpl = new SOAPPart1_2Impl(this);
        }
        return this.soapPartImpl;
    }

    @Override
    protected boolean isCorrectSoapVersion(int contentTypeId) {
        return (contentTypeId & 8) != 0;
    }

    @Override
    protected String getExpectedContentType() {
        return this.isFastInfoset ? "application/soap+fastinfoset" : "application/soap+xml";
    }

    @Override
    protected String getExpectedAcceptHeader() {
        String accept = "application/soap+xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        return this.acceptFastInfoset ? "application/soap+fastinfoset, " + accept : accept;
    }
}

