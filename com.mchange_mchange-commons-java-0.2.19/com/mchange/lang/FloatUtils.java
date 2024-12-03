/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.IntegerUtils;

public final class FloatUtils {
    static final boolean DEBUG = true;
    private static FParser fParser;

    public static byte[] byteArrayFromFloat(float f) {
        int n = Float.floatToIntBits(f);
        return IntegerUtils.byteArrayFromInt(n);
    }

    public static float floatFromByteArray(byte[] byArray, int n) {
        int n2 = IntegerUtils.intFromByteArray(byArray, n);
        return Float.intBitsToFloat(n2);
    }

    public static float parseFloat(String string, float f) {
        if (string == null) {
            return f;
        }
        try {
            return fParser.parseFloat(string);
        }
        catch (NumberFormatException numberFormatException) {
            return f;
        }
    }

    public static float parseFloat(String string) throws NumberFormatException {
        return fParser.parseFloat(string);
    }

    public static String floatToString(float f, int n) {
        boolean bl = f < 0.0f;
        f = bl ? -f : f;
        long l = Math.round((double)f * Math.pow(10.0, -n));
        String string = String.valueOf(l);
        if (l == 0L) {
            return string;
        }
        int n2 = string.length();
        int n3 = n2 + n;
        StringBuffer stringBuffer = new StringBuffer(32);
        if (bl) {
            stringBuffer.append('-');
        }
        if (n3 <= 0) {
            stringBuffer.append("0.");
            for (int i = 0; i < -n3; ++i) {
                stringBuffer.append('0');
            }
            stringBuffer.append(string);
        } else {
            stringBuffer.append(string.substring(0, Math.min(n3, n2)));
            if (n3 < n2) {
                stringBuffer.append('.');
                stringBuffer.append(string.substring(n3));
            } else if (n3 > n2) {
                int n4 = n3 - n2;
                for (int i = 0; i < n4; ++i) {
                    stringBuffer.append('0');
                }
            }
        }
        return stringBuffer.toString();
    }

    static {
        try {
            fParser = new J12FParser();
            fParser.parseFloat("0.1");
        }
        catch (NoSuchMethodError noSuchMethodError) {
            System.err.println("com.mchange.lang.FloatUtils: reconfiguring for Java 1.1 environment");
            fParser = new J11FParser();
        }
    }

    static class J11FParser
    implements FParser {
        J11FParser() {
        }

        @Override
        public float parseFloat(String string) throws NumberFormatException {
            return new Float(string).floatValue();
        }
    }

    static class J12FParser
    implements FParser {
        J12FParser() {
        }

        @Override
        public float parseFloat(String string) throws NumberFormatException {
            return Float.parseFloat(string);
        }
    }

    static interface FParser {
        public float parseFloat(String var1) throws NumberFormatException;
    }
}

