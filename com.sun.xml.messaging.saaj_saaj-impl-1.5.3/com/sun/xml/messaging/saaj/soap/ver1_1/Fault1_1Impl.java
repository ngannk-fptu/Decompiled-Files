/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Detail1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.FaultElement1_1Impl;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Fault1_1Impl
extends FaultImpl {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.ver1_1", "com.sun.xml.messaging.saaj.soap.ver1_1.LocalStrings");

    public Fault1_1Impl(SOAPDocumentImpl ownerDocument, String prefix) {
        super(ownerDocument, NameImpl.createFault1_1Name(prefix));
    }

    public Fault1_1Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    public Fault1_1Impl(SOAPDocumentImpl ownerDoc) {
        super(ownerDoc, NameImpl.createFault1_1Name(null));
    }

    @Override
    protected NameImpl getDetailName() {
        return NameImpl.createDetail1_1Name();
    }

    @Override
    protected NameImpl getFaultCodeName() {
        return NameImpl.createFromUnqualifiedName("faultcode");
    }

    @Override
    protected NameImpl getFaultStringName() {
        return NameImpl.createFromUnqualifiedName("faultstring");
    }

    @Override
    protected NameImpl getFaultActorName() {
        return NameImpl.createFromUnqualifiedName("faultactor");
    }

    @Override
    protected DetailImpl createDetail() {
        return new Detail1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument());
    }

    @Override
    protected FaultElementImpl createSOAPFaultElement(String localName) {
        return new FaultElement1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), localName);
    }

    @Override
    protected void checkIfStandardFaultCode(String faultCode, String uri) throws SOAPException {
    }

    @Override
    protected void finallySetFaultCode(String faultcode) throws SOAPException {
        this.faultCodeElement.addTextNode(faultcode);
    }

    public String getFaultCode() {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        return this.faultCodeElement.getValue();
    }

    public Name getFaultCodeAsName() {
        String faultcodeString = this.getFaultCode();
        if (faultcodeString == null) {
            return null;
        }
        int prefixIndex = faultcodeString.indexOf(58);
        if (prefixIndex == -1) {
            return NameImpl.createFromUnqualifiedName(faultcodeString);
        }
        String prefix = faultcodeString.substring(0, prefixIndex);
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        String nsName = this.faultCodeElement.getNamespaceURI(prefix);
        return NameImpl.createFromQualifiedName(faultcodeString, nsName);
    }

    public QName getFaultCodeAsQName() {
        String faultcodeString = this.getFaultCode();
        if (faultcodeString == null) {
            return null;
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        return Fault1_1Impl.convertCodeToQName(faultcodeString, (SOAPElement)this.faultCodeElement);
    }

    public void setFaultString(String faultString) throws SOAPException {
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        if (this.faultStringElement == null) {
            this.faultStringElement = this.addSOAPFaultElement("faultstring");
        } else {
            this.faultStringElement.removeContents();
            this.faultStringElement.removeAttribute("xml:lang");
        }
        this.faultStringElement.addTextNode(faultString);
    }

    public String getFaultString() {
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        return this.faultStringElement.getValue();
    }

    public Locale getFaultStringLocale() {
        String xmlLangAttr;
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        if (this.faultStringElement != null && (xmlLangAttr = this.faultStringElement.getAttributeValue((Name)NameImpl.createFromUnqualifiedName("xml:lang"))) != null) {
            return Fault1_1Impl.xmlLangToLocale(xmlLangAttr);
        }
        return null;
    }

    public void setFaultString(String faultString, Locale locale) throws SOAPException {
        this.setFaultString(faultString);
        this.faultStringElement.addAttribute(NameImpl.createFromTagName("xml:lang"), Fault1_1Impl.localeToXmlLang(locale));
    }

    @Override
    protected boolean isStandardFaultElement(String localName) {
        return localName.equalsIgnoreCase("detail") || localName.equalsIgnoreCase("faultcode") || localName.equalsIgnoreCase("faultstring") || localName.equalsIgnoreCase("faultactor");
    }

    public void appendFaultSubcode(QName subcode) {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "appendFaultSubcode");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public void removeAllFaultSubcodes() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "removeAllFaultSubcodes");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public Iterator<QName> getFaultSubcodes() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultSubcodes");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public String getFaultReasonText(Locale locale) {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonText");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public Iterator<String> getFaultReasonTexts() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonTexts");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public Iterator<Locale> getFaultReasonLocales() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonLocales");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public void addFaultReasonText(String text, Locale locale) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "addFaultReasonText");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public String getFaultRole() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultRole");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public void setFaultRole(String uri) {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "setFaultRole");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public String getFaultNode() {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultNode");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    public void setFaultNode(String uri) {
        log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "setFaultNode");
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }

    @Override
    protected QName getDefaultFaultCode() {
        return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
    }

    @Override
    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
        String localName = element.getLocalName();
        if ("Detail".equalsIgnoreCase(localName) && this.hasDetail()) {
            log.severe("SAAJ0305.ver1_2.detail.exists.error");
            throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
        }
        return super.addChildElement(element);
    }

    @Override
    protected FaultElementImpl createSOAPFaultElement(QName qname) {
        return new FaultElement1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), qname);
    }

    @Override
    protected FaultElementImpl createSOAPFaultElement(Name qname) {
        return new FaultElement1_1Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), (NameImpl)qname);
    }

    @Override
    public void setFaultCode(String faultCode, String prefix, String uri) throws SOAPException {
        if (!(prefix != null && !"".equals(prefix) || uri == null || "".equals(uri) || (prefix = this.getNamespacePrefix(uri)) != null && !"".equals(prefix))) {
            prefix = "ns0";
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        if (this.faultCodeElement == null) {
            this.faultCodeElement = this.addFaultCodeElement();
        } else {
            this.faultCodeElement.removeContents();
        }
        if ((uri == null || "".equals(uri)) && prefix != null && !"".equals("prefix")) {
            uri = this.faultCodeElement.getNamespaceURI(prefix);
        }
        if (uri == null || "".equals(uri)) {
            if (prefix != null && !"".equals(prefix)) {
                log.log(Level.SEVERE, "SAAJ0307.impl.no.ns.URI", new Object[]{prefix + ":" + faultCode});
                throw new SOAPExceptionImpl("Empty/Null NamespaceURI specified for faultCode \"" + prefix + ":" + faultCode + "\"");
            }
            uri = "";
        }
        this.checkIfStandardFaultCode(faultCode, uri);
        ((FaultElementImpl)this.faultCodeElement).ensureNamespaceIsDeclared(prefix, uri);
        if (prefix == null || "".equals(prefix)) {
            this.finallySetFaultCode(faultCode);
        } else {
            this.finallySetFaultCode(prefix + ":" + faultCode);
        }
    }

    private boolean standardFaultCode(String faultCode) {
        if (faultCode.equals("VersionMismatch") || faultCode.equals("MustUnderstand") || faultCode.equals("Client") || faultCode.equals("Server")) {
            return true;
        }
        return faultCode.startsWith("VersionMismatch.") || faultCode.startsWith("MustUnderstand.") || faultCode.startsWith("Client.") || faultCode.startsWith("Server.");
    }

    @Override
    public void setFaultActor(String faultActor) throws SOAPException {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement != null) {
            this.faultActorElement.detachNode();
        }
        if (faultActor == null) {
            return;
        }
        this.faultActorElement = this.createSOAPFaultElement(this.getFaultActorName());
        this.faultActorElement.addTextNode(faultActor);
        if (this.hasDetail()) {
            this.insertBefore((Node)this.faultActorElement, (Node)this.detail);
            return;
        }
        this.addNode((Node)this.faultActorElement);
    }
}

