/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.Hashtable;
import org.apache.xerces.dom.CoreDOMImplementationImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.NamedNodeMapImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.ParentNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

public class DocumentTypeImpl
extends ParentNode
implements DocumentType {
    static final long serialVersionUID = 7751299192316526485L;
    protected String name;
    protected NamedNodeMapImpl entities;
    protected NamedNodeMapImpl notations;
    protected NamedNodeMapImpl elements;
    protected String publicID;
    protected String systemID;
    protected String internalSubset;
    private int doctypeNumber = 0;
    private Hashtable userData = null;

    public DocumentTypeImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.name = string;
        this.entities = new NamedNodeMapImpl(this);
        this.notations = new NamedNodeMapImpl(this);
        this.elements = new NamedNodeMapImpl(this);
    }

    public DocumentTypeImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2, String string3) {
        this(coreDocumentImpl, string);
        this.publicID = string2;
        this.systemID = string3;
    }

    @Override
    public String getPublicId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.publicID;
    }

    @Override
    public String getSystemId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.systemID;
    }

    public void setInternalSubset(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.internalSubset = string;
    }

    @Override
    public String getInternalSubset() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalSubset;
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
        return this.name;
    }

    @Override
    public Node cloneNode(boolean bl) {
        DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)super.cloneNode(bl);
        documentTypeImpl.entities = this.entities.cloneMap(documentTypeImpl);
        documentTypeImpl.notations = this.notations.cloneMap(documentTypeImpl);
        documentTypeImpl.elements = this.elements.cloneMap(documentTypeImpl);
        return documentTypeImpl;
    }

    @Override
    public String getTextContent() throws DOMException {
        return null;
    }

    @Override
    public void setTextContent(String string) throws DOMException {
    }

    @Override
    public boolean isEqualNode(Node node) {
        Node node2;
        if (!super.isEqualNode(node)) {
            return false;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)node;
        if (this.getPublicId() == null && documentTypeImpl.getPublicId() != null || this.getPublicId() != null && documentTypeImpl.getPublicId() == null || this.getSystemId() == null && documentTypeImpl.getSystemId() != null || this.getSystemId() != null && documentTypeImpl.getSystemId() == null || this.getInternalSubset() == null && documentTypeImpl.getInternalSubset() != null || this.getInternalSubset() != null && documentTypeImpl.getInternalSubset() == null) {
            return false;
        }
        if (this.getPublicId() != null && !this.getPublicId().equals(documentTypeImpl.getPublicId())) {
            return false;
        }
        if (this.getSystemId() != null && !this.getSystemId().equals(documentTypeImpl.getSystemId())) {
            return false;
        }
        if (this.getInternalSubset() != null && !this.getInternalSubset().equals(documentTypeImpl.getInternalSubset())) {
            return false;
        }
        NamedNodeMapImpl namedNodeMapImpl = documentTypeImpl.entities;
        if (this.entities == null && namedNodeMapImpl != null || this.entities != null && namedNodeMapImpl == null) {
            return false;
        }
        if (this.entities != null && namedNodeMapImpl != null) {
            if (this.entities.getLength() != namedNodeMapImpl.getLength()) {
                return false;
            }
            int n = 0;
            while (this.entities.item(n) != null) {
                Node node3 = this.entities.item(n);
                if (!((NodeImpl)node3).isEqualNode(node2 = namedNodeMapImpl.getNamedItem(node3.getNodeName()))) {
                    return false;
                }
                ++n;
            }
        }
        NamedNodeMapImpl namedNodeMapImpl2 = documentTypeImpl.notations;
        if (this.notations == null && namedNodeMapImpl2 != null || this.notations != null && namedNodeMapImpl2 == null) {
            return false;
        }
        if (this.notations != null && namedNodeMapImpl2 != null) {
            if (this.notations.getLength() != namedNodeMapImpl2.getLength()) {
                return false;
            }
            int n = 0;
            while (this.notations.item(n) != null) {
                Node node4;
                node2 = this.notations.item(n);
                if (!((NodeImpl)node2).isEqualNode(node4 = namedNodeMapImpl2.getNamedItem(node2.getNodeName()))) {
                    return false;
                }
                ++n;
            }
        }
        return true;
    }

    @Override
    protected void setOwnerDocument(CoreDocumentImpl coreDocumentImpl) {
        super.setOwnerDocument(coreDocumentImpl);
        this.entities.setOwnerDocument(coreDocumentImpl);
        this.notations.setOwnerDocument(coreDocumentImpl);
        this.elements.setOwnerDocument(coreDocumentImpl);
    }

    @Override
    protected int getNodeNumber() {
        if (this.getOwnerDocument() != null) {
            return super.getNodeNumber();
        }
        if (this.doctypeNumber == 0) {
            CoreDOMImplementationImpl coreDOMImplementationImpl = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            this.doctypeNumber = coreDOMImplementationImpl.assignDocTypeNumber();
        }
        return this.doctypeNumber;
    }

    @Override
    public String getName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public NamedNodeMap getEntities() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.entities;
    }

    @Override
    public NamedNodeMap getNotations() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.notations;
    }

    @Override
    public void setReadOnly(boolean bl, boolean bl2) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        super.setReadOnly(bl, bl2);
        this.elements.setReadOnly(bl, true);
        this.entities.setReadOnly(bl, true);
        this.notations.setReadOnly(bl, true);
    }

    public NamedNodeMap getElements() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.elements;
    }

    @Override
    public Object setUserData(String string, Object object, UserDataHandler userDataHandler) {
        if (this.userData == null) {
            this.userData = new Hashtable();
        }
        if (object == null) {
            Object v;
            if (this.userData != null && (v = this.userData.remove(string)) != null) {
                ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)v;
                return userDataRecord.fData;
            }
            return null;
        }
        ParentNode.UserDataRecord userDataRecord = this.userData.put(string, new ParentNode.UserDataRecord(this, object, userDataHandler));
        if (userDataRecord != null) {
            ParentNode.UserDataRecord userDataRecord2 = userDataRecord;
            return userDataRecord2.fData;
        }
        return null;
    }

    @Override
    public Object getUserData(String string) {
        if (this.userData == null) {
            return null;
        }
        Object v = this.userData.get(string);
        if (v != null) {
            ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)v;
            return userDataRecord.fData;
        }
        return null;
    }

    @Override
    protected Hashtable getUserDataRecord() {
        return this.userData;
    }
}

