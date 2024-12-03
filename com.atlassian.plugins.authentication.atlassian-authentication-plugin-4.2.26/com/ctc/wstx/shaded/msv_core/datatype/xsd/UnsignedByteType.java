/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ShortType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnsignedShortType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class UnsignedByteType
extends ShortType {
    public static final UnsignedByteType theInstance = new UnsignedByteType();
    private static final short upperBound = 255;
    private static final long serialVersionUID = 1L;

    private UnsignedByteType() {
        super("unsignedByte", UnsignedByteType.createRangeFacet(UnsignedShortType.theInstance, null, new Short(255)));
    }

    public final XSDatatype getBaseType() {
        return UnsignedShortType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        try {
            Short v = (Short)super._createValue(lexicalValue, context);
            if (v == null) {
                return null;
            }
            if (v < 0) {
                return null;
            }
            if (v > 255) {
                return null;
            }
            return v;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

