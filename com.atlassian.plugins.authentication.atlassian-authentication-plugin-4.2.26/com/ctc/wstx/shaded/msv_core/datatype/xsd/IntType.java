/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.LongType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class IntType
extends IntegerDerivedType {
    public static final IntType theInstance = new IntType("int", IntType.createRangeFacet(LongType.theInstance, new Integer(Integer.MIN_VALUE), new Integer(Integer.MAX_VALUE)));
    private static final long serialVersionUID = 1L;

    protected IntType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    public XSDatatype getBaseType() {
        return LongType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return IntType.load(lexicalValue);
    }

    public static Integer load(String s) {
        try {
            return new Integer(IntType.removeOptionalPlus(s));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Integer v) {
        return v.toString();
    }

    public Class getJavaObjectType() {
        return Integer.class;
    }
}

