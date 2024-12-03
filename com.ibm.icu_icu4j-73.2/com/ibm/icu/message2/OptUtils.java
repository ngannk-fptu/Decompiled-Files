/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import java.util.Map;

class OptUtils {
    private OptUtils() {
    }

    static Number asNumber(Object value) {
        if (value instanceof Number) {
            return (Number)value;
        }
        if (value instanceof CharSequence) {
            String strValue = value.toString();
            try {
                return Double.parseDouble(strValue);
            }
            catch (NumberFormatException numberFormatException) {
                try {
                    return Integer.decode(strValue);
                }
                catch (NumberFormatException numberFormatException2) {
                    // empty catch block
                }
            }
        }
        return null;
    }

    static Integer getInteger(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value == null) {
            return null;
        }
        Number nrValue = OptUtils.asNumber(value);
        if (nrValue != null) {
            return nrValue.intValue();
        }
        return null;
    }

    static String getString(Map<String, Object> options, String key) {
        Object value = options.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence) {
            return value.toString();
        }
        return null;
    }
}

