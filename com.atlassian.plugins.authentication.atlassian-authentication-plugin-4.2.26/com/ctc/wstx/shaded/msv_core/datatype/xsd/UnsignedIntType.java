/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.LongType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnsignedLongType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class UnsignedIntType
extends LongType {
    public static final UnsignedIntType theInstance = new UnsignedIntType();
    private static final long upperBound = 0xFFFFFFFFL;
    private static final long serialVersionUID = 1L;

    private UnsignedIntType() {
        super("unsignedInt", UnsignedIntType.createRangeFacet(UnsignedLongType.theInstance, null, new Long(0xFFFFFFFFL)));
    }

    public final XSDatatype getBaseType() {
        return UnsignedLongType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        try {
            Long v = (Long)super._createValue(lexicalValue, context);
            if (v == null) {
                return null;
            }
            if (v < 0L) {
                return null;
            }
            if (v > 0xFFFFFFFFL) {
                return null;
            }
            return v;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

