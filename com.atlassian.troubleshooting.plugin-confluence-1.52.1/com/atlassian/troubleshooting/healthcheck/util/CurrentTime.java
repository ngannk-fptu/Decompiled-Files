/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.util;

public class CurrentTime {
    private static Long fixedTime = null;

    public static long currentTimeMillis() {
        return fixedTime != null ? fixedTime : System.currentTimeMillis();
    }

    public static void setFixedTime(long time) {
        fixedTime = time;
    }

    public static void setCurrentMillisSystem() {
        fixedTime = null;
    }
}

