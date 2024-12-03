/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FractionDigitsFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NumberType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import java.math.BigInteger;

public class IntegerType
extends IntegerDerivedType {
    public static final IntegerType theInstance;
    private static final long serialVersionUID = 1L;

    protected IntegerType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    public XSDatatype getBaseType() {
        return NumberType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return IntegerValueType.create(lexicalValue);
    }

    public Object _createJavaObject(String literal, ValidationContext context) {
        IntegerValueType o = (IntegerValueType)this._createValue(literal, context);
        if (o == null) {
            return null;
        }
        return new BigInteger(o.toString());
    }

    public static BigInteger load(String s) {
        IntegerValueType o = IntegerValueType.create(s);
        if (o == null) {
            return null;
        }
        return new BigInteger(o.toString());
    }

    public static String save(BigInteger v) {
        return v.toString();
    }

    public Class getJavaObjectType() {
        return BigInteger.class;
    }

    static {
        try {
            theInstance = new IntegerType("integer", new FractionDigitsFacet(null, null, (XSDatatypeImpl)NumberType.theInstance, 0, true));
        }
        catch (DatatypeException e) {
            throw new InternalError();
        }
    }
}

