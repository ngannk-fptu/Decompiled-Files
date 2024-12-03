/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class ShortType
extends IntegerDerivedType {
    public static final ShortType theInstance = new ShortType("short", ShortType.createRangeFacet(IntType.theInstance, new Short(Short.MIN_VALUE), new Short(Short.MAX_VALUE)));
    private static final long serialVersionUID = 1L;

    protected ShortType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    public XSDatatype getBaseType() {
        return IntType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return ShortType.load(lexicalValue);
    }

    public static Short load(String s) {
        try {
            return new Short(ShortType.removeOptionalPlus(s));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Short v) {
        return v.toString();
    }

    public Class getJavaObjectType() {
        return Short.class;
    }
}

