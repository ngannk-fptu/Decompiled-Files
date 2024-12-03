/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NonNegativeIntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class UnsignedLongType
extends IntegerType {
    public static final UnsignedLongType theInstance = new UnsignedLongType();
    private static final IntegerValueType upperBound = IntegerValueType.create("18446744073709551615");
    private static final long serialVersionUID = 1L;

    private UnsignedLongType() {
        super("unsignedLong", UnsignedLongType.createRangeFacet(NonNegativeIntegerType.theInstance, null, IntegerValueType.create("18446744073709551615")));
    }

    public final XSDatatype getBaseType() {
        return NonNegativeIntegerType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        IntegerValueType v = IntegerValueType.create(lexicalValue);
        if (v == null) {
            return null;
        }
        if (!v.isNonNegative()) {
            return null;
        }
        if (upperBound.compareTo(v) < 0) {
            return null;
        }
        return v;
    }
}

