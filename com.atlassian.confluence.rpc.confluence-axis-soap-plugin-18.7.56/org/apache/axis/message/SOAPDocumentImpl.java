/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.io.Serializable;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.SOAPPart;
import org.apache.axis.message.CDATAImpl;
import org.apache.axis.message.CommentImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.NodeListImpl;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.Text;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

public class SOAPDocumentImpl
implements Document,
Serializable {
    protected Document delegate = null;
    protected SOAPPart soapPart = null;
    private String[] features = new String[]{"foo", "bar"};
    private String version = "version 2.0";

    public SOAPDocumentImpl(SOAPPart sp) {
        try {
            this.delegate = XMLUtils.newDocument();
        }
        catch (ParserConfigurationException parserConfigurationException) {
            // empty catch block
        }
        this.soapPart = sp;
    }

    public DocumentType getDoctype() {
        return this.delegate.getDoctype();
    }

    public DOMImplementation getImplementation() {
        return this.delegate.getImplementation();
    }

    public Element getDocumentElement() {
        return this.soapPart.getDocumentElement();
    }

    public Element createElement(String tagName) throws DOMException {
        String localname;
        String prefix;
        int index = tagName.indexOf(":");
        if (index < 0) {
            prefix = "";
            localname = tagName;
        } else {
            prefix = tagName.substring(0, index);
            localname = tagName.substring(index + 1);
        }
        try {
            SOAPEnvelope soapenv = (SOAPEnvelope)this.soapPart.getEnvelope();
            if (soapenv != null) {
                if (tagName.equalsIgnoreCase("Envelope")) {
                    new SOAPEnvelope();
                }
                if (tagName.equalsIgnoreCase("Header")) {
                    return new SOAPHeader(soapenv, soapenv.getSOAPConstants());
                }
                if (tagName.equalsIgnoreCase("Body")) {
                    return new SOAPBody(soapenv, soapenv.getSOAPConstants());
                }
                if (tagName.equalsIgnoreCase("Fault")) {
                    return new SOAPEnvelope();
                }
                if (tagName.equalsIgnoreCase("detail")) {
                    return new SOAPFault(new AxisFault(tagName));
                }
                return new MessageElement("", prefix, localname);
            }
            return new MessageElement("", prefix, localname);
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public DocumentFragment createDocumentFragment() {
        return this.delegate.createDocumentFragment();
    }

    public org.w3c.dom.Text createTextNode(String data) {
        Text me = new Text(this.delegate.createTextNode(data));
        me.setOwnerDocument(this.soapPart);
        return me;
    }

    public Comment createComment(String data) {
        return new CommentImpl(data);
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        return new CDATAImpl(data);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        throw new UnsupportedOperationException("createProcessingInstruction");
    }

    public Attr createAttribute(String name) throws DOMException {
        return this.delegate.createAttribute(name);
    }

    public EntityReference createEntityReference(String name) throws DOMException {
        throw new UnsupportedOperationException("createEntityReference");
    }

    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        Node targetNode = null;
        short type = importedNode.getNodeType();
        switch (type) {
            case 1: {
                Element el = (Element)importedNode;
                if (deep) {
                    targetNode = new SOAPBodyElement(el);
                    break;
                }
                SOAPBodyElement target = new SOAPBodyElement();
                NamedNodeMap attrs = el.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    Node att = attrs.item(i);
                    if (att.getNamespaceURI() != null && att.getPrefix() != null && att.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/") && att.getPrefix().equals("xmlns")) {
                        Mapping map = new Mapping(att.getNodeValue(), att.getLocalName());
                        target.addMapping(map);
                    }
                    if (att.getLocalName() != null) {
                        target.addAttribute(att.getPrefix(), att.getNamespaceURI(), att.getLocalName(), att.getNodeValue());
                        continue;
                    }
                    if (att.getNodeName() == null) continue;
                    target.addAttribute(att.getPrefix(), att.getNamespaceURI(), att.getNodeName(), att.getNodeValue());
                }
                if (el.getLocalName() == null) {
                    target.setName(el.getNodeName());
                } else {
                    target.setQName(new QName(el.getNamespaceURI(), el.getLocalName()));
                }
                targetNode = target;
                break;
            }
            case 2: {
                if (importedNode.getLocalName() == null) {
                    targetNode = this.createAttribute(importedNode.getNodeName());
                    break;
                }
                targetNode = this.createAttributeNS(importedNode.getNamespaceURI(), importedNode.getLocalName());
                break;
            }
            case 3: {
                targetNode = this.createTextNode(importedNode.getNodeValue());
                break;
            }
            case 4: {
                targetNode = this.createCDATASection(importedNode.getNodeValue());
                break;
            }
            case 8: {
                targetNode = this.createComment(importedNode.getNodeValue());
                break;
            }
            case 11: {
                targetNode = this.createDocumentFragment();
                if (!deep) break;
                NodeList children = importedNode.getChildNodes();
                for (int i = 0; i < children.getLength(); ++i) {
                    targetNode.appendChild(this.importNode(children.item(i), true));
                }
                break;
            }
            case 5: {
                targetNode = this.createEntityReference(importedNode.getNodeName());
                break;
            }
            case 7: {
                ProcessingInstruction pi = (ProcessingInstruction)importedNode;
                targetNode = this.createProcessingInstruction(pi.getTarget(), pi.getData());
                break;
            }
            case 6: {
                throw new DOMException(9, "Entity nodes are not supported.");
            }
            case 12: {
                throw new DOMException(9, "Notation nodes are not supported.");
            }
            case 10: {
                throw new DOMException(9, "DocumentType nodes cannot be imported.");
            }
            case 9: {
                throw new DOMException(9, "Document nodes cannot be imported.");
            }
            default: {
                throw new DOMException(9, "Node type (" + type + ") cannot be imported.");
            }
        }
        return targetNode;
    }

    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        SOAPConstants soapConstants = null;
        if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceURI)) {
            soapConstants = SOAPConstants.SOAP11_CONSTANTS;
        } else if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceURI)) {
            soapConstants = SOAPConstants.SOAP12_CONSTANTS;
        }
        MessageElement me = null;
        if (soapConstants != null) {
            if (qualifiedName.equals("Envelope")) {
                me = new SOAPEnvelope(soapConstants);
            } else if (qualifiedName.equals("Header")) {
                me = new SOAPHeader(null, soapConstants);
            } else if (qualifiedName.equals("Body")) {
                me = new SOAPBody(null, soapConstants);
            } else if (qualifiedName.equals("Fault")) {
                me = null;
            } else if (qualifiedName.equals("detail")) {
                me = null;
            } else {
                throw new DOMException(11, "No such Localname for SOAP URI");
            }
            return null;
        }
        me = new MessageElement(namespaceURI, qualifiedName);
        if (me != null) {
            me.setOwnerDocument(this.soapPart);
        }
        return me;
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.delegate.createAttributeNS(namespaceURI, qualifiedName);
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        try {
            NodeListImpl list = new NodeListImpl();
            if (this.soapPart != null) {
                SOAPBody body;
                SOAPEnvelope soapEnv = (SOAPEnvelope)this.soapPart.getEnvelope();
                SOAPHeader header = (SOAPHeader)soapEnv.getHeader();
                if (header != null) {
                    list.addNodeList(header.getElementsByTagNameNS(namespaceURI, localName));
                }
                if ((body = (SOAPBody)soapEnv.getBody()) != null) {
                    list.addNodeList(body.getElementsByTagNameNS(namespaceURI, localName));
                }
            }
            return list;
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public NodeList getElementsByTagName(String localName) {
        try {
            NodeListImpl list = new NodeListImpl();
            if (this.soapPart != null) {
                SOAPBody body;
                SOAPEnvelope soapEnv = (SOAPEnvelope)this.soapPart.getEnvelope();
                SOAPHeader header = (SOAPHeader)soapEnv.getHeader();
                if (header != null) {
                    list.addNodeList(header.getElementsByTagName(localName));
                }
                if ((body = (SOAPBody)soapEnv.getBody()) != null) {
                    list.addNodeList(body.getElementsByTagName(localName));
                }
            }
            return list;
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public Element getElementById(String elementId) {
        return this.delegate.getElementById(elementId);
    }

    public String getNodeName() {
        return null;
    }

    public String getNodeValue() throws DOMException {
        throw new DOMException(6, "Cannot use TextNode.get in " + this);
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        throw new DOMException(6, "Cannot use TextNode.set in " + this);
    }

    public short getNodeType() {
        return 9;
    }

    public Node getParentNode() {
        return null;
    }

    public NodeList getChildNodes() {
        try {
            if (this.soapPart != null) {
                NodeListImpl children = new NodeListImpl();
                children.addNode(this.soapPart.getEnvelope());
                return children;
            }
            return NodeListImpl.EMPTY_NODELIST;
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public Node getFirstChild() {
        try {
            if (this.soapPart != null) {
                return (SOAPEnvelope)this.soapPart.getEnvelope();
            }
            return null;
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public Node getLastChild() {
        try {
            if (this.soapPart != null) {
                return (SOAPEnvelope)this.soapPart.getEnvelope();
            }
            return null;
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public Node getPreviousSibling() {
        return null;
    }

    public Node getNextSibling() {
        return null;
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    public Document getOwnerDocument() {
        return null;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new DOMException(9, "");
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new DOMException(9, "");
    }

    public Node removeChild(Node oldChild) throws DOMException {
        try {
            javax.xml.soap.SOAPEnvelope envNode;
            if (this.soapPart != null && (envNode = this.soapPart.getEnvelope()).equals(oldChild)) {
                return envNode;
            }
            throw new DOMException(9, "");
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public Node appendChild(Node newChild) throws DOMException {
        throw new DOMException(9, "");
    }

    public boolean hasChildNodes() {
        try {
            return this.soapPart != null && this.soapPart.getEnvelope() != null;
        }
        catch (SOAPException se) {
            throw new DOMException(11, "");
        }
    }

    public Node cloneNode(boolean deep) {
        throw new DOMException(9, "");
    }

    public void normalize() {
        throw new DOMException(9, "");
    }

    public boolean isSupported(String feature, String version) {
        return version.equalsIgnoreCase(version);
    }

    public String getPrefix() {
        throw new DOMException(9, "");
    }

    public void setPrefix(String prefix) {
        throw new DOMException(9, "");
    }

    public String getNamespaceURI() {
        throw new DOMException(9, "");
    }

    public void setNamespaceURI(String nsURI) {
        throw new DOMException(9, "");
    }

    public String getLocalName() {
        throw new DOMException(9, "");
    }

    public boolean hasAttributes() {
        throw new DOMException(9, "");
    }
}

