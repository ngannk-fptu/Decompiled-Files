/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NonNegativeIntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class PositiveIntegerType
extends IntegerType {
    public static final PositiveIntegerType theInstance = new PositiveIntegerType();
    private static final long serialVersionUID = 1L;

    private PositiveIntegerType() {
        super("positiveInteger", PositiveIntegerType.createRangeFacet(NonNegativeIntegerType.theInstance, IntegerValueType.create("1"), null));
    }

    public final XSDatatype getBaseType() {
        return NonNegativeIntegerType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        Object o = super._createValue(lexicalValue, context);
        if (o == null) {
            return null;
        }
        IntegerValueType v = (IntegerValueType)o;
        if (!v.isPositive()) {
            return null;
        }
        return v;
    }
}

