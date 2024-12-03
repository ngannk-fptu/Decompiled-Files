/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDOMImplementationImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.NamedNodeMapImpl;
import org.htmlunit.cyberneko.xerces.dom.ParentNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

public class DocumentTypeImpl
extends ParentNode
implements DocumentType {
    private final String name_;
    private NamedNodeMapImpl entities_;
    private NamedNodeMapImpl notations_;
    private NamedNodeMapImpl elements_;
    private String publicID_;
    private String systemID_;
    private String internalSubset_;
    private int doctypeNumber_ = 0;

    public DocumentTypeImpl(CoreDocumentImpl ownerDocument, String name) {
        super(ownerDocument);
        this.name_ = name;
        this.entities_ = new NamedNodeMapImpl(this);
        this.notations_ = new NamedNodeMapImpl(this);
        this.elements_ = new NamedNodeMapImpl(this);
    }

    public DocumentTypeImpl(CoreDocumentImpl ownerDocument, String qualifiedName, String publicID, String systemID) {
        this(ownerDocument, qualifiedName);
        this.publicID_ = publicID;
        this.systemID_ = systemID;
    }

    @Override
    public String getPublicId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.publicID_;
    }

    @Override
    public String getSystemId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.systemID_;
    }

    public void setInternalSubset(String internalSubset) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.internalSubset_ = internalSubset;
    }

    @Override
    public String getInternalSubset() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalSubset_;
    }

    @Override
    public short getNodeType() {
        return 10;
    }

    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name_;
    }

    @Override
    public Node cloneNode(boolean deep) {
        DocumentTypeImpl newnode = (DocumentTypeImpl)super.cloneNode(deep);
        newnode.entities_ = this.entities_.cloneMap(newnode);
        newnode.notations_ = this.notations_.cloneMap(newnode);
        newnode.elements_ = this.elements_.cloneMap(newnode);
        return newnode;
    }

    @Override
    public String getTextContent() throws DOMException {
        return null;
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
    }

    @Override
    public boolean isEqualNode(Node arg) {
        if (!super.isEqualNode(arg)) {
            return false;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        DocumentTypeImpl argDocType = (DocumentTypeImpl)arg;
        if (this.getPublicId() == null && argDocType.getPublicId() != null || this.getPublicId() != null && argDocType.getPublicId() == null || this.getSystemId() == null && argDocType.getSystemId() != null || this.getSystemId() != null && argDocType.getSystemId() == null || this.getInternalSubset() == null && argDocType.getInternalSubset() != null || this.getInternalSubset() != null && argDocType.getInternalSubset() == null) {
            return false;
        }
        if (this.getPublicId() != null && !this.getPublicId().equals(argDocType.getPublicId())) {
            return false;
        }
        if (this.getSystemId() != null && !this.getSystemId().equals(argDocType.getSystemId())) {
            return false;
        }
        if (this.getInternalSubset() != null && !this.getInternalSubset().equals(argDocType.getInternalSubset())) {
            return false;
        }
        NamedNodeMapImpl argEntities = argDocType.entities_;
        if (this.entities_ == null && argEntities != null || this.entities_ != null && argEntities == null) {
            return false;
        }
        if (this.entities_ != null && argEntities != null) {
            if (this.entities_.getLength() != argEntities.getLength()) {
                return false;
            }
            int index = 0;
            while (this.entities_.item(index) != null) {
                Node entNode2;
                Node entNode1 = this.entities_.item(index);
                if (!entNode1.isEqualNode(entNode2 = argEntities.getNamedItem(entNode1.getNodeName()))) {
                    return false;
                }
                ++index;
            }
        }
        NamedNodeMapImpl argNotations = argDocType.notations_;
        if (this.notations_ == null && argNotations != null || this.notations_ != null && argNotations == null) {
            return false;
        }
        if (this.notations_ != null && argNotations != null) {
            if (this.notations_.getLength() != argNotations.getLength()) {
                return false;
            }
            int index = 0;
            while (this.notations_.item(index) != null) {
                Node noteNode2;
                Node noteNode1 = this.notations_.item(index);
                if (!noteNode1.isEqualNode(noteNode2 = argNotations.getNamedItem(noteNode1.getNodeName()))) {
                    return false;
                }
                ++index;
            }
        }
        return true;
    }

    @Override
    protected void setOwnerDocument(CoreDocumentImpl doc) {
        super.setOwnerDocument(doc);
        this.entities_.setOwnerDocument(doc);
        this.notations_.setOwnerDocument(doc);
        this.elements_.setOwnerDocument(doc);
    }

    @Override
    protected int getNodeNumber() {
        if (this.getOwnerDocument() != null) {
            return super.getNodeNumber();
        }
        if (this.doctypeNumber_ == 0) {
            CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            this.doctypeNumber_ = cd.assignDocTypeNumber();
        }
        return this.doctypeNumber_;
    }

    @Override
    public String getName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name_;
    }

    @Override
    public NamedNodeMap getEntities() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.entities_;
    }

    @Override
    public NamedNodeMap getNotations() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.notations_;
    }

    public NamedNodeMap getElements() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.elements_;
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return null;
    }

    @Override
    public Object getUserData(String key) {
        return null;
    }
}

