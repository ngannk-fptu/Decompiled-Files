/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.HeaderElementImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public class HeaderElement1_1Impl
extends HeaderElementImpl {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.ver1_1", "com.sun.xml.messaging.saaj.soap.ver1_1.LocalStrings");

    public HeaderElement1_1Impl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }

    public HeaderElement1_1Impl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public HeaderElement1_1Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    public SOAPElement setElementQName(QName newName) throws SOAPException {
        HeaderElement1_1Impl copy = new HeaderElement1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
        return this.replaceElementWithSOAPElement((Element)((Object)this), copy);
    }

    @Override
    protected NameImpl getActorAttributeName() {
        return NameImpl.create("actor", null, "http://schemas.xmlsoap.org/soap/envelope/");
    }

    @Override
    protected NameImpl getRoleAttributeName() {
        log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[]{"Role"});
        throw new UnsupportedOperationException("Role not supported by SOAP 1.1");
    }

    @Override
    protected NameImpl getMustunderstandAttributeName() {
        return NameImpl.create("mustUnderstand", null, "http://schemas.xmlsoap.org/soap/envelope/");
    }

    @Override
    protected String getMustunderstandLiteralValue(boolean mustUnderstand) {
        return mustUnderstand ? "1" : "0";
    }

    @Override
    protected boolean getMustunderstandAttributeValue(String mu) {
        return "1".equals(mu) || "true".equalsIgnoreCase(mu);
    }

    @Override
    protected NameImpl getRelayAttributeName() {
        log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[]{"Relay"});
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }

    @Override
    protected String getRelayLiteralValue(boolean relayAttr) {
        log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[]{"Relay"});
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }

    @Override
    protected boolean getRelayAttributeValue(String mu) {
        log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[]{"Relay"});
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }

    @Override
    protected String getActorOrRole() {
        return this.getActor();
    }
}

