/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FloatingNumberType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class FloatType
extends FloatingNumberType {
    public static final FloatType theInstance = new FloatType();
    private static final long serialVersionUID = 1L;

    private FloatType() {
        super("float");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return FloatType.load(lexicalValue);
    }

    public static Float load(String s) {
        try {
            if (s.equals("NaN")) {
                return new Float(Float.NaN);
            }
            if (s.equals("INF")) {
                return new Float(Float.POSITIVE_INFINITY);
            }
            if (s.equals("-INF")) {
                return new Float(Float.NEGATIVE_INFINITY);
            }
            if (s.length() == 0 || !FloatType.isDigitOrPeriodOrSign(s.charAt(0)) || !FloatType.isDigitOrPeriodOrSign(s.charAt(s.length() - 1))) {
                return null;
            }
            return Float.valueOf(s);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public Class getJavaObjectType() {
        return Float.class;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof Float)) {
            throw new IllegalArgumentException();
        }
        return FloatType.save((Float)value);
    }

    public static String save(Float value) {
        float v = value.floatValue();
        if (Float.isNaN(v)) {
            return "NaN";
        }
        if (v == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Float.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return value.toString();
    }
}

