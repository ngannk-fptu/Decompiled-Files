/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.dom.ParentNode;
import org.apache.xerces.util.URI;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class EntityReferenceImpl
extends ParentNode
implements EntityReference {
    static final long serialVersionUID = -7381452955687102062L;
    protected String name;
    protected String baseURI;

    public EntityReferenceImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.name = string;
        this.isReadOnly(true);
        this.needsSyncChildren(true);
    }

    @Override
    public short getNodeType() {
        return 5;
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
        EntityReferenceImpl entityReferenceImpl = (EntityReferenceImpl)super.cloneNode(bl);
        entityReferenceImpl.setReadOnly(true, bl);
        return entityReferenceImpl;
    }

    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.baseURI == null) {
            EntityImpl entityImpl;
            NamedNodeMap namedNodeMap;
            DocumentType documentType = this.getOwnerDocument().getDoctype();
            if (null != documentType && null != (namedNodeMap = documentType.getEntities()) && (entityImpl = (EntityImpl)namedNodeMap.getNamedItem(this.getNodeName())) != null) {
                return entityImpl.getBaseURI();
            }
        } else if (this.baseURI != null && this.baseURI.length() != 0) {
            try {
                return new URI(this.baseURI).toString();
            }
            catch (URI.MalformedURIException malformedURIException) {
                return null;
            }
        }
        return this.baseURI;
    }

    public void setBaseURI(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = string;
    }

    protected String getEntityRefValue() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        String string = "";
        if (this.firstChild != null) {
            if (this.firstChild.getNodeType() == 5) {
                string = ((EntityReferenceImpl)this.firstChild).getEntityRefValue();
            } else if (this.firstChild.getNodeType() == 3) {
                string = this.firstChild.getNodeValue();
            } else {
                return null;
            }
            if (this.firstChild.nextSibling == null) {
                return string;
            }
            StringBuffer stringBuffer = new StringBuffer(string);
            ChildNode childNode = this.firstChild.nextSibling;
            while (childNode != null) {
                if (childNode.getNodeType() == 5) {
                    string = ((EntityReferenceImpl)childNode).getEntityRefValue();
                } else if (childNode.getNodeType() == 3) {
                    string = childNode.getNodeValue();
                } else {
                    return null;
                }
                stringBuffer.append(string);
                childNode = childNode.nextSibling;
            }
            return stringBuffer.toString();
        }
        return "";
    }

    @Override
    protected void synchronizeChildren() {
        NamedNodeMap namedNodeMap;
        this.needsSyncChildren(false);
        DocumentType documentType = this.getOwnerDocument().getDoctype();
        if (null != documentType && null != (namedNodeMap = documentType.getEntities())) {
            EntityImpl entityImpl = (EntityImpl)namedNodeMap.getNamedItem(this.getNodeName());
            if (entityImpl == null) {
                return;
            }
            this.isReadOnly(false);
            for (Node node = entityImpl.getFirstChild(); node != null; node = node.getNextSibling()) {
                Node node2 = node.cloneNode(true);
                this.insertBefore(node2, null);
            }
            this.setReadOnly(true, true);
        }
    }

    @Override
    public void setReadOnly(boolean bl, boolean bl2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (bl2) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            ChildNode childNode = this.firstChild;
            while (childNode != null) {
                childNode.setReadOnly(bl, true);
                childNode = childNode.nextSibling;
            }
        }
        this.isReadOnly(bl);
    }
}

