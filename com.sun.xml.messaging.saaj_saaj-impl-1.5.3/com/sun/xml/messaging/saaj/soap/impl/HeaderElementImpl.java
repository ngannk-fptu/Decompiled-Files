/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPHeaderElement
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import org.w3c.dom.Element;

public abstract class HeaderElementImpl
extends ElementImpl
implements SOAPHeaderElement {
    protected static Name RELAY_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("relay");
    protected static Name MUST_UNDERSTAND_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("mustUnderstand");
    Name actorAttNameWithoutNS = NameImpl.createFromTagName("actor");
    Name roleAttNameWithoutNS = NameImpl.createFromTagName("role");

    public HeaderElementImpl(SOAPDocumentImpl ownerDoc, Name qname) {
        super(ownerDoc, qname);
    }

    public HeaderElementImpl(SOAPDocumentImpl ownerDoc, QName qname) {
        super(ownerDoc, qname);
    }

    public HeaderElementImpl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    protected abstract NameImpl getActorAttributeName();

    protected abstract NameImpl getRoleAttributeName();

    protected abstract NameImpl getMustunderstandAttributeName();

    protected abstract boolean getMustunderstandAttributeValue(String var1);

    protected abstract String getMustunderstandLiteralValue(boolean var1);

    protected abstract NameImpl getRelayAttributeName();

    protected abstract boolean getRelayAttributeValue(String var1);

    protected abstract String getRelayLiteralValue(boolean var1);

    protected abstract String getActorOrRole();

    @Override
    public void setParentElement(SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPHeader)) {
            log.severe("SAAJ0130.impl.header.elem.parent.mustbe.header");
            throw new SOAPException("Parent of a SOAPHeaderElement has to be a SOAPHeader");
        }
        super.setParentElement(element);
    }

    public void setActor(String actorUri) {
        try {
            this.removeAttribute(this.getActorAttributeName());
            this.addAttribute(this.getActorAttributeName(), actorUri);
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
    }

    public void setRole(String roleUri) throws SOAPException {
        this.removeAttribute(this.getRoleAttributeName());
        this.addAttribute(this.getRoleAttributeName(), roleUri);
    }

    public String getActor() {
        String actor = this.getAttributeValue(this.getActorAttributeName());
        return actor;
    }

    public String getRole() {
        String role = this.getAttributeValue(this.getRoleAttributeName());
        return role;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        try {
            this.removeAttribute(this.getMustunderstandAttributeName());
            this.addAttribute(this.getMustunderstandAttributeName(), this.getMustunderstandLiteralValue(mustUnderstand));
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
    }

    public boolean getMustUnderstand() {
        String mu = this.getAttributeValue(this.getMustunderstandAttributeName());
        if (mu != null) {
            return this.getMustunderstandAttributeValue(mu);
        }
        return false;
    }

    public void setRelay(boolean relay) throws SOAPException {
        this.removeAttribute(this.getRelayAttributeName());
        this.addAttribute(this.getRelayAttributeName(), this.getRelayLiteralValue(relay));
    }

    public boolean getRelay() {
        String mu = this.getAttributeValue(this.getRelayAttributeName());
        if (mu != null) {
            return this.getRelayAttributeValue(mu);
        }
        return false;
    }
}

