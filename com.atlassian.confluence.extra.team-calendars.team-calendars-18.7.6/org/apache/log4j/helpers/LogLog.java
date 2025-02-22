/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.helpers;

public class LogLog {
    public static final String DEBUG_KEY = "log4j.debug";
    public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";
    protected static boolean debugEnabled = false;
    private static boolean quietMode = false;
    private static final String PREFIX = "log4j: ";
    private static final String ERR_PREFIX = "log4j:ERROR ";
    private static final String WARN_PREFIX = "log4j:WARN ";

    public static void setInternalDebugging(boolean enabled) {
        debugEnabled = enabled;
    }

    public static void debug(String msg) {
        if (debugEnabled && !quietMode) {
            System.out.println(PREFIX + msg);
        }
    }

    public static void debug(String msg, Throwable t) {
        if (debugEnabled && !quietMode) {
            System.out.println(PREFIX + msg);
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

    public static void error(String msg) {
        if (quietMode) {
            return;
        }
        System.err.println(ERR_PREFIX + msg);
    }

    public static void error(String msg, Throwable t) {
        if (quietMode) {
            return;
        }
        System.err.println(ERR_PREFIX + msg);
        if (t != null) {
            t.printStackTrace();
        }
    }

    public static void setQuietMode(boolean quietMode) {
        LogLog.quietMode = quietMode;
    }

    public static void warn(String msg) {
        if (quietMode) {
            return;
        }
        System.err.println(WARN_PREFIX + msg);
    }

    public static void warn(String msg, Throwable t) {
        if (quietMode) {
            return;
        }
        System.err.println(WARN_PREFIX + msg);
        if (t != null) {
            t.printStackTrace();
        }
    }
}

