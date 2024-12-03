/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Detail
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 *  javax.xml.soap.SOAPFaultElement
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFaultElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class FaultImpl
extends ElementImpl
implements SOAPFault {
    protected SOAPFaultElement faultStringElement;
    protected SOAPFaultElement faultActorElement;
    protected SOAPFaultElement faultCodeElement;
    protected Detail detail;

    protected FaultImpl(SOAPDocumentImpl ownerDoc, NameImpl name) {
        super(ownerDoc, name);
    }

    public FaultImpl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    protected abstract NameImpl getDetailName();

    protected abstract NameImpl getFaultCodeName();

    protected abstract NameImpl getFaultStringName();

    protected abstract NameImpl getFaultActorName();

    protected abstract DetailImpl createDetail();

    protected abstract FaultElementImpl createSOAPFaultElement(String var1);

    protected abstract FaultElementImpl createSOAPFaultElement(QName var1);

    protected abstract FaultElementImpl createSOAPFaultElement(Name var1);

    protected abstract void checkIfStandardFaultCode(String var1, String var2) throws SOAPException;

    protected abstract void finallySetFaultCode(String var1) throws SOAPException;

    protected abstract boolean isStandardFaultElement(String var1);

    protected abstract QName getDefaultFaultCode();

    protected void findFaultCodeElement() {
        this.faultCodeElement = (SOAPFaultElement)this.findAndConvertChildElement(this.getFaultCodeName());
    }

    protected void findFaultActorElement() {
        this.faultActorElement = (SOAPFaultElement)this.findAndConvertChildElement(this.getFaultActorName());
    }

    protected void findFaultStringElement() {
        this.faultStringElement = (SOAPFaultElement)this.findAndConvertChildElement(this.getFaultStringName());
    }

    public void setFaultCode(String faultCode) throws SOAPException {
        this.setFaultCode(NameImpl.getLocalNameFromTagName(faultCode), NameImpl.getPrefixFromTagName(faultCode), null);
    }

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
        if (uri == null || "".equals(uri)) {
            uri = this.faultCodeElement.getNamespaceURI(prefix);
        }
        if (uri == null || "".equals(uri)) {
            if (prefix != null && !"".equals(prefix)) {
                log.log(Level.SEVERE, "SAAJ0140.impl.no.ns.URI", new Object[]{prefix + ":" + faultCode});
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

    public void setFaultCode(Name faultCodeQName) throws SOAPException {
        this.setFaultCode(faultCodeQName.getLocalName(), faultCodeQName.getPrefix(), faultCodeQName.getURI());
    }

    public void setFaultCode(QName faultCodeQName) throws SOAPException {
        this.setFaultCode(faultCodeQName.getLocalPart(), faultCodeQName.getPrefix(), faultCodeQName.getNamespaceURI());
    }

    protected static QName convertCodeToQName(String code, SOAPElement codeContainingElement) {
        int prefixIndex = code.indexOf(58);
        if (prefixIndex == -1) {
            return new QName(code);
        }
        String prefix = code.substring(0, prefixIndex);
        String nsName = ((ElementImpl)codeContainingElement).lookupNamespaceURI(prefix);
        return new QName(nsName, FaultImpl.getLocalPart(code), prefix);
    }

    protected void initializeDetail() {
        NameImpl detailName = this.getDetailName();
        this.detail = (Detail)this.findAndConvertChildElement(detailName);
    }

    public Detail getDetail() {
        if (this.detail == null) {
            this.initializeDetail();
        }
        if (this.detail != null && this.detail.getParentNode() == null) {
            this.detail = null;
        }
        return this.detail;
    }

    public Detail addDetail() throws SOAPException {
        if (this.detail == null) {
            this.initializeDetail();
        }
        if (this.detail == null) {
            this.detail = this.createDetail();
            this.addNode((Node)this.detail);
            return this.detail;
        }
        throw new SOAPExceptionImpl("Error: Detail already exists");
    }

    public boolean hasDetail() {
        return this.getDetail() != null;
    }

    public abstract void setFaultActor(String var1) throws SOAPException;

    public String getFaultActor() {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement != null) {
            return this.faultActorElement.getValue();
        }
        return null;
    }

    @Override
    public SOAPElement setElementQName(QName newName) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }

    @Override
    protected SOAPElement convertToSoapElement(Element element) {
        String localName;
        Node soapNode = this.getSoapDocument().findIfPresent(element);
        if (soapNode instanceof SOAPFaultElement) {
            return (SOAPElement)soapNode;
        }
        if (soapNode instanceof SOAPElement) {
            SOAPElement soapElement = (SOAPElement)soapNode;
            if (this.getDetailName().equals(soapElement.getElementName())) {
                return this.replaceElementWithSOAPElement(element, this.createDetail());
            }
            String localName2 = soapElement.getElementName().getLocalName();
            if (this.isStandardFaultElement(localName2)) {
                return this.replaceElementWithSOAPElement(element, this.createSOAPFaultElement(soapElement.getElementQName()));
            }
            return soapElement;
        }
        Name elementName = NameImpl.copyElementName(element);
        ElementImpl newElement = this.getDetailName().equals(elementName) ? this.createDetail() : (this.isStandardFaultElement(localName = elementName.getLocalName()) ? this.createSOAPFaultElement(elementName) : (ElementImpl)this.createElement(elementName));
        return this.replaceElementWithSOAPElement(element, newElement);
    }

    protected SOAPFaultElement addFaultCodeElement() throws SOAPException {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        if (this.faultCodeElement == null) {
            this.faultCodeElement = this.addSOAPFaultElement(this.getFaultCodeName().getLocalName());
            return this.faultCodeElement;
        }
        throw new SOAPExceptionImpl("Error: Faultcode already exists");
    }

    private SOAPFaultElement addFaultStringElement() throws SOAPException {
        if (this.faultStringElement == null) {
            this.findFaultStringElement();
        }
        if (this.faultStringElement == null) {
            this.faultStringElement = this.addSOAPFaultElement(this.getFaultStringName().getLocalName());
            return this.faultStringElement;
        }
        throw new SOAPExceptionImpl("Error: Faultstring already exists");
    }

    private SOAPFaultElement addFaultActorElement() throws SOAPException {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement == null) {
            this.faultActorElement = this.addSOAPFaultElement(this.getFaultActorName().getLocalName());
            return this.faultActorElement;
        }
        throw new SOAPExceptionImpl("Error: Faultactor already exists");
    }

    @Override
    protected SOAPElement addElement(Name name) throws SOAPException {
        if (this.getDetailName().equals(name)) {
            return this.addDetail();
        }
        if (this.getFaultCodeName().equals(name)) {
            return this.addFaultCodeElement();
        }
        if (this.getFaultStringName().equals(name)) {
            return this.addFaultStringElement();
        }
        if (this.getFaultActorName().equals(name)) {
            return this.addFaultActorElement();
        }
        return super.addElement(name);
    }

    @Override
    protected SOAPElement addElement(QName name) throws SOAPException {
        return this.addElement(NameImpl.convertToName(name));
    }

    protected FaultElementImpl addSOAPFaultElement(String localName) throws SOAPException {
        FaultElementImpl faultElem = this.createSOAPFaultElement(localName);
        this.addNode((Node)((Object)faultElem));
        return faultElem;
    }

    protected static Locale xmlLangToLocale(String xmlLang) {
        if (xmlLang == null) {
            return null;
        }
        int index = xmlLang.indexOf("-");
        if (index == -1) {
            index = xmlLang.indexOf("_");
        }
        if (index == -1) {
            return new Locale(xmlLang, "");
        }
        String language = xmlLang.substring(0, index);
        String country = xmlLang.substring(index + 1);
        return new Locale(language, country);
    }

    protected static String localeToXmlLang(Locale locale) {
        String xmlLang = locale.getLanguage();
        String country = locale.getCountry();
        if (!"".equals(country)) {
            xmlLang = xmlLang + "-" + country;
        }
        return xmlLang;
    }
}

