/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPBodyElement
 *  javax.xml.soap.SOAPConstants
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 */
package com.sun.xml.messaging.saaj.soap.ver1_2;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.BodyImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_2.BodyElement1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Fault1_2Impl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Body1_2Impl
extends BodyImpl {
    protected static final Logger log = Logger.getLogger(Body1_2Impl.class.getName(), "com.sun.xml.messaging.saaj.soap.ver1_2.LocalStrings");

    public Body1_2Impl(SOAPDocumentImpl ownerDocument, String prefix) {
        super(ownerDocument, NameImpl.createBody1_2Name(prefix));
    }

    public Body1_2Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    protected NameImpl getFaultName(String name) {
        return NameImpl.createFault1_2Name(name, null);
    }

    @Override
    protected SOAPBodyElement createBodyElement(Name name) {
        return new BodyElement1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }

    @Override
    protected SOAPBodyElement createBodyElement(QName name) {
        return new BodyElement1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name);
    }

    @Override
    protected QName getDefaultFaultCode() {
        return SOAPConstants.SOAP_RECEIVER_FAULT;
    }

    @Override
    public SOAPFault addFault() throws SOAPException {
        if (this.hasAnyChildElement()) {
            log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addFault();
    }

    @Override
    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        log.severe("SAAJ0401.ver1_2.no.encodingstyle.in.body");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Body");
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

    @Override
    protected boolean isFault(SOAPElement child) {
        return child.getElementName().getURI().equals("http://www.w3.org/2003/05/soap-envelope") && child.getElementName().getLocalName().equals("Fault");
    }

    @Override
    protected SOAPFault createFaultElement() {
        return new Fault1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), this.getPrefix());
    }

    @Override
    public SOAPBodyElement addBodyElement(Name name) throws SOAPException {
        if (this.hasFault()) {
            log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addBodyElement(name);
    }

    @Override
    public SOAPBodyElement addBodyElement(QName name) throws SOAPException {
        if (this.hasFault()) {
            log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addBodyElement(name);
    }

    @Override
    protected SOAPElement addElement(Name name) throws SOAPException {
        if (this.hasFault()) {
            log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addElement(name);
    }

    @Override
    protected SOAPElement addElement(QName name) throws SOAPException {
        if (this.hasFault()) {
            log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addElement(name);
    }

    @Override
    public SOAPElement addChildElement(Name name) throws SOAPException {
        if (this.hasFault()) {
            log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addChildElement(name);
    }

    @Override
    public SOAPElement addChildElement(QName name) throws SOAPException {
        if (this.hasFault()) {
            log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
            throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
        }
        return super.addChildElement(name);
    }

    private boolean hasAnyChildElement() {
        for (Node currentNode = this.getFirstChild(); currentNode != null; currentNode = currentNode.getNextSibling()) {
            if (currentNode.getNodeType() != 1) continue;
            return true;
        }
        return false;
    }
}

