/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public abstract class DataTypeWithFacet
extends XSDatatypeImpl {
    public final XSDatatypeImpl baseType;
    protected final ConcreteType concreteType;
    public final String facetName;
    public final boolean isFacetFixed;
    private final boolean needValueCheckFlag;
    private static final long serialVersionUID = 1L;

    public final XSDatatype getBaseType() {
        return this.baseType;
    }

    DataTypeWithFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed) throws DatatypeException {
        this(nsUri, typeName, baseType, facetName, _isFixed, baseType.whiteSpace);
    }

    DataTypeWithFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed, WhiteSpaceProcessor whiteSpace) throws DatatypeException {
        super(nsUri, typeName, whiteSpace);
        this.baseType = baseType;
        this.facetName = facetName;
        this.isFacetFixed = _isFixed;
        this.concreteType = baseType.getConcreteType();
        this.needValueCheckFlag = baseType.needValueCheck();
        int r = baseType.isFacetApplicable(facetName);
        switch (r) {
            case 0: {
                return;
            }
            case -2: {
                throw new DatatypeException(DataTypeWithFacet.localize("BadTypeException.NotApplicableFacet", facetName));
            }
            case -1: {
                throw new DatatypeException(DataTypeWithFacet.localize("BadTypeException.OverridingFixedFacet", facetName));
            }
        }
    }

    public boolean isContextDependent() {
        return this.concreteType.isContextDependent();
    }

    public int getIdType() {
        return this.concreteType.getIdType();
    }

    public final String displayName() {
        if (this.getName() != null) {
            return this.getName();
        }
        return this.concreteType.getName() + "-derived";
    }

    public final int isFacetApplicable(String facetName) {
        if (this.facetName.equals(facetName)) {
            if (this.isFacetFixed) {
                return -1;
            }
            return 0;
        }
        return this.baseType.isFacetApplicable(facetName);
    }

    protected boolean needValueCheck() {
        return this.needValueCheckFlag;
    }

    public final DataTypeWithFacet getFacetObject(String facetName) {
        if (this.facetName.equals(facetName)) {
            return this;
        }
        return this.baseType.getFacetObject(facetName);
    }

    public final ConcreteType getConcreteType() {
        return this.concreteType;
    }

    public final int getVariety() {
        return this.concreteType.getVariety();
    }

    public final boolean isFinal(int derivationType) {
        return this.baseType.isFinal(derivationType);
    }

    public final String convertToLexicalValue(Object o, SerializationContext context) {
        return this.concreteType.convertToLexicalValue(o, context);
    }

    public final Class getJavaObjectType() {
        return this.concreteType.getJavaObjectType();
    }

    public final Object _createJavaObject(String literal, ValidationContext context) {
        if (this.isValid(literal, context)) {
            return this.baseType.createJavaObject(literal, context);
        }
        return null;
    }

    public String serializeJavaObject(Object value, SerializationContext context) {
        return this.baseType.serializeJavaObject(value, context);
    }

    protected final void _checkValid(String content, ValidationContext context) throws DatatypeException {
        this.baseType._checkValid(content, context);
        this.diagnoseByFacet(content, context);
    }

    protected abstract void diagnoseByFacet(String var1, ValidationContext var2) throws DatatypeException;
}

