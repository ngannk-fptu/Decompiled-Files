/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPConstants
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.soap.SOAPPart
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.soap.MessageImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamReader;

public class Message1_1Impl
extends MessageImpl
implements SOAPConstants {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.ver1_1", "com.sun.xml.messaging.saaj.soap.ver1_1.LocalStrings");

    public Message1_1Impl() {
    }

    public Message1_1Impl(boolean isFastInfoset, boolean acceptFastInfoset) {
        super(isFastInfoset, acceptFastInfoset);
    }

    public Message1_1Impl(SOAPMessage msg) {
        super(msg);
    }

    public Message1_1Impl(MimeHeaders headers, InputStream in) throws IOException, SOAPExceptionImpl {
        super(headers, in);
    }

    public Message1_1Impl(MimeHeaders headers, ContentType ct, int stat, InputStream in) throws SOAPExceptionImpl {
        super(headers, ct, stat, in);
    }

    public Message1_1Impl(MimeHeaders headers, ContentType ct, int stat, XMLStreamReader reader) throws SOAPExceptionImpl {
        super(headers, ct, stat, reader);
    }

    @Override
    public SOAPPart getSOAPPart() {
        if (this.soapPartImpl == null) {
            this.soapPartImpl = new SOAPPart1_1Impl(this);
        }
        return this.soapPartImpl;
    }

    @Override
    protected boolean isCorrectSoapVersion(int contentTypeId) {
        return (contentTypeId & 4) != 0;
    }

    @Override
    public String getAction() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[]{"Action"});
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }

    @Override
    public void setAction(String type) {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[]{"Action"});
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }

    @Override
    public String getCharset() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[]{"Charset"});
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }

    @Override
    public void setCharset(String charset) {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[]{"Charset"});
        throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
    }

    @Override
    protected String getExpectedContentType() {
        return this.isFastInfoset ? "application/fastinfoset" : "text/xml";
    }

    @Override
    protected String getExpectedAcceptHeader() {
        String accept = "text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        return this.acceptFastInfoset ? "application/fastinfoset, " + accept : accept;
    }
}

