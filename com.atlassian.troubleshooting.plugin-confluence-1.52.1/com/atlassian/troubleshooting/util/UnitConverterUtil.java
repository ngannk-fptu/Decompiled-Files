/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.util;

public class UnitConverterUtil {
    private UnitConverterUtil() {
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        long absBytes;
        long calculateBytes = bytes;
        int unit = si ? 1000 : 1024;
        long l = absBytes = calculateBytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(calculateBytes);
        if (absBytes < (long)unit) {
            return calculateBytes + " B";
        }
        int exp = (int)(Math.log(absBytes) / Math.log(unit));
        long th = (long)(Math.pow(unit, exp) * ((double)unit - 0.05));
        if (exp < 6 && absBytes >= th - (long)((th & 0xFFFL) == 3328L ? 52 : 0)) {
            ++exp;
        }
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        if (exp > 4) {
            calculateBytes /= (long)unit;
            --exp;
        }
        return String.format("%.1f %sB", (double)calculateBytes / Math.pow(unit, exp), pre);
    }
}

