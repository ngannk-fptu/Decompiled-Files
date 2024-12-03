/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.xs.PSVIErrorList;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSValue;

public class AttributePSVImpl
implements AttributePSVI {
    protected XSAttributeDeclaration fDeclaration = null;
    protected XSTypeDefinition fTypeDecl = null;
    protected boolean fSpecified = false;
    protected ValidatedInfo fValue = new ValidatedInfo();
    protected short fValidationAttempted = 0;
    protected short fValidity = 0;
    protected String[] fErrors = null;
    protected String fValidationContext = null;
    protected boolean fIsConstant;

    public AttributePSVImpl() {
    }

    public AttributePSVImpl(boolean bl, AttributePSVI attributePSVI) {
        this.fDeclaration = attributePSVI.getAttributeDeclaration();
        this.fTypeDecl = attributePSVI.getTypeDefinition();
        this.fSpecified = attributePSVI.getIsSchemaSpecified();
        this.fValue.copyFrom(attributePSVI.getSchemaValue());
        this.fValidationAttempted = attributePSVI.getValidationAttempted();
        this.fValidity = attributePSVI.getValidity();
        if (attributePSVI instanceof AttributePSVImpl) {
            AttributePSVImpl attributePSVImpl = (AttributePSVImpl)attributePSVI;
            this.fErrors = attributePSVImpl.fErrors != null ? (String[])attributePSVImpl.fErrors.clone() : null;
        } else {
            StringList stringList = attributePSVI.getErrorCodes();
            int n = stringList.getLength();
            if (n > 0) {
                StringList stringList2 = attributePSVI.getErrorMessages();
                String[] stringArray = new String[n << 1];
                int n2 = 0;
                for (int i = 0; i < n; ++i) {
                    stringArray[n2++] = stringList.item(i);
                    stringArray[n2++] = stringList2.item(i);
                }
                this.fErrors = stringArray;
            }
        }
        this.fValidationContext = attributePSVI.getValidationContext();
        this.fIsConstant = bl;
    }

    @Override
    public ItemPSVI constant() {
        if (this.isConstant()) {
            return this;
        }
        return new AttributePSVImpl(true, this);
    }

    @Override
    public boolean isConstant() {
        return this.fIsConstant;
    }

    @Override
    public String getSchemaDefault() {
        return this.fDeclaration == null ? null : this.fDeclaration.getConstraintValue();
    }

    @Override
    public String getSchemaNormalizedValue() {
        return this.fValue.getNormalizedValue();
    }

    @Override
    public boolean getIsSchemaSpecified() {
        return this.fSpecified;
    }

    @Override
    public short getValidationAttempted() {
        return this.fValidationAttempted;
    }

    @Override
    public short getValidity() {
        return this.fValidity;
    }

    @Override
    public StringList getErrorCodes() {
        if (this.fErrors == null || this.fErrors.length == 0) {
            return StringListImpl.EMPTY_LIST;
        }
        return new PSVIErrorList(this.fErrors, true);
    }

    @Override
    public StringList getErrorMessages() {
        if (this.fErrors == null || this.fErrors.length == 0) {
            return StringListImpl.EMPTY_LIST;
        }
        return new PSVIErrorList(this.fErrors, false);
    }

    @Override
    public String getValidationContext() {
        return this.fValidationContext;
    }

    @Override
    public XSTypeDefinition getTypeDefinition() {
        return this.fTypeDecl;
    }

    @Override
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return this.fValue.getMemberTypeDefinition();
    }

    @Override
    public XSAttributeDeclaration getAttributeDeclaration() {
        return this.fDeclaration;
    }

    @Override
    public Object getActualNormalizedValue() {
        return this.fValue.getActualValue();
    }

    @Override
    public short getActualNormalizedValueType() {
        return this.fValue.getActualValueType();
    }

    @Override
    public ShortList getItemValueTypes() {
        return this.fValue.getListValueTypes();
    }

    @Override
    public XSValue getSchemaValue() {
        return this.fValue;
    }

    public void reset() {
        this.fValue.reset();
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fSpecified = false;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
    }
}

