/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeaderElement
 */
package com.sun.xml.messaging.saaj.soap.ver1_2;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.HeaderImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_2.HeaderElement1_2Impl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import org.w3c.dom.Element;

public class Header1_2Impl
extends HeaderImpl {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.ver1_2", "com.sun.xml.messaging.saaj.soap.ver1_2.LocalStrings");

    public Header1_2Impl(SOAPDocumentImpl ownerDocument, String prefix) {
        super(ownerDocument, NameImpl.createHeader1_2Name(prefix));
    }

    public Header1_2Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    protected NameImpl getNotUnderstoodName() {
        return NameImpl.createNotUnderstood1_2Name(null);
    }

    @Override
    protected NameImpl getUpgradeName() {
        return NameImpl.createUpgrade1_2Name(null);
    }

    @Override
    protected NameImpl getSupportedEnvelopeName() {
        return NameImpl.createSupportedEnvelope1_2Name(null);
    }

    public SOAPHeaderElement addNotUnderstoodHeaderElement(QName sourceName) throws SOAPException {
        if (sourceName == null) {
            log.severe("SAAJ0410.ver1_2.no.null.to.addNotUnderstoodHeader");
            throw new SOAPException("Cannot pass NULL to addNotUnderstoodHeaderElement");
        }
        if ("".equals(sourceName.getNamespaceURI())) {
            log.severe("SAAJ0417.ver1_2.qname.not.ns.qualified");
            throw new SOAPException("The qname passed to addNotUnderstoodHeaderElement must be namespace-qualified");
        }
        String prefix = sourceName.getPrefix();
        if ("".equals(prefix)) {
            prefix = "ns1";
        }
        NameImpl notunderstoodName = this.getNotUnderstoodName();
        SOAPHeaderElement notunderstoodHeaderElement = (SOAPHeaderElement)this.addChildElement(notunderstoodName);
        notunderstoodHeaderElement.addAttribute((Name)NameImpl.createFromUnqualifiedName("qname"), Header1_2Impl.getQualifiedName(new QName(sourceName.getNamespaceURI(), sourceName.getLocalPart(), prefix)));
        notunderstoodHeaderElement.addNamespaceDeclaration(prefix, sourceName.getNamespaceURI());
        return notunderstoodHeaderElement;
    }

    @Override
    public SOAPElement addTextNode(String text) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", this.getElementQName());
        throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Header is not legal");
    }

    @Override
    protected SOAPHeaderElement createHeaderElement(Name name) throws SOAPException {
        String uri = name.getURI();
        if (uri == null || uri.equals("")) {
            log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
            throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
        }
        return new HeaderElement1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }

    @Override
    protected SOAPHeaderElement createHeaderElement(QName name) throws SOAPException {
        String uri = name.getNamespaceURI();
        if (uri == null || uri.equals("")) {
            log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
            throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
        }
        return new HeaderElement1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }

    @Override
    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        log.severe("SAAJ0409.ver1_2.no.encodingstyle.in.header");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Header");
    }

    @Override
    public SOAPElement addAttribute(Name name, String value) throws SOAPException {
        if (name.getLocalName().equals("encodingStyle") && name.getURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            this.setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }

    @Override
    public SOAPElement addAttribute(QName name, String value) throws SOAPException {
        if (name.getLocalPart().equals("encodingStyle") && name.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            this.setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }
}

