/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithLexicalConstraintFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class TotalDigitsFacet
extends DataTypeWithLexicalConstraintFacet {
    public final int precision;
    private static final long serialVersionUID = 1L;

    public TotalDigitsFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _precision, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "totalDigits", _isFixed);
        this.precision = _precision;
        DataTypeWithFacet o = baseType.getFacetObject("totalDigits");
        if (o != null && ((TotalDigitsFacet)o).precision < this.precision) {
            throw new DatatypeException(TotalDigitsFacet.localize("LoosenedFacet", "totalDigits", o.displayName()));
        }
    }

    protected boolean checkLexicalConstraint(String content) {
        return TotalDigitsFacet.countPrecision(content) <= this.precision;
    }

    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        int cnt = TotalDigitsFacet.countPrecision(content);
        if (cnt <= this.precision) {
            return;
        }
        throw new DatatypeException(-1, TotalDigitsFacet.localize("DataTypeErrorDiagnosis.TooMuchPrecision", new Integer(cnt), new Integer(this.precision)));
    }

    protected static int countPrecision(String literal) {
        int len = literal.length();
        boolean skipMode = true;
        boolean seenDot = false;
        int count = 0;
        int trailingZero = 0;
        for (int i = 0; i < len; ++i) {
            char ch = literal.charAt(i);
            if (ch == '.') {
                skipMode = false;
                seenDot = true;
            }
            if (skipMode) {
                if ('1' > ch || ch > '9') continue;
                ++count;
                skipMode = false;
                continue;
            }
            trailingZero = seenDot && ch == '0' ? ++trailingZero : 0;
            if ('0' > ch || ch > '9') continue;
            ++count;
        }
        return count - trailingZero;
    }
}

