/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithValueConstraintFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class MinLengthFacet
extends DataTypeWithValueConstraintFacet {
    public final int minLength;
    private static final long serialVersionUID = 1L;

    protected MinLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, facets.getNonNegativeInteger("minLength"), facets.isFixed("minLength"));
    }

    protected MinLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _minLength, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "minLength", _isFixed);
        this.minLength = _minLength;
        DataTypeWithFacet o = baseType.getFacetObject("minLength");
        if (o != null && ((MinLengthFacet)o).minLength > this.minLength) {
            throw new DatatypeException(MinLengthFacet.localize("LoosenedFacet", "minLength", o.displayName()));
        }
    }

    public Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null || ((Discrete)((Object)this.concreteType)).countLength(o) < this.minLength) {
            return null;
        }
        return o;
    }

    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        Object o = this.concreteType._createValue(content, context);
        if (o == null) {
            throw new IllegalStateException();
        }
        int cnt = ((Discrete)((Object)this.concreteType)).countLength(o);
        if (cnt < this.minLength) {
            throw new DatatypeException(-1, MinLengthFacet.localize("DataTypeErrorDiagnosis.MinLength", new Integer(cnt), new Integer(this.minLength)));
        }
    }
}

