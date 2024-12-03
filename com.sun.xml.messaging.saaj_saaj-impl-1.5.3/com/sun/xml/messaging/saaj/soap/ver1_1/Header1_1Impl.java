/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeaderElement
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.HeaderImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.HeaderElement1_1Impl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import org.w3c.dom.Element;

public class Header1_1Impl
extends HeaderImpl {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.ver1_1", "com.sun.xml.messaging.saaj.soap.ver1_1.LocalStrings");

    public Header1_1Impl(SOAPDocumentImpl ownerDocument, String prefix) {
        super(ownerDocument, NameImpl.createHeader1_1Name(prefix));
    }

    public Header1_1Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    protected NameImpl getNotUnderstoodName() {
        log.log(Level.SEVERE, "SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", new String[]{"getNotUnderstoodName"});
        throw new UnsupportedOperationException("Not supported by SOAP 1.1");
    }

    @Override
    protected NameImpl getUpgradeName() {
        return NameImpl.create("Upgrade", this.getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");
    }

    @Override
    protected NameImpl getSupportedEnvelopeName() {
        return NameImpl.create("SupportedEnvelope", this.getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");
    }

    public SOAPHeaderElement addNotUnderstoodHeaderElement(QName name) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", new String[]{"addNotUnderstoodHeaderElement"});
        throw new UnsupportedOperationException("Not supported by SOAP 1.1");
    }

    @Override
    protected SOAPHeaderElement createHeaderElement(Name name) {
        return new HeaderElement1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }

    @Override
    protected SOAPHeaderElement createHeaderElement(QName name) {
        return new HeaderElement1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }
}

