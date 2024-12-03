/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

abstract class DataTypeWithLexicalConstraintFacet
extends DataTypeWithFacet {
    private static final long serialVersionUID = 6093401348890059498L;

    DataTypeWithLexicalConstraintFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, facetName, _isFixed);
    }

    protected final boolean checkFormat(String literal, ValidationContext context) {
        if (!this.baseType.checkFormat(literal, context)) {
            return false;
        }
        return this.checkLexicalConstraint(literal);
    }

    public final Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o != null && !this.checkLexicalConstraint(literal)) {
            return null;
        }
        return o;
    }

    protected abstract boolean checkLexicalConstraint(String var1);
}

