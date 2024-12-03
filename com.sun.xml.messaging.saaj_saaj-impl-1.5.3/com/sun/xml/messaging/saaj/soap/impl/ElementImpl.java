/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.Node
 *  javax.xml.soap.SOAPBodyElement
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.AttrImpl;
import com.sun.xml.messaging.saaj.soap.impl.CDATAImpl;
import com.sun.xml.messaging.saaj.soap.impl.NamedNodeMapImpl;
import com.sun.xml.messaging.saaj.soap.impl.NodeListImpl;
import com.sun.xml.messaging.saaj.soap.impl.SOAPCommentImpl;
import com.sun.xml.messaging.saaj.soap.impl.SOAPTextImpl;
import com.sun.xml.messaging.saaj.soap.impl.TextImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.util.NamespaceContextIterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class ElementImpl
implements SOAPElement,
SOAPBodyElement {
    public static final String DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
    public static final String XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
    public static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
    private transient AttributeManager encodingStyleAttribute = new AttributeManager();
    protected QName elementQName;
    private Element element;
    private SOAPDocumentImpl soapDocument;
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.impl", "com.sun.xml.messaging.saaj.soap.impl.LocalStrings");
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
    public static final String XML_URI = "http://www.w3.org/XML/1998/namespace".intern();
    private static final String XMLNS = "xmlns".intern();

    public String getTagName() {
        return this.element.getTagName();
    }

    public String getAttribute(String name) {
        return this.element.getAttribute(name);
    }

    public void setAttribute(String name, String value) throws DOMException {
        boolean isQualifiedName;
        boolean bl = isQualifiedName = name.indexOf(":") > 0;
        if (isQualifiedName) {
            String nsUri = null;
            String prefix = name.substring(0, name.indexOf(":"));
            if (XMLNS.equals(prefix)) {
                nsUri = XMLNS_URI;
                this.setAttributeNS(nsUri, name, value);
                return;
            }
        }
        this.element.setAttribute(name, value);
        Attr attr = this.element.getAttributeNode(name);
        this.register(attr);
    }

    public void removeAttribute(String name) throws DOMException {
        this.element.removeAttribute(name);
    }

    public Attr getAttributeNode(String name) {
        return this.find(this.element.getAttributeNode(name));
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        Attr attr = this.element.setAttributeNode(newAttr);
        this.register(attr);
        return attr;
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        if (oldAttr instanceof AttrImpl) {
            oldAttr = ((AttrImpl)oldAttr).delegate;
        }
        return this.element.removeAttributeNode(oldAttr);
    }

    public NodeList getElementsByTagName(String name) {
        return new NodeListImpl(this.soapDocument, this.element.getElementsByTagName(name));
    }

    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        return this.element.getAttributeNS(namespaceURI, localName);
    }

    public ElementImpl(SOAPDocumentImpl ownerDoc, Name name) {
        this.soapDocument = ownerDoc;
        this.element = ownerDoc.getDomDocument().createElementNS(name.getURI(), name.getQualifiedName());
        this.elementQName = NameImpl.convertToQName(name);
        this.soapDocument.register((Node)this);
    }

    public ElementImpl(SOAPDocumentImpl ownerDoc, QName name) {
        this.soapDocument = ownerDoc;
        this.element = ownerDoc.getDomDocument().createElementNS(name.getNamespaceURI(), ElementImpl.getQualifiedName(name));
        this.elementQName = name;
        this.soapDocument.register((Node)this);
    }

    public ElementImpl(SOAPDocumentImpl ownerDoc, Element domElement) {
        this.element = domElement;
        this.soapDocument = ownerDoc;
        this.elementQName = new QName(domElement.getNamespaceURI(), domElement.getLocalName());
        this.soapDocument.register((Node)this);
        NamedNodeMap attributes = domElement.getAttributes();
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            this.register((Attr)attributes.item(i));
        }
    }

    public ElementImpl(SOAPDocumentImpl ownerDoc, String uri, String qualifiedName) {
        this.soapDocument = ownerDoc;
        this.element = ownerDoc.getDomDocument().createElementNS(uri, qualifiedName);
        this.elementQName = new QName(uri, ElementImpl.getLocalPart(qualifiedName), ElementImpl.getPrefix(qualifiedName));
        this.soapDocument.register((Node)this);
    }

    public void ensureNamespaceIsDeclared(String prefix, String uri) {
        String alreadyDeclaredUri = this.getNamespaceURI(prefix);
        if (alreadyDeclaredUri == null || !alreadyDeclaredUri.equals(uri)) {
            try {
                this.addNamespaceDeclaration(prefix, uri);
            }
            catch (SOAPException sOAPException) {
                // empty catch block
            }
        }
    }

    public Document getOwnerDocument() {
        return this.soapDocument;
    }

    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
        return this.soapDocument.findIfPresent(this.element.insertBefore(this.soapDocument.getDomNode(newChild), this.soapDocument.getDomNode(refChild)));
    }

    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
        return this.soapDocument.findIfPresent(this.element.replaceChild(this.soapDocument.getDomNode(newChild), this.soapDocument.getDomNode(oldChild)));
    }

    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
        return this.soapDocument.findIfPresent(this.element.removeChild(this.soapDocument.getDomNode(oldChild)));
    }

    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
        return this.soapDocument.findIfPresent(this.element.appendChild(this.soapDocument.getDomNode(newChild)));
    }

    public boolean hasChildNodes() {
        return this.element.hasChildNodes();
    }

    public org.w3c.dom.Node cloneNode(boolean deep) {
        org.w3c.dom.Node elementNSNode = this.element.cloneNode(deep);
        this.soapDocument.registerChildNodes(elementNSNode, deep);
        return this.soapDocument.findIfPresent(this.soapDocument.getDomNode(elementNSNode));
    }

    public void normalize() {
        this.element.normalize();
    }

    public boolean isSupported(String feature, String version) {
        return this.element.isSupported(feature, version);
    }

    public String getNamespaceURI() {
        return this.element.getNamespaceURI();
    }

    public String getPrefix() {
        return this.element.getPrefix();
    }

    public void setPrefix(String prefix) throws DOMException {
        this.element.setPrefix(prefix);
    }

    public String getLocalName() {
        return this.element.getLocalName();
    }

    public boolean hasAttributes() {
        return this.element.hasAttributes();
    }

    public String getBaseURI() {
        return this.element.getBaseURI();
    }

    public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
        return this.element.compareDocumentPosition(this.soapDocument.getDomNode(other));
    }

    public String getTextContent() throws DOMException {
        return this.element.getTextContent();
    }

    public void setTextContent(String textContent) throws DOMException {
        this.element.setTextContent(textContent);
        final org.w3c.dom.Node firstChild = this.element.getFirstChild();
        if (firstChild instanceof Text) {
            new SOAPTextImpl(this.soapDocument, textContent){

                @Override
                protected Text createN(SOAPDocumentImpl ownerDoc, String text) {
                    return (Text)firstChild;
                }
            };
        }
    }

    public boolean isSameNode(org.w3c.dom.Node other) {
        return this.element.isSameNode(this.soapDocument.getDomNode(other));
    }

    public String lookupPrefix(String namespaceURI) {
        return this.element.lookupPrefix(namespaceURI);
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return this.element.isDefaultNamespace(namespaceURI);
    }

    public String lookupNamespaceURI(String prefix) {
        return this.element.lookupNamespaceURI(prefix);
    }

    public boolean isEqualNode(org.w3c.dom.Node arg) {
        return this.element.isEqualNode(this.soapDocument.getDomNode(arg));
    }

    public Object getFeature(String feature, String version) {
        return this.element.getFeature(feature, version);
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return this.element.setUserData(key, data, handler);
    }

    public Object getUserData(String key) {
        return this.element.getUserData(key);
    }

    public SOAPElement addChildElement(Name name) throws SOAPException {
        return this.addElement(name);
    }

    public SOAPElement addChildElement(QName qname) throws SOAPException {
        return this.addElement(qname);
    }

    public SOAPElement addChildElement(String localName) throws SOAPException {
        String nsUri = this.getNamespaceURI("");
        NameImpl name = nsUri == null || nsUri.isEmpty() ? NameImpl.createFromUnqualifiedName(localName) : NameImpl.createFromQualifiedName(localName, nsUri);
        return this.addChildElement(name);
    }

    public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
        String uri = this.getNamespaceURI(prefix);
        if (uri == null) {
            log.log(Level.SEVERE, "SAAJ0101.impl.parent.of.body.elem.mustbe.body", new String[]{prefix});
            throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + prefix);
        }
        return this.addChildElement(localName, prefix, uri);
    }

    public String getNamespaceURI(String prefix) {
        block6: {
            block5: {
                if ("xmlns".equals(prefix)) {
                    return XMLNS_URI;
                }
                if ("xml".equals(prefix)) {
                    return XML_URI;
                }
                if (!"".equals(prefix)) break block5;
                for (Object currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = currentAncestor.getParentNode()) {
                    if (!(currentAncestor instanceof ElementImpl) || !((Element)currentAncestor).hasAttributeNS(XMLNS_URI, "xmlns")) continue;
                    String uri = ((Element)currentAncestor).getAttributeNS(XMLNS_URI, "xmlns");
                    if ("".equals(uri)) {
                        return null;
                    }
                    return uri;
                }
                break block6;
            }
            if (prefix == null) break block6;
            for (Object currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = currentAncestor.getParentNode()) {
                if (!((Element)currentAncestor).hasAttributeNS(XMLNS_URI, prefix)) continue;
                return ((Element)currentAncestor).getAttributeNS(XMLNS_URI, prefix);
            }
        }
        return null;
    }

    public SOAPElement setElementQName(QName newName) throws SOAPException {
        ElementImpl copy = new ElementImpl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
        return this.replaceElementWithSOAPElement((Element)((Object)this), copy);
    }

    public QName createQName(String localName, String prefix) throws SOAPException {
        String uri = this.getNamespaceURI(prefix);
        if (uri == null) {
            log.log(Level.SEVERE, "SAAJ0102.impl.cannot.locate.ns", new Object[]{prefix});
            throw new SOAPException("Unable to locate namespace for prefix " + prefix);
        }
        return new QName(uri, localName, prefix);
    }

    public String getNamespacePrefix(String uri) {
        NamespaceContextIterator eachNamespace = this.getNamespaceContextNodes();
        while (eachNamespace.hasNext()) {
            Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
            if (!namespaceDecl.getNodeValue().equals(uri)) continue;
            String candidatePrefix = namespaceDecl.getLocalName();
            if ("xmlns".equals(candidatePrefix)) {
                return "";
            }
            return candidatePrefix;
        }
        for (Object currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = currentAncestor.getParentNode()) {
            if (!uri.equals(currentAncestor.getNamespaceURI())) continue;
            return currentAncestor.getPrefix();
        }
        return null;
    }

    protected Attr getNamespaceAttr(String prefix) {
        NamespaceContextIterator eachNamespace = this.getNamespaceContextNodes();
        if (!"".equals(prefix)) {
            prefix = ":" + prefix;
        }
        while (eachNamespace.hasNext()) {
            Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
            if (!(!"".equals(prefix) ? namespaceDecl.getNodeName().endsWith(prefix) : namespaceDecl.getNodeName().equals("xmlns"))) continue;
            return namespaceDecl;
        }
        return null;
    }

    public NamespaceContextIterator getNamespaceContextNodes() {
        return this.getNamespaceContextNodes(true);
    }

    public NamespaceContextIterator getNamespaceContextNodes(boolean traverseStack) {
        return new NamespaceContextIterator((org.w3c.dom.Node)((Object)this), traverseStack);
    }

    public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
        SOAPElement newElement = this.createElement(NameImpl.create(localName, prefix, uri));
        this.addNode((org.w3c.dom.Node)newElement);
        return this.convertToSoapElement((Element)newElement);
    }

    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
        String elementURI = element.getElementName().getURI();
        String localName = element.getLocalName();
        if ("http://schemas.xmlsoap.org/soap/envelope/".equals(elementURI) || "http://www.w3.org/2003/05/soap-envelope".equals(elementURI)) {
            if ("Envelope".equalsIgnoreCase(localName) || "Header".equalsIgnoreCase(localName) || "Body".equalsIgnoreCase(localName)) {
                log.severe("SAAJ0103.impl.cannot.add.fragements");
                throw new SOAPExceptionImpl("Cannot add fragments which contain elements which are in the SOAP namespace");
            }
            if ("Fault".equalsIgnoreCase(localName) && !"Body".equalsIgnoreCase(this.getLocalName())) {
                log.severe("SAAJ0154.impl.adding.fault.to.nonbody");
                throw new SOAPExceptionImpl("Cannot add a SOAPFault as a child of " + this.getLocalName());
            }
            if ("Detail".equalsIgnoreCase(localName) && !"Fault".equalsIgnoreCase(this.getLocalName())) {
                log.severe("SAAJ0155.impl.adding.detail.nonfault");
                throw new SOAPExceptionImpl("Cannot add a Detail as a child of " + this.getLocalName());
            }
            if ("Fault".equalsIgnoreCase(localName)) {
                if (!elementURI.equals(this.getElementName().getURI())) {
                    log.severe("SAAJ0158.impl.version.mismatch.fault");
                    throw new SOAPExceptionImpl("SOAP Version mismatch encountered when trying to add SOAPFault to SOAPBody");
                }
                Iterator<Node> it = this.getChildElements();
                if (it.hasNext()) {
                    log.severe("SAAJ0156.impl.adding.fault.error");
                    throw new SOAPExceptionImpl("Cannot add SOAPFault as a child of a non-Empty SOAPBody");
                }
            }
        }
        String encodingStyle = element.getEncodingStyle();
        Element importedElement = this.importElement((Element)element);
        this.addNode(importedElement);
        SOAPElement converted = this.convertToSoapElement(importedElement);
        if (encodingStyle != null) {
            converted.setEncodingStyle(encodingStyle);
        }
        return converted;
    }

    protected Element importElement(Element element) {
        Document document = this.getOwnerDocument();
        Document oldDocument = element.getOwnerDocument();
        if (!oldDocument.equals(document)) {
            return (Element)document.importNode(element, true);
        }
        return element;
    }

    protected SOAPElement addElement(Name name) throws SOAPException {
        SOAPElement newElement = this.createElement(name);
        this.addNode(((ElementImpl)newElement).getDomElement());
        return newElement;
    }

    protected SOAPElement addElement(QName name) throws SOAPException {
        SOAPElement newElement = this.createElement(name);
        this.addNode((org.w3c.dom.Node)newElement);
        return newElement;
    }

    protected SOAPElement createElement(Name name) {
        if (this.isNamespaceQualified(name)) {
            return (SOAPElement)this.getSoapDocument().createElementNS(name.getURI(), name.getQualifiedName());
        }
        return (SOAPElement)this.getSoapDocument().createElement(name.getQualifiedName());
    }

    protected SOAPElement createElement(QName name) {
        if (this.isNamespaceQualified(name)) {
            return (SOAPElement)this.getSoapDocument().createElementNS(name.getNamespaceURI(), ElementImpl.getQualifiedName(name));
        }
        return (SOAPElement)this.getSoapDocument().createElement(ElementImpl.getQualifiedName(name));
    }

    protected void addNode(org.w3c.dom.Node newElement) throws SOAPException {
        ElementImpl element;
        QName elementName;
        this.insertBefore(this.soapDocument.getDomNode(newElement), null);
        if (this.getOwnerDocument() instanceof DocumentFragment) {
            return;
        }
        if (newElement instanceof ElementImpl && !"".equals((elementName = (element = (ElementImpl)((Object)newElement)).getElementQName()).getNamespaceURI())) {
            element.ensureNamespaceIsDeclared(elementName.getPrefix(), elementName.getNamespaceURI());
        }
    }

    Element getFirstChildElement() {
        for (org.w3c.dom.Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof Element)) continue;
            return (Element)this.soapDocument.find(child);
        }
        return null;
    }

    protected SOAPElement findChild(NameImpl name) {
        for (org.w3c.dom.Node eachChild = this.getFirstChild(); eachChild != null; eachChild = eachChild.getNextSibling()) {
            SOAPElement eachChildSoap;
            if (!(eachChild instanceof Element) || (eachChildSoap = (SOAPElement)this.soapDocument.find(eachChild)) == null || !eachChildSoap.getElementName().equals(name)) continue;
            return eachChildSoap;
        }
        return null;
    }

    protected SOAPElement findAndConvertChildElement(NameImpl name) {
        Iterator<org.w3c.dom.Node> eachChild = this.getChildElementNodes();
        while (eachChild.hasNext()) {
            SOAPElement child = (SOAPElement)eachChild.next();
            if (!child.getElementName().equals(name)) continue;
            return child;
        }
        return null;
    }

    public SOAPElement addTextNode(String text) throws SOAPException {
        if (text.startsWith("<![CDATA[") || text.startsWith("<![cdata[")) {
            return this.addCDATA(text.substring("<![CDATA[".length(), text.length() - 3));
        }
        return this.addText(text);
    }

    protected SOAPElement addCDATA(String text) throws SOAPException {
        CDATASection cdata = this.getOwnerDocument().createCDATASection(text);
        this.addNode(cdata);
        return this;
    }

    protected SOAPElement addText(String text) throws SOAPException {
        Text textNode = this.getOwnerDocument().createTextNode(text);
        this.addNode(textNode);
        return this;
    }

    public SOAPElement addAttribute(Name name, String value) throws SOAPException {
        this.addAttributeBare(name, value);
        if (!"".equals(name.getURI())) {
            this.ensureNamespaceIsDeclared(name.getPrefix(), name.getURI());
        }
        return this;
    }

    public SOAPElement addAttribute(QName qname, String value) throws SOAPException {
        this.addAttributeBare(qname, value);
        if (!"".equals(qname.getNamespaceURI())) {
            this.ensureNamespaceIsDeclared(qname.getPrefix(), qname.getNamespaceURI());
        }
        return this;
    }

    private void addAttributeBare(Name name, String value) {
        this.addAttributeBare(name.getURI(), name.getPrefix(), name.getQualifiedName(), value);
    }

    private void addAttributeBare(QName name, String value) {
        this.addAttributeBare(name.getNamespaceURI(), name.getPrefix(), ElementImpl.getQualifiedName(name), value);
    }

    private void addAttributeBare(String uri, String prefix, String qualifiedName, String value) {
        String string = uri = uri.length() == 0 ? null : uri;
        if (qualifiedName.equals("xmlns")) {
            uri = XMLNS_URI;
        }
        if (uri == null) {
            this.setAttribute(qualifiedName, value);
        } else {
            this.setAttributeNS(uri, qualifiedName, value);
        }
    }

    public SOAPElement addNamespaceDeclaration(String prefix, String uri) throws SOAPException {
        if (prefix.length() > 0) {
            this.setAttributeNS(XMLNS_URI, "xmlns:" + prefix, uri);
        } else {
            this.setAttributeNS(XMLNS_URI, "xmlns", uri);
        }
        return this;
    }

    public String getAttributeValue(Name name) {
        return ElementImpl.getAttributeValueFrom((Element)((Object)this), name);
    }

    public String getAttributeValue(QName qname) {
        return ElementImpl.getAttributeValueFrom((Element)((Object)this), qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix(), ElementImpl.getQualifiedName(qname));
    }

    public Iterator<Name> getAllAttributes() {
        Iterator<Name> i = ElementImpl.getAllAttributesFrom((Element)((Object)this));
        ArrayList<Name> list = new ArrayList<Name>();
        while (i.hasNext()) {
            Name name = i.next();
            if ("xmlns".equalsIgnoreCase(name.getPrefix())) continue;
            list.add(name);
        }
        return list.iterator();
    }

    public Iterator<QName> getAllAttributesAsQNames() {
        Iterator<Name> i = ElementImpl.getAllAttributesFrom((Element)((Object)this));
        ArrayList<QName> list = new ArrayList<QName>();
        while (i.hasNext()) {
            Name name = i.next();
            if ("xmlns".equalsIgnoreCase(name.getPrefix())) continue;
            list.add(NameImpl.convertToQName(name));
        }
        return list.iterator();
    }

    public Iterator<String> getNamespacePrefixes() {
        return this.doGetNamespacePrefixes(false);
    }

    public Iterator<String> getVisibleNamespacePrefixes() {
        return this.doGetNamespacePrefixes(true);
    }

    protected Iterator<String> doGetNamespacePrefixes(final boolean deep) {
        return new Iterator<String>(){
            String next = null;
            String last = null;
            NamespaceContextIterator eachNamespace = ElementImpl.this.getNamespaceContextNodes(deep);

            void findNext() {
                while (this.next == null && this.eachNamespace.hasNext()) {
                    String attributeKey = this.eachNamespace.nextNamespaceAttr().getNodeName();
                    if (!attributeKey.startsWith("xmlns:")) continue;
                    this.next = attributeKey.substring("xmlns:".length());
                }
            }

            @Override
            public boolean hasNext() {
                this.findNext();
                return this.next != null;
            }

            @Override
            public String next() {
                this.findNext();
                if (this.next == null) {
                    throw new NoSuchElementException();
                }
                this.last = this.next;
                this.next = null;
                return this.last;
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                this.eachNamespace.remove();
                this.next = null;
                this.last = null;
            }
        };
    }

    public Name getElementName() {
        return NameImpl.convertToName(this.elementQName);
    }

    public QName getElementQName() {
        return this.elementQName;
    }

    public boolean removeAttribute(Name name) {
        return this.removeAttribute(name.getURI(), name.getLocalName());
    }

    public boolean removeAttribute(QName name) {
        return this.removeAttribute(name.getNamespaceURI(), name.getLocalPart());
    }

    private boolean removeAttribute(String uri, String localName) {
        String nonzeroLengthUri = uri == null || uri.length() == 0 ? null : uri;
        Attr attribute = this.getAttributeNodeNS(nonzeroLengthUri, localName);
        if (attribute == null) {
            return false;
        }
        this.removeAttributeNode(attribute);
        return true;
    }

    public boolean removeNamespaceDeclaration(String prefix) {
        Attr declaration = this.getNamespaceAttr(prefix);
        if (declaration == null) {
            return false;
        }
        try {
            this.removeAttributeNode(declaration);
        }
        catch (DOMException dOMException) {
            // empty catch block
        }
        return true;
    }

    public Iterator<Node> getChildElements() {
        return this.getChildElementsFrom((Element)((Object)this));
    }

    protected SOAPElement convertToSoapElement(Element element) {
        org.w3c.dom.Node soapNode = this.soapDocument.findIfPresent(element);
        if (soapNode instanceof SOAPElement) {
            return (SOAPElement)soapNode;
        }
        return this.replaceElementWithSOAPElement(element, (ElementImpl)this.createElement(NameImpl.copyElementName(element)));
    }

    protected TextImpl convertToSoapText(CharacterData characterData) {
        org.w3c.dom.Node soapNode = this.getSoapDocument().findIfPresent(characterData);
        if (soapNode instanceof TextImpl) {
            return (TextImpl)soapNode;
        }
        TextImpl t = null;
        switch (characterData.getNodeType()) {
            case 4: {
                t = new CDATAImpl(this.getSoapDocument(), characterData.getData());
                break;
            }
            case 8: {
                t = new SOAPCommentImpl(this.getSoapDocument(), characterData.getData());
                break;
            }
            case 3: {
                t = new SOAPTextImpl(this.getSoapDocument(), characterData.getData());
            }
        }
        Node parent = this.getSoapDocument().find(characterData.getParentNode());
        if (parent != null) {
            parent.replaceChild(t, characterData);
        }
        return t;
    }

    protected SOAPElement replaceElementWithSOAPElement(Element element, ElementImpl copy) {
        Iterator<Name> eachAttribute = ElementImpl.getAllAttributesFrom(element);
        while (eachAttribute.hasNext()) {
            Name name = eachAttribute.next();
            copy.addAttributeBare(name, ElementImpl.getAttributeValueFrom(element, name));
        }
        Iterator<org.w3c.dom.Node> eachChild = this.getChildElementsFromDOM(element);
        while (eachChild.hasNext()) {
            org.w3c.dom.Node nextChild = eachChild.next();
            copy.insertBefore(nextChild, null);
        }
        Node parent = this.soapDocument.find(element.getParentNode());
        if (parent != null) {
            parent.replaceChild((org.w3c.dom.Node)((Object)copy), element);
        }
        return copy;
    }

    private Iterator<org.w3c.dom.Node> getChildElementsFromDOM(final Element el) {
        return new Iterator<org.w3c.dom.Node>(){
            org.w3c.dom.Node next;
            org.w3c.dom.Node nextNext;
            org.w3c.dom.Node last;
            org.w3c.dom.Node soapElement;
            {
                this.next = el.getFirstChild();
                this.nextNext = null;
                this.last = null;
                this.soapElement = ElementImpl.this.getSoapDocument().findIfPresent(el);
            }

            @Override
            public boolean hasNext() {
                if (this.next != null) {
                    return true;
                }
                if (this.nextNext != null) {
                    this.next = this.nextNext;
                }
                return this.next != null;
            }

            @Override
            public org.w3c.dom.Node next() {
                if (this.hasNext()) {
                    this.last = this.next;
                    this.next = null;
                    if (this.soapElement instanceof ElementImpl && this.last instanceof Element) {
                        this.last = ((ElementImpl)((Object)this.soapElement)).convertToSoapElement((Element)this.last);
                    } else if (this.soapElement instanceof ElementImpl && this.last instanceof CharacterData) {
                        this.last = ((ElementImpl)((Object)this.soapElement)).convertToSoapText((CharacterData)this.last);
                    }
                    this.nextNext = this.last.getNextSibling();
                    return this.last;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                org.w3c.dom.Node target = this.last;
                this.last = null;
                el.removeChild(target);
            }
        };
    }

    protected Iterator<org.w3c.dom.Node> getChildElementNodes() {
        return new Iterator<org.w3c.dom.Node>(){
            Iterator<Node> eachNode;
            org.w3c.dom.Node next;
            org.w3c.dom.Node last;
            {
                this.eachNode = ElementImpl.this.getChildElements();
                this.next = null;
                this.last = null;
            }

            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    while (this.eachNode.hasNext()) {
                        org.w3c.dom.Node node = (org.w3c.dom.Node)this.eachNode.next();
                        if (!(node instanceof Element)) continue;
                        this.next = ElementImpl.this.soapDocument.findIfPresent(node);
                        break;
                    }
                }
                return this.next != null;
            }

            @Override
            public Node next() {
                if (this.hasNext()) {
                    this.last = this.next;
                    this.next = null;
                    return (Node)this.last;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                org.w3c.dom.Node target = this.last;
                this.last = null;
                ElementImpl.this.removeChild(target);
            }
        };
    }

    public Iterator<Node> getChildElements(Name name) {
        return this.getChildElements(name.getURI(), name.getLocalName());
    }

    public Iterator<Node> getChildElements(QName qname) {
        return this.getChildElements(qname.getNamespaceURI(), qname.getLocalPart());
    }

    private Iterator<Node> getChildElements(final String nameUri, final String nameLocal) {
        return new Iterator<Node>(){
            Iterator<org.w3c.dom.Node> eachElement;
            org.w3c.dom.Node next;
            org.w3c.dom.Node last;
            {
                this.eachElement = ElementImpl.this.getChildElementNodes();
                this.next = null;
                this.last = null;
            }

            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    while (this.eachElement.hasNext()) {
                        org.w3c.dom.Node element = this.eachElement.next();
                        String elementUri = element.getNamespaceURI();
                        elementUri = elementUri == null ? "" : elementUri;
                        String elementName = element.getLocalName();
                        if (!elementUri.equals(nameUri) || !elementName.equals(nameLocal)) continue;
                        this.next = element;
                        break;
                    }
                }
                return this.next != null;
            }

            @Override
            public Node next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.next;
                this.next = null;
                return (Node)this.last;
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                org.w3c.dom.Node target = this.last;
                this.last = null;
                ElementImpl.this.removeChild(target);
            }
        };
    }

    public void removeContents() {
        org.w3c.dom.Node currentChild = this.getFirstChild();
        while (currentChild != null) {
            org.w3c.dom.Node temp = currentChild.getNextSibling();
            if (currentChild instanceof Node) {
                ((Node)currentChild).detachNode();
            } else {
                org.w3c.dom.Node parent = currentChild.getParentNode();
                if (parent != null) {
                    parent.removeChild(currentChild);
                }
            }
            currentChild = temp;
        }
    }

    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        if (!"".equals(encodingStyle)) {
            try {
                new URI(encodingStyle);
            }
            catch (URISyntaxException m) {
                log.log(Level.SEVERE, "SAAJ0105.impl.encoding.style.mustbe.valid.URI", new String[]{encodingStyle});
                throw new IllegalArgumentException("Encoding style (" + encodingStyle + ") should be a valid URI");
            }
        }
        this.encodingStyleAttribute.setValue(encodingStyle);
        this.tryToFindEncodingStyleAttributeName();
    }

    public String getEncodingStyle() {
        Attr attr;
        String encodingStyle = this.encodingStyleAttribute.getValue();
        if (encodingStyle != null) {
            return encodingStyle;
        }
        String soapNamespace = this.getSOAPNamespace();
        if (soapNamespace != null && (attr = this.getAttributeNodeNS(soapNamespace, "encodingStyle")) != null) {
            encodingStyle = attr.getValue();
            try {
                this.setEncodingStyle(encodingStyle);
            }
            catch (SOAPException sOAPException) {
                // empty catch block
            }
            return encodingStyle;
        }
        return null;
    }

    public String getValue() {
        Node valueNode = this.getValueNode();
        return valueNode == null ? null : valueNode.getValue();
    }

    public void setValue(String value) {
        org.w3c.dom.Node valueNode = this.getValueNodeStrict();
        if (valueNode != null) {
            valueNode.setNodeValue(value);
        } else {
            try {
                this.addTextNode(value);
            }
            catch (SOAPException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    protected org.w3c.dom.Node getValueNodeStrict() {
        org.w3c.dom.Node node = this.getFirstChild();
        if (node != null) {
            if (node.getNextSibling() == null && node.getNodeType() == 3) {
                return node;
            }
            log.severe("SAAJ0107.impl.elem.child.not.single.text");
            throw new IllegalStateException();
        }
        return null;
    }

    protected Node getValueNode() {
        Iterator<Node> i = this.getChildElements();
        while (i.hasNext()) {
            org.w3c.dom.Node n = (org.w3c.dom.Node)i.next();
            if (n.getNodeType() != 3 && n.getNodeType() != 4) continue;
            this.normalize();
            return this.soapDocument.find(n);
        }
        return null;
    }

    public void setParentElement(SOAPElement element) throws SOAPException {
        if (element == null) {
            log.severe("SAAJ0106.impl.no.null.to.parent.elem");
            throw new SOAPException("Cannot pass NULL to setParentElement");
        }
        element.addChildElement((SOAPElement)this);
        this.findEncodingStyleAttributeName();
    }

    protected void findEncodingStyleAttributeName() throws SOAPException {
        String soapNamespacePrefix;
        String soapNamespace = this.getSOAPNamespace();
        if (soapNamespace != null && (soapNamespacePrefix = this.getNamespacePrefix(soapNamespace)) != null) {
            this.setEncodingStyleNamespace(soapNamespace, soapNamespacePrefix);
        }
    }

    protected void setEncodingStyleNamespace(String soapNamespace, String soapNamespacePrefix) throws SOAPException {
        NameImpl encodingStyleAttributeName = NameImpl.create("encodingStyle", soapNamespacePrefix, soapNamespace);
        this.encodingStyleAttribute.setName(encodingStyleAttributeName);
    }

    public SOAPElement getParentElement() {
        org.w3c.dom.Node parentNode = this.getParentNode();
        if (parentNode instanceof SOAPDocument) {
            return null;
        }
        return (SOAPElement)this.soapDocument.find(parentNode);
    }

    protected String getSOAPNamespace() {
        String soapNamespace = null;
        for (ElementImpl antecedent = this; antecedent != null; antecedent = antecedent.getParentElement()) {
            Name antecedentName = antecedent.getElementName();
            String antecedentNamespace = antecedentName.getURI();
            if (!"http://schemas.xmlsoap.org/soap/envelope/".equals(antecedentNamespace) && !"http://www.w3.org/2003/05/soap-envelope".equals(antecedentNamespace)) continue;
            soapNamespace = antecedentNamespace;
            break;
        }
        return soapNamespace;
    }

    public void detachNode() {
        org.w3c.dom.Node parent = this.getParentNode();
        if (parent != null) {
            parent.removeChild(this.element);
        }
        this.encodingStyleAttribute.clearNameAndValue();
    }

    public void tryToFindEncodingStyleAttributeName() {
        try {
            this.findEncodingStyleAttributeName();
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
    }

    public void recycleNode() {
        this.detachNode();
    }

    protected static Attr getNamespaceAttrFrom(Element element, String prefix) {
        NamespaceContextIterator eachNamespace = new NamespaceContextIterator(element);
        while (eachNamespace.hasNext()) {
            Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
            String declaredPrefix = NameImpl.getLocalNameFromTagName(namespaceDecl.getNodeName());
            if (!declaredPrefix.equals(prefix)) continue;
            return namespaceDecl;
        }
        return null;
    }

    protected static Iterator<Name> getAllAttributesFrom(Element element) {
        final NamedNodeMap attributes = element.getAttributes();
        return new Iterator<Name>(){
            int attributesLength;
            int attributeIndex;
            String currentName;
            {
                this.attributesLength = attributes.getLength();
                this.attributeIndex = 0;
            }

            @Override
            public boolean hasNext() {
                return this.attributeIndex < this.attributesLength;
            }

            @Override
            public Name next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                org.w3c.dom.Node current = attributes.item(this.attributeIndex++);
                this.currentName = current.getNodeName();
                String prefix = NameImpl.getPrefixFromTagName(this.currentName);
                if (prefix.length() == 0) {
                    return NameImpl.createFromUnqualifiedName(this.currentName);
                }
                Name attributeName = NameImpl.createFromQualifiedName(this.currentName, current.getNamespaceURI());
                return attributeName;
            }

            @Override
            public void remove() {
                if (this.currentName == null) {
                    throw new IllegalStateException();
                }
                attributes.removeNamedItem(this.currentName);
            }
        };
    }

    protected static String getAttributeValueFrom(Element element, Name name) {
        return ElementImpl.getAttributeValueFrom(element, name.getURI(), name.getLocalName(), name.getPrefix(), name.getQualifiedName());
    }

    private static String getAttributeValueFrom(Element element, String uri, String localName, String prefix, String qualifiedName) {
        boolean mustUseGetAttributeNodeNS;
        String nonzeroLengthUri = uri == null || uri.length() == 0 ? null : uri;
        boolean bl = mustUseGetAttributeNodeNS = nonzeroLengthUri != null;
        if (mustUseGetAttributeNodeNS) {
            if (!element.hasAttributeNS(uri, localName)) {
                return null;
            }
            String attrValue = element.getAttributeNS(nonzeroLengthUri, localName);
            return attrValue;
        }
        Attr attribute = null;
        attribute = element.getAttributeNode(qualifiedName);
        return attribute == null ? null : attribute.getValue();
    }

    protected Iterator<Node> getChildElementsFrom(final Element element) {
        return new Iterator<Node>(){
            org.w3c.dom.Node next;
            org.w3c.dom.Node nextNext;
            org.w3c.dom.Node last;
            org.w3c.dom.Node soapElement;
            {
                this.next = element.getFirstChild();
                this.nextNext = null;
                this.last = null;
                this.soapElement = ElementImpl.this.soapDocument.findIfPresent(element);
            }

            @Override
            public boolean hasNext() {
                if (this.next != null) {
                    return true;
                }
                if (this.nextNext != null) {
                    this.next = this.nextNext;
                }
                return this.next != null;
            }

            @Override
            public Node next() {
                if (this.hasNext()) {
                    this.last = this.next;
                    this.next = null;
                    if (this.soapElement instanceof ElementImpl && this.last instanceof Element) {
                        this.last = ((ElementImpl)((Object)this.soapElement)).convertToSoapElement((Element)this.last);
                    }
                    this.nextNext = this.last.getNextSibling();
                    return (Node)ElementImpl.this.soapDocument.findIfPresent(this.last);
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                org.w3c.dom.Node target = this.last;
                this.last = null;
                element.removeChild(target);
            }
        };
    }

    public static String getQualifiedName(QName name) {
        String prefix = name.getPrefix();
        String localName = name.getLocalPart();
        String qualifiedName = null;
        qualifiedName = prefix != null && prefix.length() > 0 ? prefix + ":" + localName : localName;
        return qualifiedName;
    }

    public static String getLocalPart(String qualifiedName) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Cannot get local name for a \"null\" qualified name");
        }
        int index = qualifiedName.indexOf(58);
        if (index < 0) {
            return qualifiedName;
        }
        return qualifiedName.substring(index + 1);
    }

    public static String getPrefix(String qualifiedName) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Cannot get prefix for a  \"null\" qualified name");
        }
        int index = qualifiedName.indexOf(58);
        if (index < 0) {
            return "";
        }
        return qualifiedName.substring(0, index);
    }

    protected boolean isNamespaceQualified(Name name) {
        return !"".equals(name.getURI());
    }

    protected boolean isNamespaceQualified(QName name) {
        return !"".equals(name.getNamespaceURI());
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
        int index;
        if (namespaceURI != null && namespaceURI.isEmpty()) {
            namespaceURI = null;
        }
        String localName = (index = qualifiedName.indexOf(58)) < 0 ? qualifiedName : qualifiedName.substring(index + 1);
        this.element.setAttributeNS(namespaceURI, qualifiedName, value);
        String tmpURI = this.getNamespaceURI();
        boolean isIDNS = false;
        if (tmpURI != null && (tmpURI.equals(DSIG_NS) || tmpURI.equals(XENC_NS))) {
            isIDNS = true;
        }
        if (localName.equals("Id")) {
            if (namespaceURI == null || namespaceURI.equals("")) {
                this.setIdAttribute(localName, true);
            } else if (isIDNS || WSU_NS.equals(namespaceURI)) {
                this.setIdAttributeNS(namespaceURI, localName, true);
            }
        }
        Attr attr = this.element.getAttributeNodeNS(namespaceURI, localName);
        this.register(attr);
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        this.element.removeAttributeNS(namespaceURI, localName);
    }

    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        return this.find(this.element.getAttributeNodeNS(namespaceURI, localName));
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        return this.element.setAttributeNodeNS(newAttr);
    }

    private void register(Attr newAttr) {
        if (newAttr != null) {
            newAttr.setUserData("javax.xml.soap.Node", new AttrImpl(this, newAttr), null);
        }
    }

    private Attr find(Attr attr) {
        Object soapAttr;
        if (attr != null && (soapAttr = attr.getUserData("javax.xml.soap.Node")) instanceof Attr) {
            return (Attr)soapAttr;
        }
        return attr;
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        return new NodeListImpl(this.soapDocument, this.element.getElementsByTagNameNS(namespaceURI, localName));
    }

    public boolean hasAttribute(String name) {
        return this.element.hasAttribute(name);
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        return this.element.hasAttributeNS(namespaceURI, localName);
    }

    public TypeInfo getSchemaTypeInfo() {
        return this.element.getSchemaTypeInfo();
    }

    public void setIdAttribute(String name, boolean isId) throws DOMException {
        this.element.setIdAttribute(name, isId);
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        this.element.setIdAttributeNS(namespaceURI, localName, isId);
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        this.element.setIdAttributeNode(idAttr, isId);
    }

    public String getNodeName() {
        return this.element.getNodeName();
    }

    public String getNodeValue() throws DOMException {
        return this.element.getNodeValue();
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        this.element.setNodeValue(nodeValue);
    }

    public short getNodeType() {
        return this.element.getNodeType();
    }

    public org.w3c.dom.Node getParentNode() {
        return this.soapDocument.find(this.element.getParentNode());
    }

    public NodeList getChildNodes() {
        return new NodeListImpl(this.soapDocument, this.element.getChildNodes());
    }

    public org.w3c.dom.Node getFirstChild() {
        return this.soapDocument.findIfPresent(this.element.getFirstChild());
    }

    public org.w3c.dom.Node getLastChild() {
        return this.soapDocument.findIfPresent(this.element.getLastChild());
    }

    public org.w3c.dom.Node getPreviousSibling() {
        return this.soapDocument.findIfPresent(this.element.getPreviousSibling());
    }

    public org.w3c.dom.Node getNextSibling() {
        return this.soapDocument.findIfPresent(this.element.getNextSibling());
    }

    public NamedNodeMap getAttributes() {
        NamedNodeMap attributes = this.element.getAttributes();
        if (attributes == null) {
            return null;
        }
        return new NamedNodeMapImpl(attributes, this.soapDocument);
    }

    public Element getDomElement() {
        return this.element;
    }

    public SOAPDocumentImpl getSoapDocument() {
        return this.soapDocument;
    }

    class AttributeManager {
        Name attributeName = null;
        String attributeValue = null;

        AttributeManager() {
        }

        public void setName(Name newName) throws SOAPException {
            this.clearAttribute();
            this.attributeName = newName;
            this.reconcileAttribute();
        }

        public void clearName() {
            this.clearAttribute();
            this.attributeName = null;
        }

        public void setValue(String value) throws SOAPException {
            this.attributeValue = value;
            this.reconcileAttribute();
        }

        public Name getName() {
            return this.attributeName;
        }

        public String getValue() {
            return this.attributeValue;
        }

        public void clearNameAndValue() {
            this.attributeName = null;
            this.attributeValue = null;
        }

        private void reconcileAttribute() throws SOAPException {
            if (this.attributeName != null) {
                ElementImpl.this.removeAttribute(this.attributeName);
                if (this.attributeValue != null) {
                    ElementImpl.this.addAttribute(this.attributeName, this.attributeValue);
                }
            }
        }

        private void clearAttribute() {
            if (this.attributeName != null) {
                ElementImpl.this.removeAttribute(this.attributeName);
            }
        }
    }
}

