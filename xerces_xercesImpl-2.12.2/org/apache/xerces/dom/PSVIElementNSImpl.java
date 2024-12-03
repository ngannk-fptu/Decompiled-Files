/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.xs.ElementPSVImpl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSValue;

public class PSVIElementNSImpl
extends ElementNSImpl
implements ElementPSVI {
    static final long serialVersionUID = 6815489624636016068L;
    protected XSElementDeclaration fDeclaration = null;
    protected XSTypeDefinition fTypeDecl = null;
    protected boolean fNil = false;
    protected boolean fSpecified = true;
    protected ValidatedInfo fValue = new ValidatedInfo();
    protected XSNotationDeclaration fNotation = null;
    protected short fValidationAttempted = 0;
    protected short fValidity = 0;
    protected StringList fErrorCodes = null;
    protected StringList fErrorMessages = null;
    protected String fValidationContext = null;
    protected XSModel fSchemaInformation = null;

    public PSVIElementNSImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2, String string3) {
        super(coreDocumentImpl, string, string2, string3);
    }

    public PSVIElementNSImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2) {
        super(coreDocumentImpl, string, string2);
    }

    @Override
    public ItemPSVI constant() {
        return new ElementPSVImpl(true, this);
    }

    @Override
    public boolean isConstant() {
        return false;
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
        if (this.fErrorCodes != null) {
            return this.fErrorCodes;
        }
        return StringListImpl.EMPTY_LIST;
    }

    @Override
    public StringList getErrorMessages() {
        if (this.fErrorMessages != null) {
            return this.fErrorMessages;
        }
        return StringListImpl.EMPTY_LIST;
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
    public XSModel getSchemaInformation() {
        return this.fSchemaInformation;
    }

    public void setPSVI(ElementPSVI elementPSVI) {
        this.fDeclaration = elementPSVI.getElementDeclaration();
        this.fNotation = elementPSVI.getNotation();
        this.fValidationContext = elementPSVI.getValidationContext();
        this.fTypeDecl = elementPSVI.getTypeDefinition();
        this.fSchemaInformation = elementPSVI.getSchemaInformation();
        this.fValidity = elementPSVI.getValidity();
        this.fValidationAttempted = elementPSVI.getValidationAttempted();
        this.fErrorCodes = elementPSVI.getErrorCodes();
        this.fErrorMessages = elementPSVI.getErrorMessages();
        if (this.fTypeDecl instanceof XSSimpleTypeDefinition || this.fTypeDecl instanceof XSComplexTypeDefinition && ((XSComplexTypeDefinition)this.fTypeDecl).getContentType() == 1) {
            this.fValue.copyFrom(elementPSVI.getSchemaValue());
        } else {
            this.fValue.reset();
        }
        this.fSpecified = elementPSVI.getIsSchemaSpecified();
        this.fNil = elementPSVI.getNil();
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

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(this.getClass().getName());
    }
}

