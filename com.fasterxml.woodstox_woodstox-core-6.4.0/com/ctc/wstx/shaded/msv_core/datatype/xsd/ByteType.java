/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ShortType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class ByteType
extends IntegerDerivedType {
    public static final ByteType theInstance = new ByteType();
    private static final long serialVersionUID = 1L;

    private ByteType() {
        super("byte", ByteType.createRangeFacet(ShortType.theInstance, new Byte(-128), new Byte(127)));
    }

    public final XSDatatype getBaseType() {
        return ShortType.theInstance;
    }

    public Object _createValue(String content, ValidationContext context) {
        return ByteType.load(content);
    }

    public static Byte load(String s) {
        try {
            return new Byte(ByteType.removeOptionalPlus(s));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Byte v) {
        return v.toString();
    }

    public Class getJavaObjectType() {
        return Byte.class;
    }
}

