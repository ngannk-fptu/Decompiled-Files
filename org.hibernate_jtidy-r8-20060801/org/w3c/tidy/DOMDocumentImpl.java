/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.DOMNodeListByTagNameImpl;
import org.w3c.tidy.Node;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyUtils;

public class DOMDocumentImpl
extends DOMNodeImpl
implements Document {
    private TagTable tt = new TagTable();

    protected DOMDocumentImpl(Node adaptee) {
        super(adaptee);
    }

    public String getNodeName() {
        return "#document";
    }

    public short getNodeType() {
        return 9;
    }

    public DocumentType getDoctype() {
        Node node = this.adaptee.content;
        while (node != null && node.type != 1) {
            node = node.next;
        }
        if (node != null) {
            return (DocumentType)node.getAdapter();
        }
        return null;
    }

    public DOMImplementation getImplementation() {
        throw new DOMException(9, "DOM method not supported");
    }

    public Element getDocumentElement() {
        Node node = this.adaptee.content;
        while (node != null && node.type != 5 && node.type != 7) {
            node = node.next;
        }
        if (node != null) {
            return (Element)node.getAdapter();
        }
        return null;
    }

    public Element createElement(String tagName) throws DOMException {
        Node node = new Node(7, null, 0, 0, tagName, this.tt);
        if (node != null) {
            if (node.tag == null) {
                node.tag = TagTable.XML_TAGS;
            }
            return (Element)node.getAdapter();
        }
        return null;
    }

    public DocumentFragment createDocumentFragment() {
        throw new DOMException(9, "DOM method not supported");
    }

    public Text createTextNode(String data) {
        byte[] textarray = TidyUtils.getBytes(data);
        Node node = new Node(4, textarray, 0, textarray.length);
        if (node != null) {
            return (Text)node.getAdapter();
        }
        return null;
    }

    public Comment createComment(String data) {
        byte[] textarray = TidyUtils.getBytes(data);
        Node node = new Node(2, textarray, 0, textarray.length);
        if (node != null) {
            return (Comment)node.getAdapter();
        }
        return null;
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        throw new DOMException(9, "HTML document");
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        throw new DOMException(9, "HTML document");
    }

    public Attr createAttribute(String name) throws DOMException {
        AttVal av = new AttVal(null, null, 34, name, null);
        if (av != null) {
            av.dict = AttributeTable.getDefaultAttributeTable().findAttribute(av);
            return av.getAdapter();
        }
        return null;
    }

    public EntityReference createEntityReference(String name) throws DOMException {
        throw new DOMException(9, "createEntityReference not supported");
    }

    public NodeList getElementsByTagName(String tagname) {
        return new DOMNodeListByTagNameImpl(this.adaptee, tagname);
    }

    public org.w3c.dom.Node importNode(org.w3c.dom.Node importedNode, boolean deep) throws DOMException {
        throw new DOMException(9, "importNode not supported");
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new DOMException(9, "createAttributeNS not supported");
    }

    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new DOMException(9, "createElementNS not supported");
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        throw new DOMException(9, "getElementsByTagNameNS not supported");
    }

    public Element getElementById(String elementId) {
        return null;
    }

    public org.w3c.dom.Node adoptNode(org.w3c.dom.Node source) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public String getDocumentURI() {
        return null;
    }

    public DOMConfiguration getDomConfig() {
        return null;
    }

    public String getInputEncoding() {
        return null;
    }

    public boolean getStrictErrorChecking() {
        return true;
    }

    public String getXmlEncoding() {
        return null;
    }

    public boolean getXmlStandalone() {
        return false;
    }

    public String getXmlVersion() {
        return "1.0";
    }

    public void normalizeDocument() {
    }

    public org.w3c.dom.Node renameNode(org.w3c.dom.Node n, String namespaceURI, String qualifiedName) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public void setDocumentURI(String documentURI) {
    }

    public void setStrictErrorChecking(boolean strictErrorChecking) {
    }

    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
    }

    public void setXmlVersion(String xmlVersion) throws DOMException {
    }
}

