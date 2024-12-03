/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.xerces.dom.AttrNSImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.xs.AttributePSVImpl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSValue;

public class PSVIAttrNSImpl
extends AttrNSImpl
implements AttributePSVI {
    static final long serialVersionUID = -3241738699421018889L;
    protected XSAttributeDeclaration fDeclaration = null;
    protected XSTypeDefinition fTypeDecl = null;
    protected boolean fSpecified = true;
    protected ValidatedInfo fValue = new ValidatedInfo();
    protected short fValidationAttempted = 0;
    protected short fValidity = 0;
    protected StringList fErrorCodes = null;
    protected StringList fErrorMessages = null;
    protected String fValidationContext = null;

    public PSVIAttrNSImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2, String string3) {
        super(coreDocumentImpl, string, string2, string3);
    }

    public PSVIAttrNSImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2) {
        super(coreDocumentImpl, string, string2);
    }

    @Override
    public ItemPSVI constant() {
        return new AttributePSVImpl(true, this);
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

    public void setPSVI(AttributePSVI attributePSVI) {
        this.fDeclaration = attributePSVI.getAttributeDeclaration();
        this.fValidationContext = attributePSVI.getValidationContext();
        this.fValidity = attributePSVI.getValidity();
        this.fValidationAttempted = attributePSVI.getValidationAttempted();
        this.fErrorCodes = attributePSVI.getErrorCodes();
        this.fErrorMessages = attributePSVI.getErrorMessages();
        this.fValue.copyFrom(attributePSVI.getSchemaValue());
        this.fTypeDecl = attributePSVI.getTypeDefinition();
        this.fSpecified = attributePSVI.getIsSchemaSpecified();
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

