/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithValueConstraintFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public abstract class RangeFacet
extends DataTypeWithValueConstraintFacet {
    public final Object limitValue;
    private static final long serialVersionUID = 1L;

    protected RangeFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, Object limit, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, facetName, _isFixed);
        this.limitValue = limit;
    }

    public final Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null) {
            return null;
        }
        int r = ((Comparator)((Object)this.concreteType)).compare(this.limitValue, o);
        if (!this.rangeCheck(r)) {
            return null;
        }
        return o;
    }

    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if (this._createValue(content, context) != null) {
            return;
        }
        throw new DatatypeException(-1, RangeFacet.localize("DataTypeErrorDiagnosis.OutOfRange", this.facetName, this.limitValue));
    }

    protected abstract boolean rangeCheck(int var1);
}

