/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.ver1_2;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.HeaderElementImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public class HeaderElement1_2Impl
extends HeaderElementImpl {
    public HeaderElement1_2Impl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }

    public HeaderElement1_2Impl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public HeaderElement1_2Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    public SOAPElement setElementQName(QName newName) throws SOAPException {
        HeaderElement1_2Impl copy = new HeaderElement1_2Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
        return this.replaceElementWithSOAPElement((Element)((Object)this), copy);
    }

    @Override
    protected NameImpl getRoleAttributeName() {
        return NameImpl.create("role", null, "http://www.w3.org/2003/05/soap-envelope");
    }

    @Override
    protected NameImpl getActorAttributeName() {
        return this.getRoleAttributeName();
    }

    @Override
    protected NameImpl getMustunderstandAttributeName() {
        return NameImpl.create("mustUnderstand", null, "http://www.w3.org/2003/05/soap-envelope");
    }

    @Override
    protected String getMustunderstandLiteralValue(boolean mustUnderstand) {
        return mustUnderstand ? "true" : "false";
    }

    @Override
    protected boolean getMustunderstandAttributeValue(String mu) {
        return mu.equals("true") || mu.equals("1");
    }

    @Override
    protected NameImpl getRelayAttributeName() {
        return NameImpl.create("relay", null, "http://www.w3.org/2003/05/soap-envelope");
    }

    @Override
    protected String getRelayLiteralValue(boolean relay) {
        return relay ? "true" : "false";
    }

    @Override
    protected boolean getRelayAttributeValue(String relay) {
        return relay.equals("true") || relay.equals("1");
    }

    @Override
    protected String getActorOrRole() {
        return this.getRole();
    }
}

