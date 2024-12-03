/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSWildcard;

public class XSAttributeGroupDecl
implements XSAttributeGroupDefinition {
    public String fName = null;
    public String fTargetNamespace = null;
    int fAttrUseNum = 0;
    private static final int INITIAL_SIZE = 5;
    XSAttributeUseImpl[] fAttributeUses = new XSAttributeUseImpl[5];
    public XSWildcardDecl fAttributeWC = null;
    public String fIDAttrName = null;
    public XSObjectList fAnnotations;
    protected XSObjectListImpl fAttrUses = null;
    private XSNamespaceItem fNamespaceItem = null;

    public String addAttributeUse(XSAttributeUseImpl xSAttributeUseImpl) {
        if (xSAttributeUseImpl.fUse != 2 && xSAttributeUseImpl.fAttrDecl.fType.isIDType()) {
            if (this.fIDAttrName == null) {
                this.fIDAttrName = xSAttributeUseImpl.fAttrDecl.fName;
            } else {
                return this.fIDAttrName;
            }
        }
        if (this.fAttrUseNum == this.fAttributeUses.length) {
            this.fAttributeUses = XSAttributeGroupDecl.resize(this.fAttributeUses, this.fAttrUseNum * 2);
        }
        this.fAttributeUses[this.fAttrUseNum++] = xSAttributeUseImpl;
        return null;
    }

    public void replaceAttributeUse(XSAttributeUse xSAttributeUse, XSAttributeUseImpl xSAttributeUseImpl) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i] != xSAttributeUse) continue;
            this.fAttributeUses[i] = xSAttributeUseImpl;
        }
    }

    public XSAttributeUse getAttributeUse(String string, String string2) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace != string || this.fAttributeUses[i].fAttrDecl.fName != string2) continue;
            return this.fAttributeUses[i];
        }
        return null;
    }

    public XSAttributeUse getAttributeUseNoProhibited(String string, String string2) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace != string || this.fAttributeUses[i].fAttrDecl.fName != string2 || this.fAttributeUses[i].fUse == 2) continue;
            return this.fAttributeUses[i];
        }
        return null;
    }

    public void removeProhibitedAttrs() {
        if (this.fAttrUseNum == 0) {
            return;
        }
        int n = 0;
        XSAttributeUseImpl[] xSAttributeUseImplArray = new XSAttributeUseImpl[this.fAttrUseNum];
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fUse == 2) continue;
            xSAttributeUseImplArray[n++] = this.fAttributeUses[i];
        }
        this.fAttributeUses = xSAttributeUseImplArray;
        this.fAttrUseNum = n;
    }

    public Object[] validRestrictionOf(String string, XSAttributeGroupDecl xSAttributeGroupDecl) {
        int n;
        Object[] objectArray = null;
        XSAttributeUseImpl xSAttributeUseImpl = null;
        XSAttributeDecl xSAttributeDecl = null;
        XSAttributeUseImpl xSAttributeUseImpl2 = null;
        XSAttributeDecl xSAttributeDecl2 = null;
        for (n = 0; n < this.fAttrUseNum; ++n) {
            xSAttributeUseImpl = this.fAttributeUses[n];
            xSAttributeDecl = xSAttributeUseImpl.fAttrDecl;
            xSAttributeUseImpl2 = (XSAttributeUseImpl)xSAttributeGroupDecl.getAttributeUse(xSAttributeDecl.fTargetNamespace, xSAttributeDecl.fName);
            if (xSAttributeUseImpl2 != null) {
                ValidatedInfo validatedInfo;
                short s;
                if (xSAttributeUseImpl2.getRequired() && !xSAttributeUseImpl.getRequired()) {
                    objectArray = new Object[]{string, xSAttributeDecl.fName, xSAttributeUseImpl.fUse == 0 ? "optional" : "prohibited", "derivation-ok-restriction.2.1.1"};
                    return objectArray;
                }
                if (xSAttributeUseImpl.fUse == 2) continue;
                xSAttributeDecl2 = xSAttributeUseImpl2.fAttrDecl;
                if (!XSConstraints.checkSimpleDerivationOk(xSAttributeDecl.fType, xSAttributeDecl2.fType, xSAttributeDecl2.fType.getFinal())) {
                    objectArray = new Object[]{string, xSAttributeDecl.fName, xSAttributeDecl.fType.getName(), xSAttributeDecl2.fType.getName(), "derivation-ok-restriction.2.1.2"};
                    return objectArray;
                }
                short s2 = xSAttributeUseImpl2.fConstraintType != 0 ? xSAttributeUseImpl2.fConstraintType : xSAttributeDecl2.getConstraintType();
                short s3 = s = xSAttributeUseImpl.fConstraintType != 0 ? xSAttributeUseImpl.fConstraintType : xSAttributeDecl.getConstraintType();
                if (s2 != 2) continue;
                if (s != 2) {
                    objectArray = new Object[]{string, xSAttributeDecl.fName, "derivation-ok-restriction.2.1.3.a"};
                    return objectArray;
                }
                ValidatedInfo validatedInfo2 = xSAttributeUseImpl2.fDefault != null ? xSAttributeUseImpl2.fDefault : xSAttributeDecl2.fDefault;
                ValidatedInfo validatedInfo3 = validatedInfo = xSAttributeUseImpl.fDefault != null ? xSAttributeUseImpl.fDefault : xSAttributeDecl.fDefault;
                if (validatedInfo2.actualValue.equals(validatedInfo.actualValue)) continue;
                objectArray = new Object[]{string, xSAttributeDecl.fName, validatedInfo.stringValue(), validatedInfo2.stringValue(), "derivation-ok-restriction.2.1.3.b"};
                return objectArray;
            }
            if (xSAttributeGroupDecl.fAttributeWC == null) {
                objectArray = new Object[]{string, xSAttributeDecl.fName, "derivation-ok-restriction.2.2.a"};
                return objectArray;
            }
            if (xSAttributeGroupDecl.fAttributeWC.allowNamespace(xSAttributeDecl.fTargetNamespace)) continue;
            objectArray = new Object[]{string, xSAttributeDecl.fName, xSAttributeDecl.fTargetNamespace == null ? "" : xSAttributeDecl.fTargetNamespace, "derivation-ok-restriction.2.2.b"};
            return objectArray;
        }
        for (n = 0; n < xSAttributeGroupDecl.fAttrUseNum; ++n) {
            xSAttributeUseImpl2 = xSAttributeGroupDecl.fAttributeUses[n];
            if (xSAttributeUseImpl2.fUse != 1) continue;
            xSAttributeDecl2 = xSAttributeUseImpl2.fAttrDecl;
            if (this.getAttributeUse(xSAttributeDecl2.fTargetNamespace, xSAttributeDecl2.fName) != null) continue;
            objectArray = new Object[]{string, xSAttributeUseImpl2.fAttrDecl.fName, "derivation-ok-restriction.3"};
            return objectArray;
        }
        if (this.fAttributeWC != null) {
            if (xSAttributeGroupDecl.fAttributeWC == null) {
                objectArray = new Object[]{string, "derivation-ok-restriction.4.1"};
                return objectArray;
            }
            if (!this.fAttributeWC.isSubsetOf(xSAttributeGroupDecl.fAttributeWC)) {
                objectArray = new Object[]{string, "derivation-ok-restriction.4.2"};
                return objectArray;
            }
            if (this.fAttributeWC.weakerProcessContents(xSAttributeGroupDecl.fAttributeWC)) {
                objectArray = new Object[]{string, this.fAttributeWC.getProcessContentsAsString(), xSAttributeGroupDecl.fAttributeWC.getProcessContentsAsString(), "derivation-ok-restriction.4.3"};
                return objectArray;
            }
        }
        return null;
    }

    static final XSAttributeUseImpl[] resize(XSAttributeUseImpl[] xSAttributeUseImplArray, int n) {
        XSAttributeUseImpl[] xSAttributeUseImplArray2 = new XSAttributeUseImpl[n];
        System.arraycopy(xSAttributeUseImplArray, 0, xSAttributeUseImplArray2, 0, Math.min(xSAttributeUseImplArray.length, n));
        return xSAttributeUseImplArray2;
    }

    public void reset() {
        this.fName = null;
        this.fTargetNamespace = null;
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            this.fAttributeUses[i] = null;
        }
        this.fAttrUseNum = 0;
        this.fAttributeWC = null;
        this.fAnnotations = null;
        this.fIDAttrName = null;
    }

    @Override
    public short getType() {
        return 5;
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
    public XSObjectList getAttributeUses() {
        if (this.fAttrUses == null) {
            this.fAttrUses = this.fAttrUseNum > 0 ? new XSObjectListImpl(this.fAttributeUses, this.fAttrUseNum) : XSObjectListImpl.EMPTY_LIST;
        }
        return this.fAttrUses;
    }

    @Override
    public XSWildcard getAttributeWildcard() {
        return this.fAttributeWC;
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

