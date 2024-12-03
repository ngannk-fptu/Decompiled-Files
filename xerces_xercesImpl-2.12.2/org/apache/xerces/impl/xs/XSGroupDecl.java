/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;

public class XSGroupDecl
implements XSModelGroupDefinition {
    public String fName = null;
    public String fTargetNamespace = null;
    public XSModelGroupImpl fModelGroup = null;
    public XSObjectList fAnnotations = null;
    private XSNamespaceItem fNamespaceItem = null;

    @Override
    public short getType() {
        return 6;
    }

    @Override
    public String getName() {
        return this.fName;
    }

    @Override
    public String getNamespace() {
        return this.fTargetNamespace;
    }

    @Override
    public XSModelGroup getModelGroup() {
        return this.fModelGroup;
    }

    @Override
    public XSAnnotation getAnnotation() {
        return this.fAnnotations != null ? (XSAnnotation)this.fAnnotations.item(0) : null;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }

    void setNamespaceItem(XSNamespaceItem xSNamespaceItem) {
        this.fNamespaceItem = xSNamespaceItem;
    }
}

