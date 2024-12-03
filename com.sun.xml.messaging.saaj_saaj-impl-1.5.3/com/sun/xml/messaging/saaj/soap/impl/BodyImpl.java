/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.Node
 *  javax.xml.soap.SOAPBody
 *  javax.xml.soap.SOAPBodyElement
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPEnvelope
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.StaxBridge;
import com.sun.xml.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.util.SAAJUtil;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class BodyImpl
extends ElementImpl
implements SOAPBody {
    private SOAPFault fault;
    private StaxBridge staxBridge;
    private boolean payloadStreamRead = false;

    protected BodyImpl(SOAPDocumentImpl ownerDoc, NameImpl bodyName) {
        super(ownerDoc, bodyName);
    }

    public BodyImpl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    protected abstract NameImpl getFaultName(String var1);

    protected abstract boolean isFault(SOAPElement var1);

    protected abstract SOAPBodyElement createBodyElement(Name var1);

    protected abstract SOAPBodyElement createBodyElement(QName var1);

    protected abstract SOAPFault createFaultElement();

    protected abstract QName getDefaultFaultCode();

    public SOAPFault addFault() throws SOAPException {
        if (this.hasFault()) {
            log.severe("SAAJ0110.impl.fault.already.exists");
            throw new SOAPExceptionImpl("Error: Fault already exists");
        }
        this.fault = this.createFaultElement();
        this.addNode((org.w3c.dom.Node)this.fault);
        this.fault.setFaultCode(this.getDefaultFaultCode());
        this.fault.setFaultString("Fault string, and possibly fault code, not set");
        return this.fault;
    }

    public SOAPFault addFault(Name faultCode, String faultString, Locale locale) throws SOAPException {
        SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString, locale);
        return fault;
    }

    public SOAPFault addFault(QName faultCode, String faultString, Locale locale) throws SOAPException {
        SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString, locale);
        return fault;
    }

    public SOAPFault addFault(Name faultCode, String faultString) throws SOAPException {
        SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString);
        return fault;
    }

    public SOAPFault addFault(QName faultCode, String faultString) throws SOAPException {
        SOAPFault fault = this.addFault();
        fault.setFaultCode(faultCode);
        fault.setFaultString(faultString);
        return fault;
    }

    void initializeFault() {
        FaultImpl flt = (FaultImpl)this.findFault();
        this.fault = flt;
    }

    protected SOAPElement findFault() {
        Iterator<org.w3c.dom.Node> eachChild = this.getChildElementNodes();
        while (eachChild.hasNext()) {
            SOAPElement child = (SOAPElement)eachChild.next();
            if (!this.isFault(child)) continue;
            return child;
        }
        return null;
    }

    public boolean hasFault() {
        QName payloadQName = this.getPayloadQName();
        return this.getFaultQName().equals(payloadQName);
    }

    private Object getFaultQName() {
        return new QName(this.getNamespaceURI(), "Fault");
    }

    public SOAPFault getFault() {
        if (this.hasFault()) {
            if (this.fault == null) {
                this.fault = (SOAPFault)this.getSoapDocument().find(this.getFirstChildElement());
            }
            return this.fault;
        }
        return null;
    }

    public SOAPBodyElement addBodyElement(Name name) throws SOAPException {
        SOAPBodyElement newBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name.getLocalName(), name.getPrefix(), name.getURI());
        if (newBodyElement == null) {
            newBodyElement = this.createBodyElement(name);
        }
        this.addNode((org.w3c.dom.Node)newBodyElement);
        return newBodyElement;
    }

    public SOAPBodyElement addBodyElement(QName qname) throws SOAPException {
        SOAPBodyElement newBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), qname.getLocalPart(), qname.getPrefix(), qname.getNamespaceURI());
        if (newBodyElement == null) {
            newBodyElement = this.createBodyElement(qname);
        }
        this.addNode((org.w3c.dom.Node)newBodyElement);
        return newBodyElement;
    }

    @Override
    public void setParentElement(SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPEnvelope)) {
            log.severe("SAAJ0111.impl.body.parent.must.be.envelope");
            throw new SOAPException("Parent of SOAPBody has to be a SOAPEnvelope");
        }
        super.setParentElement(element);
    }

    @Override
    protected SOAPElement addElement(Name name) throws SOAPException {
        return this.addBodyElement(name);
    }

    @Override
    protected SOAPElement addElement(QName name) throws SOAPException {
        return this.addBodyElement(name);
    }

    public SOAPBodyElement addDocument(Document document) throws SOAPException {
        SOAPBodyElement newBodyElement = null;
        DocumentFragment docFrag = document.createDocumentFragment();
        Element rootElement = document.getDocumentElement();
        if (rootElement != null) {
            docFrag.appendChild(rootElement);
            Document ownerDoc = this.getOwnerDocument();
            org.w3c.dom.Node replacingNode = ownerDoc.importNode(docFrag, true);
            this.addNode(replacingNode);
            Iterator<Node> i = this.getChildElements(NameImpl.copyElementName(rootElement));
            while (i.hasNext()) {
                newBodyElement = (SOAPBodyElement)i.next();
            }
        }
        return newBodyElement;
    }

    @Override
    protected SOAPElement convertToSoapElement(Element element) {
        org.w3c.dom.Node soapNode = this.getSoapDocument().findIfPresent(element);
        if (soapNode instanceof SOAPBodyElement && !soapNode.getClass().equals(ElementImpl.class)) {
            return (SOAPElement)soapNode;
        }
        return this.replaceElementWithSOAPElement(element, (ElementImpl)this.createBodyElement(NameImpl.copyElementName(element)));
    }

    @Override
    public SOAPElement setElementQName(QName newName) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }

    public Document extractContentAsDocument() throws SOAPException {
        Iterator<Node> eachChild = this.getChildElements();
        Node firstBodyElement = null;
        while (eachChild.hasNext() && !(firstBodyElement instanceof SOAPElement)) {
            firstBodyElement = eachChild.next();
        }
        boolean exactlyOneChildElement = true;
        if (firstBodyElement == null) {
            exactlyOneChildElement = false;
        } else {
            for (org.w3c.dom.Node node = firstBodyElement.getNextSibling(); node != null; node = node.getNextSibling()) {
                if (!(node instanceof Element)) continue;
                exactlyOneChildElement = false;
                break;
            }
        }
        if (!exactlyOneChildElement) {
            log.log(Level.SEVERE, "SAAJ0250.impl.body.should.have.exactly.one.child");
            throw new SOAPException("Cannot extract Document from body");
        }
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", SAAJUtil.getSystemClassLoader());
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            Element rootElement = (Element)document.importNode((org.w3c.dom.Node)firstBodyElement, true);
            document.appendChild(rootElement);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "SAAJ0251.impl.cannot.extract.document.from.body");
            throw new SOAPExceptionImpl("Unable to extract Document from body", e);
        }
        firstBodyElement.detachNode();
        return document;
    }

    private void materializePayloadWrapException() {
        try {
            this.materializePayload();
        }
        catch (SOAPException e) {
            throw new RuntimeException(e);
        }
    }

    private void materializePayload() throws SOAPException {
        if (this.staxBridge != null) {
            if (this.payloadStreamRead) {
                throw new SOAPException("SOAPBody payload stream has been fully read - cannot materialize as DOM!");
            }
            try {
                this.staxBridge.bridgePayload();
                this.staxBridge = null;
                this.payloadStreamRead = true;
            }
            catch (XMLStreamException e) {
                throw new SOAPException((Throwable)e);
            }
        }
    }

    @Override
    public boolean hasChildNodes() {
        boolean hasChildren = super.hasChildNodes();
        if (!hasChildren) {
            this.materializePayloadWrapException();
        }
        return super.hasChildNodes();
    }

    @Override
    public NodeList getChildNodes() {
        this.materializePayloadWrapException();
        return super.getChildNodes();
    }

    @Override
    public org.w3c.dom.Node getFirstChild() {
        org.w3c.dom.Node child = super.getFirstChild();
        if (child == null) {
            this.materializePayloadWrapException();
        }
        return super.getFirstChild();
    }

    public org.w3c.dom.Node getFirstChildNoMaterialize() {
        return super.getFirstChild();
    }

    @Override
    public org.w3c.dom.Node getLastChild() {
        this.materializePayloadWrapException();
        return super.getLastChild();
    }

    XMLStreamReader getPayloadReader() {
        return this.staxBridge.getPayloadReader();
    }

    void setStaxBridge(StaxBridge bridge) {
        this.staxBridge = bridge;
    }

    StaxBridge getStaxBridge() {
        return this.staxBridge;
    }

    void setPayloadStreamRead() {
        this.payloadStreamRead = true;
    }

    QName getPayloadQName() {
        if (this.staxBridge != null) {
            return this.staxBridge.getPayloadQName();
        }
        Element elem = this.getFirstChildElement();
        if (elem != null) {
            String ns = elem.getNamespaceURI();
            String pref = elem.getPrefix();
            String local = elem.getLocalName();
            if (pref != null) {
                return new QName(ns, local, pref);
            }
            if (ns != null) {
                return new QName(ns, local);
            }
            return new QName(local);
        }
        return null;
    }

    String getPayloadAttributeValue(String attName) {
        if (this.staxBridge != null) {
            return this.staxBridge.getPayloadAttributeValue(attName);
        }
        Element elem = this.getFirstChildElement();
        if (elem != null) {
            return elem.getAttribute(this.getLocalName());
        }
        return null;
    }

    String getPayloadAttributeValue(QName attNAme) {
        if (this.staxBridge != null) {
            return this.staxBridge.getPayloadAttributeValue(attNAme);
        }
        Element elem = this.getFirstChildElement();
        if (elem != null) {
            return elem.getAttributeNS(attNAme.getNamespaceURI(), attNAme.getLocalPart());
        }
        return null;
    }

    public boolean isLazy() {
        return this.staxBridge != null && !this.payloadStreamRead;
    }
}

