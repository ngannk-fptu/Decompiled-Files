/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.datetime;

import java.util.Date;

public class RequestTimeThreadLocal {
    private static ThreadLocal<Long> requestTimeThreadLocal = new ThreadLocal();

    public static void setTime(long currentTimeMillis) {
        requestTimeThreadLocal.set(currentTimeMillis);
    }

    public static void clearTime() {
        requestTimeThreadLocal.set(null);
    }

    public static long getTime() {
        Long value = requestTimeThreadLocal.get();
        if (value == null) {
            return -1L;
        }
        return value;
    }

    public static Date getTimeOrNow() {
        long time = RequestTimeThreadLocal.getTime();
        if (time != -1L) {
            return new Date(time);
        }
        return new Date();
    }
}

