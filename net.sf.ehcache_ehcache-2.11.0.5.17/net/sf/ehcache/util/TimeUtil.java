/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

public class TimeUtil {
    static final long ONE_SECOND = 1000L;

    public static int toSecs(long timeInMillis) {
        return (int)Math.ceil((double)timeInMillis / 1000.0);
    }

    public static long toMillis(int timeInSecs) {
        return (long)timeInSecs * 1000L;
    }

    public static int convertTimeToInt(long seconds) {
        if (seconds > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)seconds;
    }
}

