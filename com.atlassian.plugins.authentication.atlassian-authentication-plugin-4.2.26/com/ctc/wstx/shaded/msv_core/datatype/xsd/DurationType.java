/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigTimeDurationValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.ITimeDurationValueType;

public final class DurationType
extends BuiltinAtomicType
implements Comparator {
    public static final DurationType theInstance = new DurationType();
    private static final long serialVersionUID = 1L;

    private DurationType() {
        super("duration");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    protected boolean checkFormat(String content, ValidationContext context) {
        try {
            new BigTimeDurationValueType(content);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Object _createValue(String content, ValidationContext context) {
        try {
            return new BigTimeDurationValueType(content);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Class getJavaObjectType() {
        return ITimeDurationValueType.class;
    }

    public int compare(Object lhs, Object rhs) {
        return ((ITimeDurationValueType)lhs).compare((ITimeDurationValueType)rhs);
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("pattern") || facetName.equals("enumeration") || facetName.equals("whiteSpace") || facetName.equals("maxInclusive") || facetName.equals("maxExclusive") || facetName.equals("minInclusive") || facetName.equals("minExclusive")) {
            return 0;
        }
        return -2;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof ITimeDurationValueType)) {
            throw new IllegalArgumentException();
        }
        return value.toString();
    }
}

