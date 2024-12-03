/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

abstract class DataTypeWithValueConstraintFacet
extends DataTypeWithFacet {
    private static final long serialVersionUID = 2497055158497151572L;

    DataTypeWithValueConstraintFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, facetName, _isFixed);
    }

    protected final boolean needValueCheck() {
        return true;
    }

    protected final boolean checkFormat(String literal, ValidationContext context) {
        return this._createValue(literal, context) != null;
    }
}

