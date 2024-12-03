/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import org.codehaus.jettison.mapped.TypeConverter;

public class DefaultConverter
implements TypeConverter {
    private static final String ENFORCE_32BIT_INTEGER_KEY = "jettison.mapped.typeconverter.enforce_32bit_integer";
    public static final boolean ENFORCE_32BIT_INTEGER = Boolean.getBoolean("jettison.mapped.typeconverter.enforce_32bit_integer");
    private boolean enforce32BitInt = ENFORCE_32BIT_INTEGER;
    private static final int MAX_LENGTH_LONG = String.valueOf(Long.MAX_VALUE).length();
    private static final int MAX_LENGTH_LONG_NEGATIVE = String.valueOf(Long.MAX_VALUE).length() + 1;
    private static final int MAX_LENGTH_INTEGER = String.valueOf(Integer.MAX_VALUE).length();
    private static final int MAX_LENGTH_INTEGER_NEGATIVE = String.valueOf(Integer.MAX_VALUE).length() + 1;

    public void setEnforce32BitInt(boolean enforce32BitInt) {
        this.enforce32BitInt = enforce32BitInt;
    }

    @Override
    public Object convertToJSONPrimitive(String text) {
        Double dbl;
        char first;
        if (text == null) {
            return text;
        }
        if (text.length() >= 1 && ((first = text.charAt(0)) < '0' || first > '9') && first != '-') {
            if (first == 't') {
                if (text.equals("true")) {
                    return Boolean.TRUE;
                }
            } else if (first == 'f' && text.equals("false")) {
                return Boolean.FALSE;
            }
            return text;
        }
        Object primitive = null;
        primitive = this.enforce32BitInt ? (Number)DefaultConverter.getInteger(text) : (Number)DefaultConverter.getLong(text);
        if (primitive == null && (dbl = DefaultConverter.getDouble(text)) != null) {
            primitive = dbl.isInfinite() || dbl.isNaN() ? text : dbl;
        }
        if (primitive == null || !primitive.toString().equals(text)) {
            primitive = text;
        }
        return primitive;
    }

    private static Long getLong(String text) {
        if (text.isEmpty()) {
            return null;
        }
        if (text.charAt(0) == '-' ? text.length() > MAX_LENGTH_LONG_NEGATIVE : text.length() > MAX_LENGTH_LONG) {
            return null;
        }
        int i = 0;
        if (text.charAt(0) == '-') {
            if (text.length() > 1) {
                ++i;
            } else {
                return null;
            }
        }
        while (i < text.length()) {
            if (!Character.isDigit(text.charAt(i))) {
                return null;
            }
            ++i;
        }
        try {
            return Long.parseLong(text);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static Integer getInteger(String text) {
        if (text.isEmpty()) {
            return null;
        }
        if (text.charAt(0) == '-' ? text.length() > MAX_LENGTH_INTEGER_NEGATIVE : text.length() > MAX_LENGTH_INTEGER) {
            return null;
        }
        int i = 0;
        if (text.charAt(0) == '-') {
            if (text.length() > 1) {
                ++i;
            } else {
                return null;
            }
        }
        while (i < text.length()) {
            if (!Character.isDigit(text.charAt(i))) {
                return null;
            }
            ++i;
        }
        try {
            return Integer.parseInt(text);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static Double getDouble(String text) {
        boolean foundDP = false;
        boolean foundExp = false;
        if (text.isEmpty()) {
            return null;
        }
        int i = 0;
        if (text.charAt(0) == '-') {
            if (text.length() > 1) {
                ++i;
            } else {
                return null;
            }
        }
        while (i < text.length()) {
            char next = text.charAt(i);
            if (!Character.isDigit(next)) {
                if (next == '.') {
                    if (foundDP) {
                        return null;
                    }
                    foundDP = true;
                } else if (next == 'E' || next == 'e') {
                    if (foundExp) {
                        return null;
                    }
                    foundExp = true;
                } else {
                    return null;
                }
            }
            ++i;
        }
        try {
            return Double.parseDouble(text);
        }
        catch (Exception e) {
            return null;
        }
    }
}

