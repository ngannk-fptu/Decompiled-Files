/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class LongType
extends IntegerDerivedType {
    public static final LongType theInstance = new LongType();
    private static final long serialVersionUID = 1L;

    private LongType() {
        super("long", LongType.createRangeFacet(IntegerType.theInstance, new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE)));
    }

    protected LongType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    public XSDatatype getBaseType() {
        return IntegerType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return LongType.load(lexicalValue);
    }

    public static Long load(String s) {
        try {
            return new Long(LongType.removeOptionalPlus(s));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Long v) {
        return v.toString();
    }

    public Class getJavaObjectType() {
        return Long.class;
    }
}

