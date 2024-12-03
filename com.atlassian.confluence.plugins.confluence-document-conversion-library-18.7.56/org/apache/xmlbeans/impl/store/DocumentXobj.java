/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.util.Hashtable;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NodeXobj;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

class DocumentXobj
extends NodeXobj
implements Document {
    private Hashtable<String, DomImpl.Dom> _idToElement;

    DocumentXobj(Locale l) {
        super(l, 1, 9);
    }

    @Override
    Xobj newNode(Locale l) {
        return new DocumentXobj(l);
    }

    @Override
    public Attr createAttribute(String name) {
        return DomImpl._document_createAttribute(this, name);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) {
        return DomImpl._document_createAttributeNS(this, namespaceURI, qualifiedName);
    }

    @Override
    public CDATASection createCDATASection(String data) {
        return DomImpl._document_createCDATASection(this, data);
    }

    @Override
    public Comment createComment(String data) {
        return DomImpl._document_createComment(this, data);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return DomImpl._document_createDocumentFragment(this);
    }

    @Override
    public Element createElement(String tagName) {
        return DomImpl._document_createElement(this, tagName);
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) {
        return DomImpl._document_createElementNS(this, namespaceURI, qualifiedName);
    }

    @Override
    public EntityReference createEntityReference(String name) {
        return DomImpl._document_createEntityReference(this, name);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return DomImpl._document_createProcessingInstruction(this, target, data);
    }

    @Override
    public Text createTextNode(String data) {
        return DomImpl._document_createTextNode(this, data);
    }

    @Override
    public DocumentType getDoctype() {
        return DomImpl._document_getDoctype(this);
    }

    @Override
    public Element getDocumentElement() {
        return DomImpl._document_getDocumentElement(this);
    }

    @Override
    public Element getElementById(String elementId) {
        if (this._idToElement == null) {
            return null;
        }
        Xobj o = (Xobj)((Object)this._idToElement.get(elementId));
        if (o == null) {
            return null;
        }
        if (!this.isInSameTree(o)) {
            this._idToElement.remove(elementId);
        }
        return (Element)((Object)o);
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return DomImpl._document_getElementsByTagName(this, tagname);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return DomImpl._document_getElementsByTagNameNS(this, namespaceURI, localName);
    }

    @Override
    public DOMImplementation getImplementation() {
        return DomImpl._document_getImplementation(this);
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) {
        return DomImpl._document_importNode(this, importedNode, deep);
    }

    @Override
    public Node adoptNode(Node source) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getDocumentURI() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public DOMConfiguration getDomConfig() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getInputEncoding() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public boolean getStrictErrorChecking() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getXmlEncoding() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public boolean getXmlStandalone() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getXmlVersion() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void normalizeDocument() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public Node renameNode(Node n, String namespaceURI, String qualifiedName) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setDocumentURI(String documentURI) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setXmlVersion(String xmlVersion) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    protected void addIdElement(String idVal, DomImpl.Dom e) {
        if (this._idToElement == null) {
            this._idToElement = new Hashtable();
        }
        this._idToElement.put(idVal, e);
    }

    void removeIdElement(String idVal) {
        if (this._idToElement != null) {
            this._idToElement.remove(idVal);
        }
    }
}

