/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class NonPositiveIntegerType
extends IntegerType {
    public static final NonPositiveIntegerType theInstance = new NonPositiveIntegerType();
    private static final long serialVersionUID = 1L;

    private NonPositiveIntegerType() {
        super("nonPositiveInteger", NonPositiveIntegerType.createRangeFacet(IntegerType.theInstance, null, IntegerValueType.create("0")));
    }

    public final XSDatatype getBaseType() {
        return IntegerType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        Object o = super._createValue(lexicalValue, context);
        if (o == null) {
            return null;
        }
        IntegerValueType v = (IntegerValueType)o;
        if (!v.isNonPositive()) {
            return null;
        }
        return v;
    }
}

