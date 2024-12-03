/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnsignedIntType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class UnsignedShortType
extends IntType {
    public static final UnsignedShortType theInstance = new UnsignedShortType();
    private static final int upperBound = 65535;
    private static final long serialVersionUID = 1L;

    private UnsignedShortType() {
        super("unsignedShort", UnsignedShortType.createRangeFacet(UnsignedIntType.theInstance, null, new Integer(65535)));
    }

    public XSDatatype getBaseType() {
        return UnsignedIntType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        try {
            Integer v = (Integer)super._createValue(lexicalValue, context);
            if (v == null) {
                return null;
            }
            if (v < 0) {
                return null;
            }
            if (v > 65535) {
                return null;
            }
            return v;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

