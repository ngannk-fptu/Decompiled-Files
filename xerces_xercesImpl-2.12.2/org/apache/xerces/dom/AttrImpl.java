/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.EntityReferenceImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.TextImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class AttrImpl
extends NodeImpl
implements Attr,
TypeInfo {
    static final long serialVersionUID = 7277707688218972102L;
    static final String DTD_URI = "http://www.w3.org/TR/REC-xml";
    protected Object value = null;
    protected String name;
    transient Object type;

    protected AttrImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.name = string;
        this.isSpecified(true);
        this.hasStringValue(true);
    }

    protected AttrImpl() {
    }

    void rename(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.name = string;
    }

    protected void makeChildNode() {
        if (this.hasStringValue()) {
            if (this.value != null) {
                TextImpl textImpl = (TextImpl)this.ownerDocument().createTextNode((String)this.value);
                this.value = textImpl;
                textImpl.isFirstChild(true);
                textImpl.previousSibling = textImpl;
                textImpl.ownerNode = this;
                textImpl.isOwned(true);
            }
            this.hasStringValue(false);
        }
    }

    @Override
    protected void setOwnerDocument(CoreDocumentImpl coreDocumentImpl) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        super.setOwnerDocument(coreDocumentImpl);
        if (!this.hasStringValue()) {
            ChildNode childNode = (ChildNode)this.value;
            while (childNode != null) {
                childNode.setOwnerDocument(coreDocumentImpl);
                childNode = childNode.nextSibling;
            }
        }
    }

    public void setIdAttribute(boolean bl) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isIdAttribute(bl);
    }

    @Override
    public boolean isId() {
        return this.isIdAttribute();
    }

    @Override
    public Node cloneNode(boolean bl) {
        AttrImpl attrImpl;
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (!(attrImpl = (AttrImpl)super.cloneNode(bl)).hasStringValue()) {
            attrImpl.value = null;
            for (Node node = (Node)this.value; node != null; node = node.getNextSibling()) {
                attrImpl.appendChild(node.cloneNode(true));
            }
        }
        attrImpl.isSpecified(true);
        return attrImpl;
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
    public void setNodeValue(String string) throws DOMException {
        this.setValue(string);
    }

    @Override
    public String getTypeName() {
        return (String)this.type;
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
    public void setValue(String string) {
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        if (coreDocumentImpl.errorChecking && this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        Element element = this.getOwnerElement();
        String string3 = "";
        TextImpl textImpl = null;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.value != null) {
            if (coreDocumentImpl.getMutationEvents()) {
                if (this.hasStringValue()) {
                    string3 = (String)this.value;
                    textImpl = (TextImpl)coreDocumentImpl.createTextNode((String)this.value);
                    this.value = textImpl;
                    textImpl.isFirstChild(true);
                    textImpl.previousSibling = textImpl;
                    textImpl.ownerNode = this;
                    textImpl.isOwned(true);
                    this.hasStringValue(false);
                    this.internalRemoveChild(textImpl, true);
                } else {
                    string3 = this.getValue();
                    while (this.value != null) {
                        this.internalRemoveChild((Node)this.value, true);
                    }
                }
            } else {
                if (this.hasStringValue()) {
                    string3 = (String)this.value;
                } else {
                    string3 = this.getValue();
                    ChildNode childNode = (ChildNode)this.value;
                    childNode.previousSibling = null;
                    childNode.isFirstChild(false);
                    childNode.ownerNode = coreDocumentImpl;
                }
                this.value = null;
                this.needsSyncChildren(false);
            }
            if (this.isIdAttribute() && element != null) {
                coreDocumentImpl.removeIdentifier(string3);
            }
        }
        this.isSpecified(true);
        if (coreDocumentImpl.getMutationEvents()) {
            if (textImpl == null) {
                textImpl = (TextImpl)coreDocumentImpl.createTextNode(string);
            } else {
                textImpl.data = string;
            }
            this.internalInsertBefore(textImpl, null, true);
            this.hasStringValue(false);
            coreDocumentImpl.modifiedAttrValue(this, string3);
        } else {
            this.value = string;
            this.hasStringValue(true);
            this.changed();
        }
        if (this.isIdAttribute() && element != null) {
            coreDocumentImpl.putIdentifier(string, element);
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
        if (this.value == null) {
            return "";
        }
        if (this.hasStringValue()) {
            return (String)this.value;
        }
        ChildNode childNode = (ChildNode)this.value;
        String string = null;
        string = childNode.getNodeType() == 5 ? ((EntityReferenceImpl)childNode).getEntityRefValue() : childNode.getNodeValue();
        ChildNode childNode2 = childNode.nextSibling;
        if (childNode2 == null || string == null) {
            return string == null ? "" : string;
        }
        StringBuffer stringBuffer = new StringBuffer(string);
        while (childNode2 != null) {
            if (childNode2.getNodeType() == 5) {
                string = ((EntityReferenceImpl)childNode2).getEntityRefValue();
                if (string == null) {
                    return "";
                }
                stringBuffer.append(string);
            } else {
                stringBuffer.append(childNode2.getNodeValue());
            }
            childNode2 = childNode2.nextSibling;
        }
        return stringBuffer.toString();
    }

    @Override
    public boolean getSpecified() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.isSpecified();
    }

    public Element getElement() {
        return (Element)((Object)(this.isOwned() ? this.ownerNode : null));
    }

    @Override
    public Element getOwnerElement() {
        return (Element)((Object)(this.isOwned() ? this.ownerNode : null));
    }

    @Override
    public void normalize() {
        if (this.isNormalized() || this.hasStringValue()) {
            return;
        }
        ChildNode childNode = (ChildNode)this.value;
        Node node = childNode;
        while (node != null) {
            Node node2 = node.getNextSibling();
            if (node.getNodeType() == 3) {
                if (node2 != null && node2.getNodeType() == 3) {
                    ((Text)node).appendData(node2.getNodeValue());
                    this.removeChild(node2);
                    node2 = node;
                } else if (node.getNodeValue() == null || node.getNodeValue().length() == 0) {
                    this.removeChild(node);
                }
            }
            node = node2;
        }
        this.isNormalized(true);
    }

    public void setSpecified(boolean bl) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isSpecified(bl);
    }

    public void setType(Object object) {
        this.type = object;
    }

    @Override
    public String toString() {
        return this.getName() + "=" + "\"" + this.getValue() + "\"";
    }

    @Override
    public boolean hasChildNodes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.value != null;
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
        return (Node)this.value;
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
        return this.value != null ? ((ChildNode)this.value).previousSibling : null;
    }

    final void lastChild(ChildNode childNode) {
        if (this.value != null) {
            ((ChildNode)this.value).previousSibling = childNode;
        }
    }

    @Override
    public Node insertBefore(Node node, Node node2) throws DOMException {
        return this.internalInsertBefore(node, node2, false);
    }

    Node internalInsertBefore(Node node, Node node2, boolean bl) throws DOMException {
        Object object;
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        boolean bl2 = coreDocumentImpl.errorChecking;
        if (node.getNodeType() == 11) {
            if (bl2) {
                for (Node node3 = node.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                    if (coreDocumentImpl.isKidOK(this, node3)) continue;
                    String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                    throw new DOMException(3, string);
                }
            }
            while (node.hasChildNodes()) {
                this.insertBefore(node.getFirstChild(), node2);
            }
            return node;
        }
        if (node == node2) {
            node2 = node2.getNextSibling();
            this.removeChild(node);
            this.insertBefore(node, node2);
            return node;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (bl2) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (node.getOwnerDocument() != coreDocumentImpl) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
            if (!coreDocumentImpl.isKidOK(this, node)) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, string);
            }
            if (node2 != null && node2.getParentNode() != this) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, string);
            }
            boolean bl3 = true;
            for (object = this; bl3 && object != null; object = ((NodeImpl)object).parentNode()) {
                bl3 = node != object;
            }
            if (!bl3) {
                object = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, (String)object);
            }
        }
        this.makeChildNode();
        coreDocumentImpl.insertingNode(this, bl);
        ChildNode childNode = (ChildNode)node;
        object = childNode.parentNode();
        if (object != null) {
            object.removeChild(childNode);
        }
        ChildNode childNode2 = (ChildNode)node2;
        childNode.ownerNode = this;
        childNode.isOwned(true);
        ChildNode childNode3 = (ChildNode)this.value;
        if (childNode3 == null) {
            this.value = childNode;
            childNode.isFirstChild(true);
            childNode.previousSibling = childNode;
        } else if (childNode2 == null) {
            ChildNode childNode4 = childNode3.previousSibling;
            childNode4.nextSibling = childNode;
            childNode.previousSibling = childNode4;
            childNode3.previousSibling = childNode;
        } else if (node2 == childNode3) {
            childNode3.isFirstChild(false);
            childNode.nextSibling = childNode3;
            childNode.previousSibling = childNode3.previousSibling;
            childNode3.previousSibling = childNode;
            this.value = childNode;
            childNode.isFirstChild(true);
        } else {
            ChildNode childNode5 = childNode2.previousSibling;
            childNode.nextSibling = childNode2;
            childNode5.nextSibling = childNode;
            childNode2.previousSibling = childNode;
            childNode.previousSibling = childNode5;
        }
        this.changed();
        coreDocumentImpl.insertedNode(this, childNode, bl);
        this.checkNormalizationAfterInsert(childNode);
        return node;
    }

    @Override
    public Node removeChild(Node node) throws DOMException {
        if (this.hasStringValue()) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, string);
        }
        return this.internalRemoveChild(node, false);
    }

    Node internalRemoveChild(Node node, boolean bl) throws DOMException {
        ChildNode childNode;
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        if (coreDocumentImpl.errorChecking) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (node != null && node.getParentNode() != this) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, string);
            }
        }
        ChildNode childNode2 = (ChildNode)node;
        coreDocumentImpl.removingNode(this, childNode2, bl);
        if (childNode2 == this.value) {
            childNode2.isFirstChild(false);
            this.value = childNode2.nextSibling;
            childNode = (ChildNode)this.value;
            if (childNode != null) {
                childNode.isFirstChild(true);
                childNode.previousSibling = childNode2.previousSibling;
            }
        } else {
            ChildNode childNode3;
            childNode = childNode2.previousSibling;
            childNode.nextSibling = childNode3 = childNode2.nextSibling;
            if (childNode3 == null) {
                ChildNode childNode4 = (ChildNode)this.value;
                childNode4.previousSibling = childNode;
            } else {
                childNode3.previousSibling = childNode;
            }
        }
        childNode = childNode2.previousSibling();
        childNode2.ownerNode = coreDocumentImpl;
        childNode2.isOwned(false);
        childNode2.nextSibling = null;
        childNode2.previousSibling = null;
        this.changed();
        coreDocumentImpl.removedNode(this, bl);
        this.checkNormalizationAfterRemove(childNode);
        return childNode2;
    }

    @Override
    public Node replaceChild(Node node, Node node2) throws DOMException {
        this.makeChildNode();
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        coreDocumentImpl.replacingNode(this);
        this.internalInsertBefore(node, node2, true);
        if (node != node2) {
            this.internalRemoveChild(node2, true);
        }
        coreDocumentImpl.replacedNode(this);
        return node2;
    }

    @Override
    public int getLength() {
        if (this.hasStringValue()) {
            return 1;
        }
        ChildNode childNode = (ChildNode)this.value;
        int n = 0;
        while (childNode != null) {
            ++n;
            childNode = childNode.nextSibling;
        }
        return n;
    }

    @Override
    public Node item(int n) {
        if (this.hasStringValue()) {
            if (n != 0 || this.value == null) {
                return null;
            }
            this.makeChildNode();
            return (Node)this.value;
        }
        if (n < 0) {
            return null;
        }
        ChildNode childNode = (ChildNode)this.value;
        for (int i = 0; i < n && childNode != null; ++i) {
            childNode = childNode.nextSibling;
        }
        return childNode;
    }

    @Override
    public boolean isEqualNode(Node node) {
        return super.isEqualNode(node);
    }

    @Override
    public boolean isDerivedFrom(String string, String string2, int n) {
        return false;
    }

    @Override
    public void setReadOnly(boolean bl, boolean bl2) {
        super.setReadOnly(bl, bl2);
        if (bl2) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            if (this.hasStringValue()) {
                return;
            }
            ChildNode childNode = (ChildNode)this.value;
            while (childNode != null) {
                if (childNode.getNodeType() != 5) {
                    childNode.setReadOnly(bl, true);
                }
                childNode = childNode.nextSibling;
            }
        }
    }

    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
    }

    void checkNormalizationAfterInsert(ChildNode childNode) {
        if (childNode.getNodeType() == 3) {
            ChildNode childNode2 = childNode.previousSibling();
            ChildNode childNode3 = childNode.nextSibling;
            if (childNode2 != null && childNode2.getNodeType() == 3 || childNode3 != null && childNode3.getNodeType() == 3) {
                this.isNormalized(false);
            }
        } else if (!childNode.isNormalized()) {
            this.isNormalized(false);
        }
    }

    void checkNormalizationAfterRemove(ChildNode childNode) {
        ChildNode childNode2;
        if (childNode != null && childNode.getNodeType() == 3 && (childNode2 = childNode.nextSibling) != null && childNode2.getNodeType() == 3) {
            this.isNormalized(false);
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        objectOutputStream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.needsSyncChildren(false);
    }
}

