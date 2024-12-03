/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPConstants
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.ver1_2;

import com.sun.xml.messaging.saaj.soap.Envelope;
import com.sun.xml.messaging.saaj.soap.EnvelopeFactory;
import com.sun.xml.messaging.saaj.soap.MessageImpl;
import com.sun.xml.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Envelope1_2Impl;
import com.sun.xml.messaging.saaj.util.XMLDeclarationParser;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;

public class SOAPPart1_2Impl
extends SOAPPartImpl
implements SOAPConstants {
    protected static final Logger log = Logger.getLogger(SOAPPart1_2Impl.class.getName(), "com.sun.xml.messaging.saaj.soap.ver1_2.LocalStrings");

    public SOAPPart1_2Impl() {
    }

    public SOAPPart1_2Impl(MessageImpl message) {
        super(message);
    }

    @Override
    protected String getContentType() {
        return "application/soap+xml";
    }

    @Override
    protected Envelope createEmptyEnvelope(String prefix) throws SOAPException {
        return new Envelope1_2Impl(this.getDocument(), prefix, true, true);
    }

    @Override
    protected Envelope createEnvelopeFromSource() throws SOAPException {
        XMLDeclarationParser parser = this.lookForXmlDecl();
        Source tmp = this.source;
        this.source = null;
        EnvelopeImpl envelope = (EnvelopeImpl)EnvelopeFactory.createEnvelope(tmp, this);
        if (!envelope.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            log.severe("SAAJ0415.ver1_2.msg.invalid.soap1.2");
            throw new SOAPException("InputStream does not represent a valid SOAP 1.2 Message");
        }
        if (parser != null && !this.omitXmlDecl) {
            envelope.setOmitXmlDecl("no");
            envelope.setXmlDecl(parser.getXmlDeclaration());
            envelope.setCharsetEncoding(parser.getEncoding());
        }
        return envelope;
    }

    @Override
    protected SOAPPartImpl duplicateType() {
        return new SOAPPart1_2Impl();
    }

    @Override
    public String getSOAPNamespace() {
        return "http://www.w3.org/2003/05/soap-envelope";
    }
}

