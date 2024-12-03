/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.jmx;

import java.util.Date;

public class CurrentTimeFacade {
    private static Date currentTime;

    public static Date getCurrentTime() {
        return currentTime == null ? new Date() : new Date(currentTime.getTime());
    }

    public static void setCurrentTime(Date currentTime) {
        CurrentTimeFacade.currentTime = new Date(currentTime.getTime());
    }

    public static void reset() {
        currentTime = null;
    }
}

