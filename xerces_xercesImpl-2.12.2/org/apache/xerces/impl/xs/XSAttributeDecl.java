/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSValue;

public class XSAttributeDecl
implements XSAttributeDeclaration {
    public static final short SCOPE_ABSENT = 0;
    public static final short SCOPE_GLOBAL = 1;
    public static final short SCOPE_LOCAL = 2;
    String fName = null;
    String fTargetNamespace = null;
    XSSimpleType fType = null;
    public QName fUnresolvedTypeName = null;
    short fConstraintType = 0;
    short fScope = 0;
    XSComplexTypeDecl fEnclosingCT = null;
    XSObjectList fAnnotations = null;
    ValidatedInfo fDefault = null;
    private XSNamespaceItem fNamespaceItem = null;

    public void setValues(String string, String string2, XSSimpleType xSSimpleType, short s, short s2, ValidatedInfo validatedInfo, XSComplexTypeDecl xSComplexTypeDecl, XSObjectList xSObjectList) {
        this.fName = string;
        this.fTargetNamespace = string2;
        this.fType = xSSimpleType;
        this.fConstraintType = s;
        this.fScope = s2;
        this.fDefault = validatedInfo;
        this.fEnclosingCT = xSComplexTypeDecl;
        this.fAnnotations = xSObjectList;
    }

    public void reset() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fType = null;
        this.fUnresolvedTypeName = null;
        this.fConstraintType = 0;
        this.fScope = 0;
        this.fDefault = null;
        this.fAnnotations = null;
    }

    @Override
    public short getType() {
        return 1;
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
    public XSSimpleTypeDefinition getTypeDefinition() {
        return this.fType;
    }

    @Override
    public short getScope() {
        return this.fScope;
    }

    @Override
    public XSComplexTypeDefinition getEnclosingCTDefinition() {
        return this.fEnclosingCT;
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
    public XSAnnotation getAnnotation() {
        return this.fAnnotations != null ? (XSAnnotation)this.fAnnotations.item(0) : null;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

    public ValidatedInfo getValInfo() {
        return this.fDefault;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }

    void setNamespaceItem(XSNamespaceItem xSNamespaceItem) {
        this.fNamespaceItem = xSNamespaceItem;
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
}

