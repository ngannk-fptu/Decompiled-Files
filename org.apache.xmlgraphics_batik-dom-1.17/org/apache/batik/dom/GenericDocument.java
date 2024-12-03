/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.GenericAttr;
import org.apache.batik.dom.GenericAttrNS;
import org.apache.batik.dom.GenericCDATASection;
import org.apache.batik.dom.GenericComment;
import org.apache.batik.dom.GenericDocumentFragment;
import org.apache.batik.dom.GenericElement;
import org.apache.batik.dom.GenericElementNS;
import org.apache.batik.dom.GenericEntityReference;
import org.apache.batik.dom.GenericProcessingInstruction;
import org.apache.batik.dom.GenericText;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class GenericDocument
extends AbstractDocument {
    protected static final String ATTR_ID = "id";
    protected boolean readonly;

    protected GenericDocument() {
    }

    public GenericDocument(DocumentType dt, DOMImplementation impl) {
        super(dt, impl);
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public void setReadonly(boolean v) {
        this.readonly = v;
    }

    @Override
    public boolean isId(Attr node) {
        if (node.getNamespaceURI() != null) {
            return false;
        }
        return ATTR_ID.equals(node.getNodeName());
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        return new GenericElement(tagName.intern(), this);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return new GenericDocumentFragment(this);
    }

    @Override
    public Text createTextNode(String data) {
        return new GenericText(data, this);
    }

    @Override
    public Comment createComment(String data) {
        return new GenericComment(data, this);
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        return new GenericCDATASection(data, this);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        return new GenericProcessingInstruction(target, data, this);
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        return new GenericAttr(name.intern(), this);
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        return new GenericEntityReference(name, this);
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (namespaceURI == null) {
            return new GenericElement(qualifiedName.intern(), this);
        }
        return new GenericElementNS(namespaceURI.intern(), qualifiedName.intern(), this);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (namespaceURI == null) {
            return new GenericAttr(qualifiedName.intern(), this);
        }
        return new GenericAttrNS(namespaceURI.intern(), qualifiedName.intern(), this);
    }

    @Override
    protected Node newNode() {
        return new GenericDocument();
    }
}

