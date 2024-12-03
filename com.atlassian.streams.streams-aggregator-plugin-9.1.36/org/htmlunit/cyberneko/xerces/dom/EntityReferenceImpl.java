/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.ChildNode;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.EntityImpl;
import org.htmlunit.cyberneko.xerces.dom.ParentNode;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class EntityReferenceImpl
extends ParentNode
implements EntityReference {
    protected final String name;

    public EntityReferenceImpl(CoreDocumentImpl ownerDoc, String name) {
        super(ownerDoc);
        this.name = name;
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
    public Node cloneNode(boolean deep) {
        EntityReferenceImpl er = (EntityReferenceImpl)super.cloneNode(deep);
        return er;
    }

    @Override
    public String getBaseURI() {
        EntityImpl entDef;
        NamedNodeMap entities;
        DocumentType doctype;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (null != (doctype = this.getOwnerDocument().getDoctype()) && null != (entities = doctype.getEntities()) && (entDef = (EntityImpl)entities.getNamedItem(this.getNodeName())) != null) {
            return entDef.getBaseURI();
        }
        return null;
    }

    protected String getEntityRefValue() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.firstChild != null) {
            String value;
            if (this.firstChild.getNodeType() == 5) {
                value = ((EntityReferenceImpl)this.firstChild).getEntityRefValue();
            } else if (this.firstChild.getNodeType() == 3) {
                value = this.firstChild.getNodeValue();
            } else {
                return null;
            }
            if (this.firstChild.nextSibling == null) {
                return value;
            }
            StringBuilder buff = new StringBuilder(value);
            ChildNode next = this.firstChild.nextSibling;
            while (next != null) {
                if (next.getNodeType() == 5) {
                    value = ((EntityReferenceImpl)next).getEntityRefValue();
                } else if (next.getNodeType() == 3) {
                    value = next.getNodeValue();
                } else {
                    return null;
                }
                buff.append(value);
                next = next.nextSibling;
            }
            return buff.toString();
        }
        return "";
    }

    @Override
    protected void synchronizeChildren() {
        NamedNodeMap entities;
        this.needsSyncChildren(false);
        DocumentType doctype = this.getOwnerDocument().getDoctype();
        if (null != doctype && null != (entities = doctype.getEntities())) {
            EntityImpl entDef = (EntityImpl)entities.getNamedItem(this.getNodeName());
            if (entDef == null) {
                return;
            }
            for (Node defkid = entDef.getFirstChild(); defkid != null; defkid = defkid.getNextSibling()) {
                Node newkid = defkid.cloneNode(true);
                this.insertBefore(newkid, null);
            }
        }
    }
}

