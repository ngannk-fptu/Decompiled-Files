/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NonPositiveIntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class NegativeIntegerType
extends IntegerType {
    public static final NegativeIntegerType theInstance = new NegativeIntegerType();
    private static final long serialVersionUID = 1L;

    private NegativeIntegerType() {
        super("negativeInteger", NegativeIntegerType.createRangeFacet(NonPositiveIntegerType.theInstance, null, IntegerValueType.create("-1")));
    }

    public final XSDatatype getBaseType() {
        return NonPositiveIntegerType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        Object o = super._createValue(lexicalValue, context);
        if (o == null) {
            return null;
        }
        IntegerValueType v = (IntegerValueType)o;
        if (!v.isNegative()) {
            return null;
        }
        return v;
    }
}

