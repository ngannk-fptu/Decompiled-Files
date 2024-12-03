/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FloatingNumberType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class DoubleType
extends FloatingNumberType {
    public static final DoubleType theInstance = new DoubleType();
    private static final long serialVersionUID = 1L;

    private DoubleType() {
        super("double");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return DoubleType.load(lexicalValue);
    }

    public static Double load(String lexicalValue) {
        try {
            if (lexicalValue.equals("NaN")) {
                return new Double(Double.NaN);
            }
            if (lexicalValue.equals("INF")) {
                return new Double(Double.POSITIVE_INFINITY);
            }
            if (lexicalValue.equals("-INF")) {
                return new Double(Double.NEGATIVE_INFINITY);
            }
            if (lexicalValue.length() == 0 || !DoubleType.isDigitOrPeriodOrSign(lexicalValue.charAt(0)) || !DoubleType.isDigitOrPeriodOrSign(lexicalValue.charAt(lexicalValue.length() - 1))) {
                return null;
            }
            return Double.valueOf(lexicalValue);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof Double)) {
            throw new IllegalArgumentException();
        }
        return DoubleType.save((Double)value);
    }

    public static String save(Double value) {
        double v = value;
        if (Double.isNaN(v)) {
            return "NaN";
        }
        if (v == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return value.toString();
    }

    public Class getJavaObjectType() {
        return Double.class;
    }
}

