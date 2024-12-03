/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.xs.PSVIErrorList;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSModelImpl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSValue;

public class ElementPSVImpl
implements ElementPSVI {
    protected XSElementDeclaration fDeclaration = null;
    protected XSTypeDefinition fTypeDecl = null;
    protected boolean fNil = false;
    protected boolean fSpecified = false;
    protected ValidatedInfo fValue = new ValidatedInfo();
    protected XSNotationDeclaration fNotation = null;
    protected short fValidationAttempted = 0;
    protected short fValidity = 0;
    protected String[] fErrors = null;
    protected String fValidationContext = null;
    protected SchemaGrammar[] fGrammars = null;
    protected XSModel fSchemaInformation = null;
    protected boolean fIsConstant;

    public ElementPSVImpl() {
    }

    public ElementPSVImpl(boolean bl, ElementPSVI elementPSVI) {
        this.fDeclaration = elementPSVI.getElementDeclaration();
        this.fTypeDecl = elementPSVI.getTypeDefinition();
        this.fNil = elementPSVI.getNil();
        this.fSpecified = elementPSVI.getIsSchemaSpecified();
        this.fValue.copyFrom(elementPSVI.getSchemaValue());
        this.fNotation = elementPSVI.getNotation();
        this.fValidationAttempted = elementPSVI.getValidationAttempted();
        this.fValidity = elementPSVI.getValidity();
        this.fValidationContext = elementPSVI.getValidationContext();
        if (elementPSVI instanceof ElementPSVImpl) {
            ElementPSVImpl elementPSVImpl = (ElementPSVImpl)elementPSVI;
            this.fErrors = elementPSVImpl.fErrors != null ? (String[])elementPSVImpl.fErrors.clone() : null;
            elementPSVImpl.copySchemaInformationTo(this);
        } else {
            StringList stringList = elementPSVI.getErrorCodes();
            int n = stringList.getLength();
            if (n > 0) {
                StringList stringList2 = elementPSVI.getErrorMessages();
                String[] stringArray = new String[n << 1];
                int n2 = 0;
                for (int i = 0; i < n; ++i) {
                    stringArray[n2++] = stringList.item(i);
                    stringArray[n2++] = stringList2.item(i);
                }
                this.fErrors = stringArray;
            }
            this.fSchemaInformation = elementPSVI.getSchemaInformation();
        }
        this.fIsConstant = bl;
    }

    @Override
    public ItemPSVI constant() {
        if (this.isConstant()) {
            return this;
        }
        return new ElementPSVImpl(true, this);
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
    public boolean getNil() {
        return this.fNil;
    }

    @Override
    public XSNotationDeclaration getNotation() {
        return this.fNotation;
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
    public XSElementDeclaration getElementDeclaration() {
        return this.fDeclaration;
    }

    @Override
    public synchronized XSModel getSchemaInformation() {
        if (this.fSchemaInformation == null && this.fGrammars != null) {
            this.fSchemaInformation = new XSModelImpl(this.fGrammars);
        }
        return this.fSchemaInformation;
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
        this.fDeclaration = null;
        this.fTypeDecl = null;
        this.fNil = false;
        this.fSpecified = false;
        this.fNotation = null;
        this.fValidationAttempted = 0;
        this.fValidity = 0;
        this.fErrors = null;
        this.fValidationContext = null;
        this.fValue.reset();
    }

    public void copySchemaInformationTo(ElementPSVImpl elementPSVImpl) {
        elementPSVImpl.fGrammars = this.fGrammars;
        elementPSVImpl.fSchemaInformation = this.fSchemaInformation;
    }
}

