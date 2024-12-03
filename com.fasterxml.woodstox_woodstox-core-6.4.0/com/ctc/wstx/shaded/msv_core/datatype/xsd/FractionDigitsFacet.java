/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithLexicalConstraintFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class FractionDigitsFacet
extends DataTypeWithLexicalConstraintFacet {
    public final int scale;
    private static final long serialVersionUID = 1L;

    public FractionDigitsFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _scale, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "fractionDigits", _isFixed);
        this.scale = _scale;
        DataTypeWithFacet o = baseType.getFacetObject("fractionDigits");
        if (o != null && ((FractionDigitsFacet)o).scale < this.scale) {
            throw new DatatypeException(FractionDigitsFacet.localize("LoosenedFacet", "fractionDigits", o.displayName()));
        }
    }

    protected boolean checkLexicalConstraint(String content) {
        return FractionDigitsFacet.countScale(content) <= this.scale;
    }

    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        int cnt = FractionDigitsFacet.countScale(content);
        if (cnt <= this.scale) {
            return;
        }
        throw new DatatypeException(-1, FractionDigitsFacet.localize("DataTypeErrorDiagnosis.TooMuchScale", new Integer(cnt), new Integer(this.scale)));
    }

    protected static final int countScale(String literal) {
        int len = literal.length();
        boolean skipMode = true;
        int count = 0;
        int trailingZero = 0;
        for (int i = 0; i < len; ++i) {
            char ch = literal.charAt(i);
            if (skipMode) {
                if (ch != '.') continue;
                skipMode = false;
                continue;
            }
            trailingZero = ch == '0' ? ++trailingZero : 0;
            if ('0' > ch || ch > '9') continue;
            ++count;
        }
        return count - trailingZero;
    }
}

