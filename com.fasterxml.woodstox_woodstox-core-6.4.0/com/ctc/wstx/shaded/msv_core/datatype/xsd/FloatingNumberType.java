/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;

abstract class FloatingNumberType
extends BuiltinAtomicType
implements Comparator {
    private static final long serialVersionUID = -224134863141700384L;

    protected FloatingNumberType(String typeName) {
        super(typeName);
    }

    protected final boolean checkFormat(String lexicalValue, ValidationContext context) {
        return this._createValue(lexicalValue, context) != null;
    }

    protected static boolean isDigitOrPeriodOrSign(char ch) {
        if ('0' <= ch && ch <= '9') {
            return true;
        }
        return ch == '+' || ch == '-' || ch == '.';
    }

    public final int compare(Object lhs, Object rhs) {
        int r = ((Comparable)lhs).compareTo(rhs);
        if (r < 0) {
            return -1;
        }
        if (r > 0) {
            return 1;
        }
        return 0;
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("pattern") || facetName.equals("enumeration") || facetName.equals("whiteSpace") || facetName.equals("maxInclusive") || facetName.equals("minInclusive") || facetName.equals("maxExclusive") || facetName.equals("minExclusive")) {
            return 0;
        }
        return -2;
    }
}

