/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class Proxy
extends XSDatatypeImpl {
    public final XSDatatypeImpl baseType;
    private static final long serialVersionUID = 1L;

    public final XSDatatype getBaseType() {
        return this.baseType;
    }

    public Proxy(String nsUri, String newTypeName, XSDatatypeImpl baseType) {
        super(nsUri, newTypeName, baseType.whiteSpace);
        this.baseType = baseType;
    }

    public boolean isContextDependent() {
        return this.baseType.isContextDependent();
    }

    public int getIdType() {
        return this.baseType.getIdType();
    }

    public boolean isFinal(int derivationType) {
        return this.baseType.isFinal(derivationType);
    }

    public ConcreteType getConcreteType() {
        return this.baseType.getConcreteType();
    }

    public String displayName() {
        return this.baseType.displayName();
    }

    public int getVariety() {
        return this.baseType.getVariety();
    }

    public int isFacetApplicable(String facetName) {
        return this.baseType.isFacetApplicable(facetName);
    }

    public boolean checkFormat(String content, ValidationContext context) {
        return this.baseType.checkFormat(content, context);
    }

    public Object _createValue(String content, ValidationContext context) {
        return this.baseType._createValue(content, context);
    }

    public DataTypeWithFacet getFacetObject(String facetName) {
        return this.baseType.getFacetObject(facetName);
    }

    public Class getJavaObjectType() {
        return this.baseType.getJavaObjectType();
    }

    public Object _createJavaObject(String literal, ValidationContext context) {
        return this.baseType._createJavaObject(literal, context);
    }

    public String serializeJavaObject(Object value, SerializationContext context) {
        return this.baseType.serializeJavaObject(value, context);
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        return this.baseType.convertToLexicalValue(value, context);
    }

    public void _checkValid(String content, ValidationContext context) throws DatatypeException {
        this.baseType._checkValid(content, context);
    }
}

