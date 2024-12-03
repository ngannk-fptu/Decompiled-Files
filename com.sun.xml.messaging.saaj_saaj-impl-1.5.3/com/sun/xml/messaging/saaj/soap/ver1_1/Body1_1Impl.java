/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPBodyElement
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPFault
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.BodyImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.BodyElement1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Fault1_1Impl;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Element;

public class Body1_1Impl
extends BodyImpl {
    public Body1_1Impl(SOAPDocumentImpl ownerDocument, String prefix) {
        super(ownerDocument, NameImpl.createBody1_1Name(prefix));
    }

    public Body1_1Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    public SOAPFault addSOAP12Fault(QName faultCode, String faultReason, Locale locale) {
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    @Override
    protected NameImpl getFaultName(String name) {
        return NameImpl.createFault1_1Name(null);
    }

    @Override
    protected SOAPBodyElement createBodyElement(Name name) {
        return new BodyElement1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }

    @Override
    protected SOAPBodyElement createBodyElement(QName name) {
        return new BodyElement1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }

    @Override
    protected QName getDefaultFaultCode() {
        return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
    }

    @Override
    protected boolean isFault(SOAPElement child) {
        return child.getElementName().equals(this.getFaultName(null));
    }

    @Override
    protected SOAPFault createFaultElement() {
        return new Fault1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), this.getPrefix());
    }
}

