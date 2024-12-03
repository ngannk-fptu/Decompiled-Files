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

public class LengthFacet
extends DataTypeWithValueConstraintFacet {
    public final int length;
    private static final long serialVersionUID = 1L;

    protected LengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, facets.getNonNegativeInteger("length"), facets.isFixed("length"));
    }

    protected LengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _length, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "length", _isFixed);
        this.length = _length;
        DataTypeWithFacet o = baseType.getFacetObject("length");
        if (o != null && ((LengthFacet)o).length != this.length) {
            throw new DatatypeException(LengthFacet.localize("LoosenedFacet", "length", o.displayName()));
        }
    }

    public Object _createValue(String content, ValidationContext context) {
        Object o = this.baseType._createValue(content, context);
        if (o == null || ((Discrete)((Object)this.concreteType)).countLength(o) != this.length) {
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
        if (cnt != this.length) {
            throw new DatatypeException(-1, LengthFacet.localize("DataTypeErrorDiagnosis.Length", new Integer(cnt), new Integer(this.length)));
        }
    }
}

