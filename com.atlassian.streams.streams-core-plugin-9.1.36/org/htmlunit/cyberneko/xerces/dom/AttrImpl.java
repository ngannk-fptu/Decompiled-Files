/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.ChildNode;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.EntityReferenceImpl;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.htmlunit.cyberneko.xerces.dom.TextImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public class AttrImpl
extends NodeImpl
implements Attr,
TypeInfo {
    protected static final String DTD_URI = "http://www.w3.org/TR/REC-xml";
    private Object value_ = null;
    protected String name;
    protected String type;

    protected AttrImpl(CoreDocumentImpl ownerDocument, String name) {
        super(ownerDocument);
        this.name = name;
        this.isSpecified(true);
        this.hasStringValue(true);
    }

    void rename(String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.name = name;
    }

    protected void makeChildNode() {
        if (this.hasStringValue()) {
            if (this.value_ != null) {
                TextImpl text = (TextImpl)this.ownerDocument().createTextNode((String)this.value_);
                this.value_ = text;
                text.isFirstChild(true);
                text.previousSibling = text;
                text.ownerNode = this;
                text.isOwned(true);
            }
            this.hasStringValue(false);
        }
    }

    @Override
    protected void setOwnerDocument(CoreDocumentImpl doc) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        super.setOwnerDocument(doc);
        if (!this.hasStringValue()) {
            ChildNode child = (ChildNode)this.value_;
            while (child != null) {
                child.setOwnerDocument(doc);
                child = child.nextSibling;
            }
        }
    }

    @Override
    public boolean isId() {
        return this.isIdAttribute();
    }

    @Override
    public Node cloneNode(boolean deep) {
        AttrImpl clone;
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (!(clone = (AttrImpl)super.cloneNode(deep)).hasStringValue()) {
            clone.value_ = null;
            for (Node child = (Node)this.value_; child != null; child = child.getNextSibling()) {
                clone.appendChild(child.cloneNode(true));
            }
        }
        clone.isSpecified(true);
        return clone;
    }

    @Override
    public short getNodeType() {
        return 2;
    }

    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public void setNodeValue(String value) throws DOMException {
        this.setValue(value);
    }

    @Override
    public String getTypeName() {
        return this.type;
    }

    @Override
    public String getTypeNamespace() {
        if (this.type != null) {
            return DTD_URI;
        }
        return null;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return this;
    }

    @Override
    public String getNodeValue() {
        return this.getValue();
    }

    @Override
    public String getName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public void setValue(String newvalue) {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        Element ownerElement = this.getOwnerElement();
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.value_ != null) {
            String oldvalue;
            if (this.hasStringValue()) {
                oldvalue = (String)this.value_;
            } else {
                oldvalue = this.getValue();
                ChildNode firstChild = (ChildNode)this.value_;
                firstChild.previousSibling = null;
                firstChild.isFirstChild(false);
                firstChild.ownerNode = ownerDocument;
            }
            this.value_ = null;
            this.needsSyncChildren(false);
            if (this.isIdAttribute() && ownerElement != null) {
                ownerDocument.removeIdentifier(oldvalue);
            }
        }
        this.isSpecified(true);
        this.value_ = newvalue;
        this.hasStringValue(true);
        this.changed();
        if (this.isIdAttribute() && ownerElement != null) {
            ownerDocument.putIdentifier(newvalue, ownerElement);
        }
    }

    @Override
    public String getValue() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.value_ == null) {
            return "";
        }
        if (this.hasStringValue()) {
            return (String)this.value_;
        }
        ChildNode firstChild = (ChildNode)this.value_;
        String data = firstChild.getNodeType() == 5 ? ((EntityReferenceImpl)firstChild).getEntityRefValue() : firstChild.getNodeValue();
        ChildNode node = firstChild.nextSibling;
        if (node == null || data == null) {
            return data == null ? "" : data;
        }
        StringBuilder v = new StringBuilder(data);
        while (node != null) {
            if (node.getNodeType() == 5) {
                data = ((EntityReferenceImpl)node).getEntityRefValue();
                if (data == null) {
                    return "";
                }
                v.append(data);
            } else {
                v.append(node.getNodeValue());
            }
            node = node.nextSibling;
        }
        return v.toString();
    }

    @Override
    public boolean getSpecified() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.isSpecified();
    }

    @Override
    public Element getOwnerElement() {
        return (Element)((Object)(this.isOwned() ? this.ownerNode : null));
    }

    public void setSpecified(boolean arg) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isSpecified(arg);
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getName() + "=\"" + this.getValue() + "\"";
    }

    @Override
    public boolean hasChildNodes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.value_ != null;
    }

    @Override
    public NodeList getChildNodes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this;
    }

    @Override
    public Node getFirstChild() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        this.makeChildNode();
        return (Node)this.value_;
    }

    @Override
    public Node getLastChild() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.lastChild();
    }

    final ChildNode lastChild() {
        this.makeChildNode();
        return this.value_ != null ? ((ChildNode)this.value_).previousSibling : null;
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return this.internalInsertBefore(newChild, refChild, false);
    }

    Node internalInsertBefore(Node newChild, Node refChild, boolean replace) throws DOMException {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        boolean errorChecking = ownerDocument.errorChecking;
        if (newChild.getNodeType() == 11) {
            if (errorChecking) {
                for (Node kid = newChild.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
                    if (ownerDocument.isKidOK(this, kid)) continue;
                    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                    throw new DOMException(3, msg);
                }
            }
            while (newChild.hasChildNodes()) {
                this.insertBefore(newChild.getFirstChild(), refChild);
            }
            return newChild;
        }
        if (newChild == refChild) {
            refChild = refChild.getNextSibling();
            this.removeChild(newChild);
            this.insertBefore(newChild, refChild);
            return newChild;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (errorChecking) {
            if (newChild.getOwnerDocument() != ownerDocument) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, msg);
            }
            if (!ownerDocument.isKidOK(this, newChild)) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, msg);
            }
            if (refChild != null && refChild.getParentNode() != this) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, msg);
            }
            boolean treeSafe = true;
            for (NodeImpl a = this; treeSafe && a != null; a = a.parentNode()) {
                treeSafe = newChild != a;
            }
            if (!treeSafe) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, msg);
            }
        }
        this.makeChildNode();
        ownerDocument.insertingNode(this, replace);
        ChildNode newInternal = (ChildNode)newChild;
        NodeImpl oldparent = newInternal.parentNode();
        if (oldparent != null) {
            oldparent.removeChild(newInternal);
        }
        ChildNode refInternal = (ChildNode)refChild;
        newInternal.ownerNode = this;
        newInternal.isOwned(true);
        ChildNode firstChild = (ChildNode)this.value_;
        if (firstChild == null) {
            this.value_ = newInternal;
            newInternal.isFirstChild(true);
            newInternal.previousSibling = newInternal;
        } else if (refInternal == null) {
            ChildNode lastChild = firstChild.previousSibling;
            lastChild.nextSibling = newInternal;
            newInternal.previousSibling = lastChild;
            firstChild.previousSibling = newInternal;
        } else if (refChild == firstChild) {
            firstChild.isFirstChild(false);
            newInternal.nextSibling = firstChild;
            newInternal.previousSibling = firstChild.previousSibling;
            firstChild.previousSibling = newInternal;
            this.value_ = newInternal;
            newInternal.isFirstChild(true);
        } else {
            ChildNode prev = refInternal.previousSibling;
            newInternal.nextSibling = refInternal;
            prev.nextSibling = newInternal;
            refInternal.previousSibling = newInternal;
            newInternal.previousSibling = prev;
        }
        this.changed();
        ownerDocument.insertedNode(this, newInternal, replace);
        this.checkNormalizationAfterInsert(newInternal);
        return newChild;
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        if (this.hasStringValue()) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        return this.internalRemoveChild(oldChild, false);
    }

    Node internalRemoveChild(Node oldChild, boolean replace) throws DOMException {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && oldChild != null && oldChild.getParentNode() != this) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        ChildNode oldInternal = (ChildNode)oldChild;
        ownerDocument.removingNode(this, oldInternal, replace);
        if (oldInternal == this.value_) {
            oldInternal.isFirstChild(false);
            this.value_ = oldInternal.nextSibling;
            ChildNode firstChild = (ChildNode)this.value_;
            if (firstChild != null) {
                firstChild.isFirstChild(true);
                firstChild.previousSibling = oldInternal.previousSibling;
            }
        } else {
            ChildNode next;
            ChildNode prev = oldInternal.previousSibling;
            prev.nextSibling = next = oldInternal.nextSibling;
            if (next == null) {
                ChildNode firstChild = (ChildNode)this.value_;
                firstChild.previousSibling = prev;
            } else {
                next.previousSibling = prev;
            }
        }
        ChildNode oldPreviousSibling = oldInternal.previousSibling();
        oldInternal.ownerNode = ownerDocument;
        oldInternal.isOwned(false);
        oldInternal.nextSibling = null;
        oldInternal.previousSibling = null;
        this.changed();
        ownerDocument.removedNode(this, replace);
        this.checkNormalizationAfterRemove(oldPreviousSibling);
        return oldInternal;
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        this.makeChildNode();
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        ownerDocument.replacingNode(this);
        this.internalInsertBefore(newChild, oldChild, true);
        if (newChild != oldChild) {
            this.internalRemoveChild(oldChild, true);
        }
        ownerDocument.replacedNode(this);
        return oldChild;
    }

    @Override
    public int getLength() {
        if (this.hasStringValue()) {
            return 1;
        }
        ChildNode node = (ChildNode)this.value_;
        int length = 0;
        while (node != null) {
            ++length;
            node = node.nextSibling;
        }
        return length;
    }

    @Override
    public Node item(int index) {
        if (this.hasStringValue()) {
            if (index != 0 || this.value_ == null) {
                return null;
            }
            this.makeChildNode();
            return (Node)this.value_;
        }
        if (index < 0) {
            return null;
        }
        ChildNode node = (ChildNode)this.value_;
        for (int i = 0; i < index && node != null; ++i) {
            node = node.nextSibling;
        }
        return node;
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return super.isEqualNode(arg);
    }

    @Override
    public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
        return false;
    }

    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
    }

    void checkNormalizationAfterInsert(ChildNode insertedChild) {
        if (insertedChild.getNodeType() == 3) {
            ChildNode prev = insertedChild.previousSibling();
            ChildNode next = insertedChild.nextSibling;
            if (prev != null && prev.getNodeType() == 3 || next != null && next.getNodeType() == 3) {
                this.isNormalized(false);
            }
        } else if (!insertedChild.isNormalized()) {
            this.isNormalized(false);
        }
    }

    void checkNormalizationAfterRemove(ChildNode previousSibling) {
        ChildNode next;
        if (previousSibling != null && previousSibling.getNodeType() == 3 && (next = previousSibling.nextSibling) != null && next.getNodeType() == 3) {
            this.isNormalized(false);
        }
    }
}

