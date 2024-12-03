/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Node
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentFragment;
import com.sun.xml.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.messaging.saaj.soap.impl.CDATAImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.NamedNodeMapImpl;
import com.sun.xml.messaging.saaj.soap.impl.NodeListImpl;
import com.sun.xml.messaging.saaj.soap.impl.SOAPCommentImpl;
import com.sun.xml.messaging.saaj.soap.impl.SOAPTextImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.util.SAAJUtil;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public class SOAPDocumentImpl
implements SOAPDocument,
Node,
Document {
    public static final String SAAJ_NODE = "javax.xml.soap.Node";
    private static final String XMLNS = "xmlns".intern();
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap", "com.sun.xml.messaging.saaj.soap.LocalStrings");
    SOAPPartImpl enclosingSOAPPart;
    private Document document;

    public SOAPDocumentImpl(SOAPPartImpl enclosingDocument) {
        this(enclosingDocument, SOAPDocumentImpl.createDocument());
    }

    SOAPDocumentImpl(SOAPPartImpl enclosingDocument, Document document) {
        this.document = document;
        this.enclosingSOAPPart = enclosingDocument;
        this.register(this);
    }

    private static Document createDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", SAAJUtil.getSystemClassLoader());
        try {
            DocumentBuilder documentBuilder = docFactory.newDocumentBuilder();
            return documentBuilder.newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException("Error creating xml document", e);
        }
    }

    @Override
    public SOAPPartImpl getSOAPPart() {
        if (this.enclosingSOAPPart == null) {
            log.severe("SAAJ0541.soap.fragment.not.bound.to.part");
            throw new RuntimeException("Could not complete operation. Fragment not bound to SOAP part.");
        }
        return this.enclosingSOAPPart;
    }

    @Override
    public SOAPDocumentImpl getDocument() {
        return this;
    }

    @Override
    public DocumentType getDoctype() {
        return null;
    }

    @Override
    public DOMImplementation getImplementation() {
        return this.document.getImplementation();
    }

    @Override
    public Element getDocumentElement() {
        this.getSOAPPart().doGetDocumentElement();
        return this.doGetDocumentElement();
    }

    protected Element doGetDocumentElement() {
        return this.document.getDocumentElement();
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(tagName), NameImpl.getPrefixFromTagName(tagName), null);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return new SOAPDocumentFragment(this);
    }

    @Override
    public Text createTextNode(String data) {
        return new SOAPTextImpl(this, data);
    }

    @Override
    public Comment createComment(String data) {
        return new SOAPCommentImpl(this, data);
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        return new CDATAImpl(this, data);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        log.severe("SAAJ0542.soap.proc.instructions.not.allowed.in.docs");
        throw new UnsupportedOperationException("Processing Instructions are not allowed in SOAP documents");
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        boolean isQualifiedName;
        boolean bl = isQualifiedName = name.indexOf(":") > 0;
        if (isQualifiedName) {
            String nsUri = null;
            String prefix = name.substring(0, name.indexOf(":"));
            if (XMLNS.equals(prefix)) {
                nsUri = ElementImpl.XMLNS_URI;
                return this.createAttributeNS(nsUri, name);
            }
        }
        return this.document.createAttribute(name);
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        log.severe("SAAJ0543.soap.entity.refs.not.allowed.in.docs");
        throw new UnsupportedOperationException("Entity References are not allowed in SOAP documents");
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return new NodeListImpl(this, this.document.getElementsByTagName(tagname));
    }

    @Override
    public org.w3c.dom.Node importNode(org.w3c.dom.Node importedNode, boolean deep) throws DOMException {
        org.w3c.dom.Node domNode = this.getDomNode(importedNode);
        org.w3c.dom.Node newNode = this.document.importNode(domNode, deep);
        if (importedNode instanceof Node) {
            org.w3c.dom.Node newSoapNode = this.createSoapNode(importedNode.getClass(), newNode);
            newNode.setUserData(SAAJ_NODE, newSoapNode, null);
            if (deep && newSoapNode.hasChildNodes()) {
                NodeList childNodes = newSoapNode.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    this.registerChildNodes(childNodes.item(i), deep);
                }
            }
            return newSoapNode;
        }
        this.registerChildNodes(newNode, deep);
        return this.findIfPresent(newNode);
    }

    public void registerChildNodes(org.w3c.dom.Node parentNode, boolean deep) {
        if (parentNode.getUserData(SAAJ_NODE) == null) {
            if (parentNode instanceof Element) {
                ElementFactory.createElement(this, (Element)parentNode);
            } else if (parentNode instanceof CharacterData) {
                switch (parentNode.getNodeType()) {
                    case 4: {
                        new CDATAImpl(this, (CharacterData)parentNode);
                        break;
                    }
                    case 8: {
                        new SOAPCommentImpl(this, (CharacterData)parentNode);
                        break;
                    }
                    case 3: {
                        new SOAPTextImpl(this, (CharacterData)parentNode);
                    }
                }
            } else if (parentNode instanceof DocumentFragment) {
                new SOAPDocumentFragment(this, (DocumentFragment)parentNode);
            }
        }
        if (deep) {
            NodeList nodeList = parentNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                org.w3c.dom.Node nextChild = nodeList.item(i);
                this.registerChildNodes(nextChild, true);
            }
        }
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(qualifiedName), NameImpl.getPrefixFromTagName(qualifiedName), namespaceURI);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.createAttributeNS(namespaceURI, qualifiedName);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return new NodeListImpl(this, this.document.getElementsByTagNameNS(namespaceURI, localName));
    }

    @Override
    public Element getElementById(String elementId) {
        return (Element)this.findIfPresent(this.document.getElementById(elementId));
    }

    @Override
    public String getInputEncoding() {
        return this.document.getInputEncoding();
    }

    @Override
    public String getXmlEncoding() {
        return this.document.getXmlEncoding();
    }

    @Override
    public boolean getXmlStandalone() {
        return this.document.getXmlStandalone();
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        this.document.setXmlStandalone(xmlStandalone);
    }

    @Override
    public String getXmlVersion() {
        return this.document.getXmlVersion();
    }

    @Override
    public void setXmlVersion(String xmlVersion) throws DOMException {
        this.document.setXmlVersion(xmlVersion);
    }

    @Override
    public boolean getStrictErrorChecking() {
        return this.document.getStrictErrorChecking();
    }

    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        this.document.setStrictErrorChecking(strictErrorChecking);
    }

    @Override
    public String getDocumentURI() {
        return this.document.getDocumentURI();
    }

    @Override
    public void setDocumentURI(String documentURI) {
        this.document.setDocumentURI(documentURI);
    }

    @Override
    public org.w3c.dom.Node adoptNode(org.w3c.dom.Node source) throws DOMException {
        return this.document.adoptNode(source);
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return this.document.getDomConfig();
    }

    @Override
    public void normalizeDocument() {
        this.document.normalizeDocument();
    }

    @Override
    public org.w3c.dom.Node renameNode(org.w3c.dom.Node n, String namespaceURI, String qualifiedName) throws DOMException {
        return this.findIfPresent(this.document.renameNode(n, namespaceURI, qualifiedName));
    }

    @Override
    public String getNodeName() {
        return this.document.getNodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return this.document.getNodeValue();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        this.document.setNodeValue(nodeValue);
    }

    @Override
    public short getNodeType() {
        return this.document.getNodeType();
    }

    @Override
    public org.w3c.dom.Node getParentNode() {
        return this.findIfPresent(this.document.getParentNode());
    }

    @Override
    public NodeList getChildNodes() {
        return new NodeListImpl(this, this.document.getChildNodes());
    }

    @Override
    public org.w3c.dom.Node getFirstChild() {
        return this.findIfPresent(this.document.getFirstChild());
    }

    @Override
    public org.w3c.dom.Node getLastChild() {
        return this.findIfPresent(this.document.getLastChild());
    }

    @Override
    public org.w3c.dom.Node getPreviousSibling() {
        return this.findIfPresent(this.document.getPreviousSibling());
    }

    @Override
    public org.w3c.dom.Node getNextSibling() {
        return this.findIfPresent(this.document.getNextSibling());
    }

    @Override
    public NamedNodeMap getAttributes() {
        NamedNodeMap attributes = this.document.getAttributes();
        if (attributes == null) {
            return null;
        }
        return new NamedNodeMapImpl(attributes, this);
    }

    @Override
    public Document getOwnerDocument() {
        return this.document.getOwnerDocument();
    }

    @Override
    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
        return this.document.insertBefore(this.getDomNode(newChild), this.getDomNode(refChild));
    }

    @Override
    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
        return this.document.replaceChild(this.getDomNode(newChild), this.getDomNode(oldChild));
    }

    @Override
    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
        return this.document.removeChild(this.getDomNode(oldChild));
    }

    @Override
    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
        return this.document.appendChild(this.getDomNode(newChild));
    }

    @Override
    public boolean hasChildNodes() {
        return this.document.hasChildNodes();
    }

    @Override
    public org.w3c.dom.Node cloneNode(boolean deep) {
        SOAPPartImpl enclosingPartClone = (SOAPPartImpl)((Object)this.enclosingSOAPPart.cloneNode(deep));
        this.registerChildNodes(enclosingPartClone.getDocument().getDomDocument(), deep);
        return enclosingPartClone.getDocument();
    }

    @Override
    public void normalize() {
        this.document.normalize();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return this.document.isSupported(feature, version);
    }

    @Override
    public String getNamespaceURI() {
        return this.document.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return this.document.getPrefix();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        this.document.setPrefix(prefix);
    }

    @Override
    public String getLocalName() {
        return this.document.getLocalName();
    }

    @Override
    public boolean hasAttributes() {
        return this.document.hasAttributes();
    }

    @Override
    public String getBaseURI() {
        return this.document.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
        return this.document.compareDocumentPosition(this.getDomNode(other));
    }

    @Override
    public String getTextContent() throws DOMException {
        return this.document.getTextContent();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        this.document.setTextContent(textContent);
    }

    @Override
    public boolean isSameNode(org.w3c.dom.Node other) {
        return this.document.isSameNode(this.getDomNode(other));
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return this.document.lookupPrefix(namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return this.document.isDefaultNamespace(namespaceURI);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return this.document.lookupNamespaceURI(prefix);
    }

    @Override
    public boolean isEqualNode(org.w3c.dom.Node arg) {
        return this.document.isEqualNode(this.getDomNode(arg));
    }

    @Override
    public Object getFeature(String feature, String version) {
        return this.document.getFeature(feature, version);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return this.document.setUserData(key, data, handler);
    }

    @Override
    public Object getUserData(String key) {
        return this.document.getUserData(key);
    }

    public Document getDomDocument() {
        return this.document;
    }

    public void register(Node node) {
        org.w3c.dom.Node domElement = this.getDomNode((org.w3c.dom.Node)node);
        if (domElement.getUserData(SAAJ_NODE) != null) {
            throw new IllegalStateException("Element " + domElement.getNodeName() + " is already registered");
        }
        domElement.setUserData(SAAJ_NODE, node, null);
    }

    public Node find(org.w3c.dom.Node node) {
        return this.find(node, true);
    }

    private Node find(org.w3c.dom.Node node, boolean required) {
        if (node == null) {
            return null;
        }
        if (node instanceof Node) {
            return (Node)node;
        }
        Node found = (Node)node.getUserData(SAAJ_NODE);
        if (found == null && required) {
            throw new IllegalArgumentException(MessageFormat.format("Cannot find SOAP wrapper for element {0}", node));
        }
        return found;
    }

    public org.w3c.dom.Node findIfPresent(org.w3c.dom.Node node) {
        Node found = this.find(node, false);
        return found != null ? found : node;
    }

    public org.w3c.dom.Node getDomNode(org.w3c.dom.Node node) {
        if (node instanceof SOAPDocumentImpl) {
            return ((SOAPDocumentImpl)node).getDomElement();
        }
        if (node instanceof ElementImpl) {
            return ((ElementImpl)((Object)node)).getDomElement();
        }
        if (node instanceof SOAPTextImpl) {
            return ((SOAPTextImpl)node).getDomElement();
        }
        if (node instanceof SOAPCommentImpl) {
            return ((SOAPCommentImpl)node).getDomElement();
        }
        if (node instanceof CDATAImpl) {
            return ((CDATAImpl)node).getDomElement();
        }
        if (node instanceof SOAPDocumentFragment) {
            return ((SOAPDocumentFragment)node).getDomNode();
        }
        return node;
    }

    private org.w3c.dom.Node createSoapNode(Class nodeType, org.w3c.dom.Node node) {
        if (SOAPTextImpl.class.isAssignableFrom(nodeType)) {
            return new SOAPTextImpl(this, (Text)node);
        }
        if (SOAPCommentImpl.class.isAssignableFrom(nodeType)) {
            return new SOAPCommentImpl(this, (Comment)node);
        }
        if (CDATAImpl.class.isAssignableFrom(nodeType)) {
            return new CDATAImpl(this, (CDATASection)node);
        }
        if (SOAPDocumentFragment.class.isAssignableFrom(nodeType)) {
            return new SOAPDocumentFragment(this, (DocumentFragment)node);
        }
        try {
            Constructor constructor = nodeType.getConstructor(SOAPDocumentImpl.class, Element.class);
            return (org.w3c.dom.Node)constructor.newInstance(this, node);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Document getDomElement() {
        return this.document;
    }

    public String getValue() {
        throw new UnsupportedOperationException();
    }

    public void setValue(String value) {
        throw new UnsupportedOperationException();
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        throw new UnsupportedOperationException();
    }

    public SOAPElement getParentElement() {
        throw new UnsupportedOperationException();
    }

    public void detachNode() {
        throw new UnsupportedOperationException();
    }

    public void recycleNode() {
        throw new UnsupportedOperationException();
    }
}

