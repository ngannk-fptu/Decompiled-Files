/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.DatatypeException;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

public class XSSimpleTypeDelegate
implements XSSimpleType {
    protected final XSSimpleType type;

    public XSSimpleTypeDelegate(XSSimpleType xSSimpleType) {
        if (xSSimpleType == null) {
            throw new NullPointerException();
        }
        this.type = xSSimpleType;
    }

    public XSSimpleType getWrappedXSSimpleType() {
        return this.type;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.type.getAnnotations();
    }

    @Override
    public boolean getBounded() {
        return this.type.getBounded();
    }

    @Override
    public short getBuiltInKind() {
        return this.type.getBuiltInKind();
    }

    @Override
    public short getDefinedFacets() {
        return this.type.getDefinedFacets();
    }

    @Override
    public XSObjectList getFacets() {
        return this.type.getFacets();
    }

    @Override
    public XSObject getFacet(int n) {
        return this.type.getFacet(n);
    }

    @Override
    public boolean getFinite() {
        return this.type.getFinite();
    }

    @Override
    public short getFixedFacets() {
        return this.type.getFixedFacets();
    }

    @Override
    public XSSimpleTypeDefinition getItemType() {
        return this.type.getItemType();
    }

    @Override
    public StringList getLexicalEnumeration() {
        return this.type.getLexicalEnumeration();
    }

    @Override
    public String getLexicalFacetValue(short s) {
        return this.type.getLexicalFacetValue(s);
    }

    @Override
    public StringList getLexicalPattern() {
        return this.type.getLexicalPattern();
    }

    @Override
    public XSObjectList getMemberTypes() {
        return this.type.getMemberTypes();
    }

    @Override
    public XSObjectList getMultiValueFacets() {
        return this.type.getMultiValueFacets();
    }

    @Override
    public boolean getNumeric() {
        return this.type.getNumeric();
    }

    @Override
    public short getOrdered() {
        return this.type.getOrdered();
    }

    @Override
    public XSSimpleTypeDefinition getPrimitiveType() {
        return this.type.getPrimitiveType();
    }

    @Override
    public short getVariety() {
        return this.type.getVariety();
    }

    @Override
    public boolean isDefinedFacet(short s) {
        return this.type.isDefinedFacet(s);
    }

    @Override
    public boolean isFixedFacet(short s) {
        return this.type.isFixedFacet(s);
    }

    @Override
    public boolean derivedFrom(String string, String string2, short s) {
        return this.type.derivedFrom(string, string2, s);
    }

    @Override
    public boolean derivedFromType(XSTypeDefinition xSTypeDefinition, short s) {
        return this.type.derivedFromType(xSTypeDefinition, s);
    }

    @Override
    public boolean getAnonymous() {
        return this.type.getAnonymous();
    }

    @Override
    public XSTypeDefinition getBaseType() {
        return this.type.getBaseType();
    }

    @Override
    public short getFinal() {
        return this.type.getFinal();
    }

    @Override
    public short getTypeCategory() {
        return this.type.getTypeCategory();
    }

    @Override
    public boolean isFinal(short s) {
        return this.type.isFinal(s);
    }

    @Override
    public String getName() {
        return this.type.getName();
    }

    @Override
    public String getNamespace() {
        return this.type.getNamespace();
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.type.getNamespaceItem();
    }

    @Override
    public short getType() {
        return this.type.getType();
    }

    @Override
    public void applyFacets(XSFacets xSFacets, short s, short s2, ValidationContext validationContext) throws InvalidDatatypeFacetException {
        this.type.applyFacets(xSFacets, s, s2, validationContext);
    }

    @Override
    public short getPrimitiveKind() {
        return this.type.getPrimitiveKind();
    }

    @Override
    public short getWhitespace() throws DatatypeException {
        return this.type.getWhitespace();
    }

    @Override
    public boolean isEqual(Object object, Object object2) {
        return this.type.isEqual(object, object2);
    }

    @Override
    public boolean isIDType() {
        return this.type.isIDType();
    }

    @Override
    public void validate(ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        this.type.validate(validationContext, validatedInfo);
    }

    @Override
    public Object validate(String string, ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.type.validate(string, validationContext, validatedInfo);
    }

    @Override
    public Object validate(Object object, ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.type.validate(object, validationContext, validatedInfo);
    }

    public String toString() {
        return this.type.toString();
    }
}

