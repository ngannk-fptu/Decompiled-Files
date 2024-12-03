/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io.impl;

public final class NumberParser {
    private NumberParser() {
    }

    public static Long parseLong(String str) {
        if (null != str) {
            try {
                return new Long(Long.parseLong(str.trim()));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static Integer parseInt(String str) {
        if (null != str) {
            try {
                return new Integer(Integer.parseInt(str.trim()));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static Float parseFloat(String str) {
        if (null != str) {
            try {
                return new Float(Float.parseFloat(str.trim()));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static float parseFloat(String str, float def) {
        Float result = NumberParser.parseFloat(str);
        if (result == null) {
            return def;
        }
        return result.floatValue();
    }

    public static long parseLong(String str, long def) {
        Long ret = NumberParser.parseLong(str);
        if (ret == null) {
            return def;
        }
        return ret;
    }
}

