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

public class MaxLengthFacet
extends DataTypeWithValueConstraintFacet {
    public final int maxLength;
    private static final long serialVersionUID = 1L;

    protected MaxLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, facets.getNonNegativeInteger("maxLength"), facets.isFixed("maxLength"));
    }

    protected MaxLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _maxLength, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "maxLength", _isFixed);
        this.maxLength = _maxLength;
        DataTypeWithFacet o = baseType.getFacetObject("maxLength");
        if (o != null && ((MaxLengthFacet)o).maxLength < this.maxLength) {
            throw new DatatypeException(MaxLengthFacet.localize("LoosenedFacet", "maxLength", o.displayName()));
        }
    }

    public Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null || ((Discrete)((Object)this.concreteType)).countLength(o) > this.maxLength) {
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
        if (cnt > this.maxLength) {
            throw new DatatypeException(-1, MaxLengthFacet.localize("DataTypeErrorDiagnosis.MaxLength", new Integer(cnt), new Integer(this.maxLength)));
        }
    }
}

