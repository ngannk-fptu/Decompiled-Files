/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.AttrImpl;
import org.htmlunit.cyberneko.xerces.dom.CharacterDataImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMImplementationImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;

public class DocumentImpl
extends CoreDocumentImpl
implements DocumentEvent {
    public DocumentImpl() {
    }

    public DocumentImpl(boolean grammarAccess) {
        super(grammarAccess);
    }

    public DocumentImpl(DocumentType doctype) {
        super(doctype);
    }

    public DocumentImpl(DocumentType doctype, boolean grammarAccess) {
        super(doctype, grammarAccess);
    }

    @Override
    public Node cloneNode(boolean deep) {
        DocumentImpl newdoc = new DocumentImpl();
        this.cloneNode(newdoc, deep);
        return newdoc;
    }

    @Override
    public DOMImplementation getImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }

    @Override
    void replacedText(CharacterDataImpl node) {
    }

    @Override
    void deletedText(CharacterDataImpl node, int offset, int count) {
    }

    @Override
    void insertedText(CharacterDataImpl node, int offset, int count) {
    }

    @Override
    public Event createEvent(String type) throws DOMException {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException(9, msg);
    }

    @Override
    void modifyingCharacterData(NodeImpl node, boolean replace) {
    }

    @Override
    void modifiedCharacterData(NodeImpl node, String oldvalue, String value, boolean replace) {
    }

    @Override
    void replacedCharacterData(NodeImpl node, String oldvalue, String value) {
        this.modifiedCharacterData(node, oldvalue, value, false);
    }

    @Override
    void insertingNode(NodeImpl node, boolean replace) {
    }

    @Override
    void insertedNode(NodeImpl node, NodeImpl newInternal, boolean replace) {
    }

    @Override
    void removingNode(NodeImpl node, NodeImpl oldChild, boolean replace) {
    }

    @Override
    void removedNode(NodeImpl node, boolean replace) {
    }

    @Override
    void replacingNode(NodeImpl node) {
    }

    @Override
    void replacingData(NodeImpl node) {
    }

    @Override
    void replacedNode(NodeImpl node) {
    }

    @Override
    void modifiedAttrValue(AttrImpl attr, String oldvalue) {
    }

    @Override
    void setAttrNode(AttrImpl attr, AttrImpl previous) {
    }

    @Override
    void removedAttrNode(AttrImpl attr, NodeImpl oldOwner, String name) {
    }

    @Override
    void renamedAttrNode(Attr oldAt, Attr newAt) {
    }

    @Override
    void renamedElement(Element oldEl, Element newEl) {
    }
}

