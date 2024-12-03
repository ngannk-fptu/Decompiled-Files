/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSValue;

public class XSAttributeUseImpl
implements XSAttributeUse {
    public XSAttributeDecl fAttrDecl = null;
    public short fUse = 0;
    public short fConstraintType = 0;
    public ValidatedInfo fDefault = null;
    public XSObjectList fAnnotations = null;

    public void reset() {
        this.fDefault = null;
        this.fAttrDecl = null;
        this.fUse = 0;
        this.fConstraintType = 0;
        this.fAnnotations = null;
    }

    @Override
    public short getType() {
        return 4;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public boolean getRequired() {
        return this.fUse == 1;
    }

    @Override
    public XSAttributeDeclaration getAttrDeclaration() {
        return this.fAttrDecl;
    }

    @Override
    public short getConstraintType() {
        return this.fConstraintType;
    }

    @Override
    public String getConstraintValue() {
        return this.getConstraintType() == 0 ? null : this.fDefault.stringValue();
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

    @Override
    public Object getActualVC() {
        return this.getConstraintType() == 0 ? null : this.fDefault.actualValue;
    }

    @Override
    public short getActualVCType() {
        return this.getConstraintType() == 0 ? (short)45 : this.fDefault.actualValueType;
    }

    @Override
    public ShortList getItemValueTypes() {
        return this.getConstraintType() == 0 ? null : this.fDefault.itemValueTypes;
    }

    @Override
    public XSValue getValueConstraintValue() {
        return this.fDefault;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
}

