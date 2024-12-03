/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPConstants
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.ver1_2;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Detail1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.FaultElement1_2Impl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Fault1_2Impl
extends FaultImpl {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.ver1_2", "com.sun.xml.messaging.saaj.soap.ver1_2.LocalStrings");
    private static final QName textName = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
    private final QName valueName = new QName("http://www.w3.org/2003/05/soap-envelope", "Value", this.getPrefix());
    private final QName subcodeName = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode", this.getPrefix());
    private SOAPElement innermostSubCodeElement = null;

    public Fault1_2Impl(SOAPDocumentImpl ownerDoc, String name, String prefix) {
        super(ownerDoc, NameImpl.createFault1_2Name(name, prefix));
    }

    public Fault1_2Impl(SOAPDocumentImpl ownerDocument, String prefix) {
        super(ownerDocument, NameImpl.createFault1_2Name(null, prefix));
    }

    public Fault1_2Impl(SOAPDocumentImpl ownerDocument) {
        super(ownerDocument, NameImpl.createFault1_2Name(null, null));
    }

    public Fault1_2Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    protected NameImpl getDetailName() {
        return NameImpl.createSOAP12Name("Detail", this.getPrefix());
    }

    @Override
    protected NameImpl getFaultCodeName() {
        return NameImpl.createSOAP12Name("Code", this.getPrefix());
    }

    @Override
    protected NameImpl getFaultStringName() {
        return this.getFaultReasonName();
    }

    @Override
    protected NameImpl getFaultActorName() {
        return this.getFaultRoleName();
    }

    private NameImpl getFaultRoleName() {
        return NameImpl.createSOAP12Name("Role", this.getPrefix());
    }

    private NameImpl getFaultReasonName() {
        return NameImpl.createSOAP12Name("Reason", this.getPrefix());
    }

    private NameImpl getFaultReasonTextName() {
        return NameImpl.createSOAP12Name("Text", this.getPrefix());
    }

    private NameImpl getFaultNodeName() {
        return NameImpl.createSOAP12Name("Node", this.getPrefix());
    }

    private static NameImpl getXmlLangName() {
        return NameImpl.createXmlName("lang");
    }

    @Override
    protected DetailImpl createDetail() {
        return new Detail1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument());
    }

    @Override
    protected FaultElementImpl createSOAPFaultElement(String localName) {
        return new FaultElement1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), localName);
    }

    @Override
    protected void checkIfStandardFaultCode(String faultCode, String uri) throws SOAPException {
        QName qname = new QName(uri, faultCode);
        if (SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT.equals(qname) || SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT.equals(qname) || SOAPConstants.SOAP_RECEIVER_FAULT.equals(qname) || SOAPConstants.SOAP_SENDER_FAULT.equals(qname) || SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.equals(qname)) {
            return;
        }
        log.log(Level.SEVERE, "SAAJ0435.ver1_2.code.not.standard", qname);
        throw new SOAPExceptionImpl(qname + " is not a standard Code value");
    }

    @Override
    protected void finallySetFaultCode(String faultcode) throws SOAPException {
        SOAPElement value = this.faultCodeElement.addChildElement(this.valueName);
        value.addTextNode(faultcode);
    }

    private void findReasonElement() {
        this.findFaultStringElement();
    }

    public Iterator<String> getFaultReasonTexts() throws SOAPException {
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        Iterator eachTextElement = this.faultStringElement.getChildElements(textName);
        ArrayList<String> texts = new ArrayList<String>();
        while (eachTextElement.hasNext()) {
            SOAPElement textElement = (SOAPElement)eachTextElement.next();
            Locale thisLocale = Fault1_2Impl.getLocale(textElement);
            if (thisLocale == null) {
                log.severe("SAAJ0431.ver1_2.xml.lang.missing");
                throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
            }
            texts.add(textElement.getValue());
        }
        if (texts.isEmpty()) {
            log.severe("SAAJ0434.ver1_2.text.element.not.present");
            throw new SOAPExceptionImpl("env:Text must be present inside env:Reason");
        }
        return texts.iterator();
    }

    public void addFaultReasonText(String text, Locale locale) throws SOAPException {
        SOAPElement reasonText;
        if (locale == null) {
            log.severe("SAAJ0430.ver1_2.locale.required");
            throw new SOAPException("locale is required and must not be null");
        }
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        if (this.faultStringElement == null) {
            this.faultStringElement = this.addSOAPFaultElement("Reason");
            reasonText = this.faultStringElement.addChildElement((Name)this.getFaultReasonTextName());
        } else {
            this.removeDefaultFaultString();
            reasonText = this.getFaultReasonTextElement(locale);
            if (reasonText != null) {
                reasonText.removeContents();
            } else {
                reasonText = this.faultStringElement.addChildElement((Name)this.getFaultReasonTextName());
            }
        }
        String xmlLang = Fault1_2Impl.localeToXmlLang(locale);
        reasonText.addAttribute((Name)Fault1_2Impl.getXmlLangName(), xmlLang);
        reasonText.addTextNode(text);
    }

    private void removeDefaultFaultString() throws SOAPException {
        String defaultFaultString;
        SOAPElement reasonText = this.getFaultReasonTextElement(Locale.getDefault());
        if (reasonText != null && (defaultFaultString = "Fault string, and possibly fault code, not set").equals(reasonText.getValue())) {
            reasonText.detachNode();
        }
    }

    public String getFaultReasonText(Locale locale) throws SOAPException {
        SOAPElement textElement;
        if (locale == null) {
            return null;
        }
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        if (this.faultStringElement != null && (textElement = this.getFaultReasonTextElement(locale)) != null) {
            textElement.normalize();
            return textElement.getFirstChild().getNodeValue();
        }
        return null;
    }

    public Iterator<Locale> getFaultReasonLocales() throws SOAPException {
        if (this.faultStringElement == null) {
            this.findReasonElement();
        }
        Iterator eachTextElement = this.faultStringElement.getChildElements(textName);
        ArrayList<Locale> localeSet = new ArrayList<Locale>();
        while (eachTextElement.hasNext()) {
            SOAPElement textElement = (SOAPElement)eachTextElement.next();
            Locale thisLocale = Fault1_2Impl.getLocale(textElement);
            if (thisLocale == null) {
                log.severe("SAAJ0431.ver1_2.xml.lang.missing");
                throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
            }
            localeSet.add(thisLocale);
        }
        if (localeSet.isEmpty()) {
            log.severe("SAAJ0434.ver1_2.text.element.not.present");
            throw new SOAPExceptionImpl("env:Text elements with mandatory xml:lang attributes must be present inside env:Reason");
        }
        return localeSet.iterator();
    }

    public Locale getFaultStringLocale() {
        Locale locale = null;
        try {
            locale = this.getFaultReasonLocales().next();
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
        return locale;
    }

    private SOAPElement getFaultReasonTextElement(Locale locale) throws SOAPException {
        Iterator eachTextElement = this.faultStringElement.getChildElements(textName);
        while (eachTextElement.hasNext()) {
            SOAPElement textElement = (SOAPElement)eachTextElement.next();
            Locale thisLocale = Fault1_2Impl.getLocale(textElement);
            if (thisLocale == null) {
                log.severe("SAAJ0431.ver1_2.xml.lang.missing");
                throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
            }
            if (!thisLocale.equals(locale)) continue;
            return textElement;
        }
        return null;
    }

    public String getFaultNode() {
        SOAPElement faultNode = this.findAndConvertChildElement(this.getFaultNodeName());
        if (faultNode == null) {
            return null;
        }
        return faultNode.getValue();
    }

    public void setFaultNode(String uri) throws SOAPException {
        SOAPElement faultNode = this.findAndConvertChildElement(this.getFaultNodeName());
        if (faultNode != null) {
            faultNode.detachNode();
        }
        faultNode = this.createSOAPFaultElement(this.getFaultNodeName());
        faultNode = faultNode.addTextNode(uri);
        if (this.getFaultRole() != null) {
            this.insertBefore((Node)faultNode, (Node)this.faultActorElement);
            return;
        }
        if (this.hasDetail()) {
            this.insertBefore((Node)faultNode, (Node)this.detail);
            return;
        }
        this.addNode((Node)faultNode);
    }

    public String getFaultRole() {
        return this.getFaultActor();
    }

    public void setFaultRole(String uri) throws SOAPException {
        if (this.faultActorElement == null) {
            this.findFaultActorElement();
        }
        if (this.faultActorElement != null) {
            this.faultActorElement.detachNode();
        }
        this.faultActorElement = this.createSOAPFaultElement(this.getFaultActorName());
        this.faultActorElement.addTextNode(uri);
        if (this.hasDetail()) {
            this.insertBefore((Node)this.faultActorElement, (Node)this.detail);
            return;
        }
        this.addNode((Node)this.faultActorElement);
    }

    public String getFaultCode() {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        Iterator codeValues = this.faultCodeElement.getChildElements(this.valueName);
        return ((SOAPElement)codeValues.next()).getValue();
    }

    public QName getFaultCodeAsQName() {
        String faultcode = this.getFaultCode();
        if (faultcode == null) {
            return null;
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        Iterator valueElements = this.faultCodeElement.getChildElements(this.valueName);
        return Fault1_2Impl.convertCodeToQName(faultcode, (SOAPElement)valueElements.next());
    }

    public Name getFaultCodeAsName() {
        String faultcode = this.getFaultCode();
        if (faultcode == null) {
            return null;
        }
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        Iterator valueElements = this.faultCodeElement.getChildElements(this.valueName);
        return NameImpl.convertToName(Fault1_2Impl.convertCodeToQName(faultcode, (SOAPElement)valueElements.next()));
    }

    public String getFaultString() {
        String reason = null;
        try {
            reason = this.getFaultReasonTexts().next();
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
        return reason;
    }

    public void setFaultString(String faultString) throws SOAPException {
        this.addFaultReasonText(faultString, Locale.getDefault());
    }

    public void setFaultString(String faultString, Locale locale) throws SOAPException {
        this.addFaultReasonText(faultString, locale);
    }

    public void appendFaultSubcode(QName subcode) throws SOAPException {
        if (subcode == null) {
            return;
        }
        if (subcode.getNamespaceURI() == null || "".equals(subcode.getNamespaceURI())) {
            log.severe("SAAJ0432.ver1_2.subcode.not.ns.qualified");
            throw new SOAPExceptionImpl("A Subcode must be namespace-qualified");
        }
        if (this.innermostSubCodeElement == null) {
            if (this.faultCodeElement == null) {
                this.findFaultCodeElement();
            }
            this.innermostSubCodeElement = this.faultCodeElement;
        }
        String prefix = null;
        prefix = subcode.getPrefix() == null || "".equals(subcode.getPrefix()) ? ((ElementImpl)this.innermostSubCodeElement).getNamespacePrefix(subcode.getNamespaceURI()) : subcode.getPrefix();
        if (prefix == null || "".equals(prefix)) {
            prefix = "ns1";
        }
        this.innermostSubCodeElement = this.innermostSubCodeElement.addChildElement(this.subcodeName);
        SOAPElement subcodeValueElement = this.innermostSubCodeElement.addChildElement(this.valueName);
        ((ElementImpl)subcodeValueElement).ensureNamespaceIsDeclared(prefix, subcode.getNamespaceURI());
        subcodeValueElement.addTextNode(prefix + ":" + subcode.getLocalPart());
    }

    public void removeAllFaultSubcodes() {
        Iterator subcodeElements;
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        if ((subcodeElements = this.faultCodeElement.getChildElements(this.subcodeName)).hasNext()) {
            SOAPElement subcode = (SOAPElement)subcodeElements.next();
            subcode.detachNode();
        }
    }

    public Iterator<QName> getFaultSubcodes() {
        if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
        }
        final ArrayList<QName> subcodeList = new ArrayList<QName>();
        Object currentCodeElement = this.faultCodeElement;
        Iterator subcodeElements = currentCodeElement.getChildElements(this.subcodeName);
        while (subcodeElements.hasNext()) {
            currentCodeElement = (ElementImpl)subcodeElements.next();
            Iterator valueElements = currentCodeElement.getChildElements(this.valueName);
            SOAPElement valueElement = (SOAPElement)valueElements.next();
            String code = valueElement.getValue();
            subcodeList.add(Fault1_2Impl.convertCodeToQName(code, valueElement));
            subcodeElements = currentCodeElement.getChildElements(this.subcodeName);
        }
        return new Iterator<QName>(){
            Iterator<QName> subCodeIter;
            {
                this.subCodeIter = subcodeList.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.subCodeIter.hasNext();
            }

            @Override
            public QName next() {
                return this.subCodeIter.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Method remove() not supported on SubCodes Iterator");
            }
        };
    }

    private static Locale getLocale(SOAPElement reasonText) {
        return Fault1_2Impl.xmlLangToLocale(reasonText.getAttributeValue((Name)Fault1_2Impl.getXmlLangName()));
    }

    @Override
    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        log.severe("SAAJ0407.ver1_2.no.encodingStyle.in.fault");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Fault");
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
    public SOAPElement addTextNode(String text) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", this.getElementQName());
        throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Fault is not legal");
    }

    @Override
    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
        String localName = element.getLocalName();
        if ("Detail".equalsIgnoreCase(localName)) {
            if (this.hasDetail()) {
                log.severe("SAAJ0436.ver1_2.detail.exists.error");
                throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
            }
            String uri = element.getElementQName().getNamespaceURI();
            if (!uri.equals("http://www.w3.org/2003/05/soap-envelope")) {
                log.severe("SAAJ0437.ver1_2.version.mismatch.error");
                throw new SOAPExceptionImpl("Cannot add Detail, Incorrect SOAP version specified for Detail element");
            }
        }
        if (element instanceof Detail1_2Impl) {
            Element importedElement = this.importElement((Element)element);
            this.addNode(importedElement);
            return this.convertToSoapElement(importedElement);
        }
        return super.addChildElement(element);
    }

    @Override
    protected boolean isStandardFaultElement(String localName) {
        return localName.equalsIgnoreCase("code") || localName.equalsIgnoreCase("reason") || localName.equalsIgnoreCase("node") || localName.equalsIgnoreCase("role") || localName.equalsIgnoreCase("detail");
    }

    @Override
    protected QName getDefaultFaultCode() {
        return SOAPConstants.SOAP_SENDER_FAULT;
    }

    @Override
    protected FaultElementImpl createSOAPFaultElement(QName qname) {
        return new FaultElement1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), qname);
    }

    @Override
    protected FaultElementImpl createSOAPFaultElement(Name qname) {
        return new FaultElement1_2Impl(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), (NameImpl)qname);
    }

    @Override
    public void setFaultActor(String faultActor) throws SOAPException {
        this.setFaultRole(faultActor);
    }
}

