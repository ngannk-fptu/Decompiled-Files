/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MaxInclusiveFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MinInclusiveFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

abstract class IntegerDerivedType
extends BuiltinAtomicType
implements Comparator {
    private final XSDatatypeImpl baseFacets;
    private static final long serialVersionUID = -7353993842821534786L;

    protected IntegerDerivedType(String typeName, XSDatatypeImpl _baseFacets) {
        super(typeName);
        this.baseFacets = _baseFacets;
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("totalDigits") || facetName.equals("pattern") || facetName.equals("whiteSpace") || facetName.equals("enumeration") || facetName.equals("maxInclusive") || facetName.equals("minInclusive") || facetName.equals("maxExclusive") || facetName.equals("minExclusive")) {
            return 0;
        }
        if (facetName.equals("fractionDigits")) {
            return -1;
        }
        return -2;
    }

    public DataTypeWithFacet getFacetObject(String facetName) {
        return this.baseFacets.getFacetObject(facetName);
    }

    protected final boolean checkFormat(String content, ValidationContext context) {
        return this._createValue(content, context) != null;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof Number || value instanceof IntegerValueType) {
            return value.toString();
        }
        throw new IllegalArgumentException("invalid value type:" + value.getClass().toString());
    }

    public final int compare(Object o1, Object o2) {
        int r = ((Comparable)o1).compareTo(o2);
        if (r < 0) {
            return -1;
        }
        if (r > 0) {
            return 1;
        }
        return 0;
    }

    protected static String removeOptionalPlus(String s) {
        if (s.length() <= 1 || s.charAt(0) != '+') {
            return s;
        }
        char ch = (s = s.substring(1)).charAt(0);
        if ('0' <= ch && ch <= '9') {
            return s;
        }
        if ('.' == ch) {
            return s;
        }
        throw new NumberFormatException();
    }

    protected static XSDatatypeImpl createRangeFacet(XSDatatypeImpl baseType, Number min, Number max) {
        try {
            XSDatatypeImpl r = baseType;
            if (min != null) {
                r = new MinInclusiveFacet(null, null, r, min, false);
            }
            if (max != null) {
                r = new MaxInclusiveFacet(null, null, r, max, false);
            }
            return r;
        }
        catch (DatatypeException e) {
            throw new InternalError();
        }
    }
}

