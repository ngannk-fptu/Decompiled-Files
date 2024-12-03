/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

public final class ArgUtil {
    private ArgUtil() {
    }

    public static boolean convertToBoolean(String prop, Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (value instanceof String) {
            String str = (String)value;
            if (str.equalsIgnoreCase("false")) {
                return false;
            }
            if (str.equalsIgnoreCase("true")) {
                return true;
            }
            throw new IllegalArgumentException("Invalid String value for property '" + prop + "': expected Boolean value.");
        }
        throw new IllegalArgumentException("Invalid value type (" + value.getClass() + ") for property '" + prop + "': expected Boolean value.");
    }

    public static int convertToInt(String prop, Object value, int minValue) {
        int i;
        if (value == null) {
            i = 0;
        } else if (value instanceof Number) {
            long l = ((Number)value).longValue();
            i = l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (l < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int)l);
        } else if (value instanceof String) {
            try {
                i = Integer.parseInt((String)value);
            }
            catch (NumberFormatException nex) {
                throw new IllegalArgumentException("Invalid String value for property '" + prop + "': expected a number (Integer).");
            }
        } else {
            throw new IllegalArgumentException("Invalid value type (" + value.getClass() + ") for property '" + prop + "': expected Integer value.");
        }
        if (i < minValue) {
            throw new IllegalArgumentException("Invalid numeric value (" + i + ") for property '" + prop + "': minimum is " + minValue + ".");
        }
        return i;
    }

    public static long convertToLong(String prop, Object value, long minValue) {
        long i;
        if (value == null) {
            i = 0L;
        } else if (value instanceof Number) {
            i = ((Number)value).longValue();
        } else if (value instanceof String) {
            try {
                i = Long.parseLong((String)value);
            }
            catch (NumberFormatException nex) {
                throw new IllegalArgumentException("Invalid String value for property '" + prop + "': expected a number (Long).");
            }
        } else {
            throw new IllegalArgumentException("Invalid value type (" + value.getClass() + ") for property '" + prop + "': expected Long value.");
        }
        if (i < minValue) {
            throw new IllegalArgumentException("Invalid numeric value (" + i + ") for property '" + prop + "': minimum is " + minValue + ".");
        }
        return i;
    }
}

